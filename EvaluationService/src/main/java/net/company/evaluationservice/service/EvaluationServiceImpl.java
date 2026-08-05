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

    @Override
    @Transactional
    public void evaluate(AssessmentSubmittedEvent event) {

        log.info("Fetching answer key for contest {}", event.getContestId());

        List<AnswerKeyDto> answerKey =
                contestFeignClient.getAnswerKey(event.getContestId());

        log.info("Answer key fetched. Questions={}", answerKey.size());

        EvaluationResult result = calculateResult(event, answerKey);

        log.info("Calculated Result = {}", result);

        attemptFeignClient.updateEvaluation(
                event.getParticipantId(),
                result
        );

        log.info("Attempt service updated successfully");
    }

    @Transactional
    private EvaluationResult calculateResult(
            AssessmentSubmittedEvent event,
            List<AnswerKeyDto> answerKey
    ) {

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

        for (AnswerDto submitted : event.getAnswers()) {

            AnswerKeyDto key =
                    answerKeyMap.get(submitted.getQuestionId());

            if (key == null) {
                continue;
            }

            if (isCorrect(submitted, key)) {

                correct++;

                score = score.add(
                        BigDecimal.valueOf(key.getMarks())
                );

            } else {

                wrong++;

            }

        }

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


    private boolean isCorrect(
            AnswerDto submitted,
            AnswerKeyDto key
    ) {

        List<Long> selected =
                Optional.ofNullable(submitted.getOptionId())
                        .orElse(Collections.emptyList());

        List<Long> correct =
                Optional.ofNullable(key.getCorrectOptionIds())
                        .orElse(Collections.emptyList());

        return new HashSet<>(selected)
                .equals(new HashSet<>(correct));

    }

}