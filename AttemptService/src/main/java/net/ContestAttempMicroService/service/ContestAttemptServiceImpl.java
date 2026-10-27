package net.ContestAttempMicroService.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ContestAttempMicroService.Client.ContestServiceClient;
import net.ContestAttempMicroService.Mapper.AttemptMapper;
import net.ContestAttempMicroService.Repo.ContestAttemptRepository;
import net.ContestAttempMicroService.dto.*;
import net.ContestAttempMicroService.entity.AttemptStatus;
import net.ContestAttempMicroService.entity.ContestAttempt;
import net.ContestAttempMicroService.entity.ParticipantStatus;
import net.ContestAttempMicroService.kafka.AssessmentEventProducer;
import net.company.common.Exception.ContestNotFoundException;
import net.company.common.Exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContestAttemptServiceImpl implements ContestAttemptService {

    private final ContestServiceClient contestServiceClient;
    private final ContestAttemptRepository contestAttemptRepository;
    private final AttemptMapper attemptMapper;
    private final AssessmentEventProducer producer;

    /**
     * Starts a new attempt after verifying the contest and participant.
     *
     * @param contestId the ID of the contest
     * @param userId    the ID of the user starting the contest
     * @return the newly created attempt details
     */
    @Override
    public AttemptResponse startContest(Long contestId, Long userId) {

        // Retrieve contest details from the Contest Service.
        ContestDto contest;
        try {
            contest = contestServiceClient.getContestById(contestId);
        } catch (FeignException ex) {
            if (ex.status() == 404) {
                throw new ContestNotFoundException("Contest not found.");
            }

            // Propagate other downstream service errors.
            throw ex;
        }

        // Resolve the participant associated with this user and contest.
        Long participantId;
        try {
            participantId = contestServiceClient.getParticipantId(
                    contestId, userId
            );
        } catch (FeignException ex) {
            throw new IllegalStateException(ex.getMessage());
        }

        // Prevent creating another attempt for the same participant.
        if (contestAttemptRepository.existsByParticipantId(participantId)) {
            throw new IllegalStateException("Contest already started");
        }

        // Initialize the attempt with its starting status and default counters.
        ContestAttempt attempt = ContestAttempt.builder()
                .contestId(contestId)
                .participantId(participantId)
                .status(AttemptStatus.IN_PROGRESS)
                .score(BigDecimal.ZERO)
                .totalQuestions(contest.getTotalQuestions())
                .correctAnswers(0)
                .wrongAnswers(0)
                .unansweredQuestions(contest.getTotalQuestions())
                .startedAt(LocalDateTime.now())
                .expiresAt(
                        LocalDateTime.now().plusSeconds(
                                Long.parseLong(contest.getDurationInSeconds())
                        )
                )
                .build();

        // Persist the attempt before updating the participant's status.
        contestAttemptRepository.save(attempt);

        contestServiceClient.setParticipantStatus(
                contestId, userId, ParticipantStatus.STARTED
        );

        return attemptMapper.toResponse(attempt);
    }

    /**
     * Retrieves an attempt belonging to the specified user.
     *
     * TODO: Implement attempt lookup and ownership validation.
     *
     * @param attemptId the ID of the attempt
     * @param userId    the ID of the requesting user
     * @return the attempt details
     */
    @Override
    public AttemptResponse getAttempt(Long attemptId, Long userId) {
        return null;
    }

    /**
     * Publishes a submission event for asynchronous evaluation.
     * The attempt is marked as submitted after Kafka acknowledges publication.
     *
     * @param contestId the ID of the contest
     * @param userId    the ID of the submitting user
     * @param request   the submitted answers
     * @return a message confirming that submission was accepted
     */
    @Override
    @Transactional
    public String submitContest(
            Long contestId,
            Long userId,
            SubmitRequestDto request) {

        // Resolve the participant before locating their attempt.
        Long participantId = contestServiceClient.getParticipantId(
                contestId, userId
        );

        ContestAttempt attempt = contestAttemptRepository
                .findByParticipantId(participantId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Attempt not found")
                );

        // Only an attempt currently in progress can be submitted.
        if (attempt.getStatus() != AttemptStatus.IN_PROGRESS) {
            throw new IllegalStateException("Attempt already submitted");
        }

        // Reject submissions that contain no answers.
        if (request.getAnswers() == null || request.getAnswers().isEmpty()) {
            throw new IllegalArgumentException("No answers submitted.");
        }

        // Build the event consumed by the evaluation workflow.
        AssessmentSubmittedEvent event = AssessmentSubmittedEvent.builder()
                .contestId(contestId)
                .participantId(participantId)
                .userId(userId)
                .answers(request.getAnswers())
                .submittedAt(LocalDateTime.now())
                .build();

        try {
            // Wait for Kafka publication acknowledgement before updating status.
            producer.publish(event).get();

            attempt.setStatus(AttemptStatus.SUBMITTED);
            contestAttemptRepository.save(attempt);

            return "Submission accepted. Evaluation started.";
        } catch (Exception ex) {
            throw new RuntimeException(
                    "Failed to publish submission to Kafka", ex
            );
        }
    }

    /**
     * Retrieves the evaluation result for a user's attempt.
     *
     * TODO: Implement result lookup and ownership validation.
     *
     * @param attemptId the ID of the attempt
     * @param userId    the ID of the requesting user
     * @return the contest result
     */
    @Override
    public ContestResultResponse getResult(Long attemptId, Long userId) {
        return null;
    }

    /**
     * Applies the evaluation results to an attempt.
     * Repeated updates are ignored once the attempt has been evaluated.
     *
     * @param participantId the ID of the participant
     * @param dto            the evaluation results
     */
    @Override
    @Transactional
    public void updateEvaluation(
            Long participantId,
            EvaluationResultDto dto) {

        ContestAttempt attempt = contestAttemptRepository
                .findByParticipantId(participantId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Contest Attempt not found"
                        )
                );

        // Avoid applying the same evaluation again to an evaluated attempt.
        if (attempt.getStatus() == AttemptStatus.EVALUATED) {
            log.info(
                    "Evaluation already processed for participant {}",
                    participantId
            );
            return;
        }

        // Update the attempt with the evaluation outcome.
        attempt.setScore(dto.getScore());
        attempt.setCorrectAnswers(dto.getCorrectAnswers());
        attempt.setWrongAnswers(dto.getWrongAnswers());
        attempt.setUnansweredQuestions(dto.getUnansweredQuestions());
        attempt.setTotalQuestions(dto.getTotalQuestions());
        attempt.setSubmittedAt(dto.getSubmittedAt());
        attempt.setStatus(AttemptStatus.EVALUATED);

        contestAttemptRepository.save(attempt);
    }
}
