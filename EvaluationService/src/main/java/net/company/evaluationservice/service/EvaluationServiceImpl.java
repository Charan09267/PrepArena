package net.company.evaluationservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.company.evaluationservice.Client.AttemptFeignClient;
import net.company.evaluationservice.Client.ContestFeignClient;
import net.company.evaluationservice.dto.AnswerDto;
import net.company.evaluationservice.dto.AnswerKeyDto;
import net.company.evaluationservice.dto.AssessmentSubmittedEvent;
import net.company.evaluationservice.dto.EvaluationResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class EvaluationServiceImpl implements EvaluationService {

    private final ContestFeignClient contestFeignClient;
    private final AttemptFeignClient attemptFeignClient;

    /**
     * Evaluates a submitted assessment and sends the result to the Attempt Service.
     *
     * @param event submitted assessment details
     */
    @Override
    @Transactional
    public void evaluate(AssessmentSubmittedEvent event) {

        log.info(
                "Fetching answer key for contest {}",
                event.getContestId()
        );

        // Retrieve the correct answers for the submitted contest.
        List<AnswerKeyDto> answerKey =
                contestFeignClient.getAnswerKey(event.getContestId());

        log.info(
                "Answer key fetched. Questions={}",
                answerKey.size()
        );

        // Calculate the participant's score using the submitted answers.
        EvaluationResult result = calculateResult(event, answerKey);

        log.info("Calculated evaluation result: {}", result);

        // Send the calculated result to the Attempt Service.
        attemptFeignClient.updateEvaluation(
                event.getParticipantId(),
                result
        );

        log.info("Attempt Service updated successfully");
    }

    /**
     * Calculates the score and answer statistics for a submitted assessment.
     *
     * @param event     submitted assessment details
     * @param answerKey official answer key for the contest
     * @return calculated evaluation result
     */
    @Transactional
    private EvaluationResult calculateResult(
            AssessmentSubmittedEvent event,
            List<AnswerKeyDto> answerKey
    ) {

        // Index the answer key by question ID for efficient lookups.
        Map<Long, AnswerKeyDto> answerKeyMap =
                answerKey.stream()
                        .collect(Collectors.toMap(
                                AnswerKeyDto::getQuestionId,
                                Function.identity()
                        ));

        BigDecimal score = BigDecimal.ZERO;

        int correct = 0;
        int wrong = 0;

        int totalQuestions = answerKey.size();

        // Compare each submitted answer with the corresponding correct answer.
        for (AnswerDto submitted : event.getAnswers()) {

            AnswerKeyDto key =
                    answerKeyMap.get(submitted.getQuestionId());

            // Ignore answers that do not belong to this answer key.
            if (key == null) {
                continue;
            }

            if (isCorrect(submitted, key)) {

                correct++;

                // Add the question's marks when the answer is correct.
                score = score.add(
                        BigDecimal.valueOf(key.getMarks())
                );

            } else {
                wrong++;
            }
        }

        // Calculate unanswered questions using the submitted answer count.
        int unanswered = totalQuestions - event.getAnswers().size();

        return EvaluationResult.builder()
                .contestId(event.getContestId())
                .participantId(event.getParticipantId())
                .userId(event.getUserId())
                .score(score)
                .correctAnswers(correct)
                .wrongAnswers(wrong)
                .unansweredQuestions(unanswered)
                .totalQuestions(totalQuestions)
                .submittedAt(event.getSubmittedAt())
                .build();
    }

    /**
     * Checks whether the selected option IDs match the correct option IDs.
     *
     * @param submitted participant's submitted answer
     * @param key       official answer key for the question
     * @return true if both option ID sets match
     */
    private boolean isCorrect(
            AnswerDto submitted,
            AnswerKeyDto key
    ) {

        // Treat a null selection as an empty list.
        List<Long> selected =
                Optional.ofNullable(submitted.getOptionId())
                        .orElse(Collections.emptyList());

        // Treat a missing correct-option list as an empty list.
        List<Long> correct =
                Optional.ofNullable(key.getCorrectOptionIds())
                        .orElse(Collections.emptyList());

        // Set comparison ignores option order and duplicate IDs.
        return new HashSet<>(selected)
                .equals(new HashSet<>(correct));
    }
}