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

@Service
@RequiredArgsConstructor
@Slf4j
public class ContestAttemptServiceImpl implements ContestAttemptService {
    private final ContestServiceClient contestServiceClient;
    private final ContestAttemptRepository contestAttemptRepository;
    private final AttemptMapper attemptMapper;
    private final AssessmentEventProducer producer;


    @Override
    public AttemptResponse startContest(Long contestId , Long userId) {

        ContestDto contest;
        try {

            contest = contestServiceClient.getContestById(contestId);

        } catch (FeignException ex) {

            if (ex.status() == 404) {
                throw new ContestNotFoundException("Contest not found.");
            }

            throw ex;
        }

        Long participantId;
        try {
            participantId = contestServiceClient.getParticipantId(contestId, userId);
        } catch (FeignException e) {
            throw new IllegalStateException(e.getMessage());
        }

        if (contestAttemptRepository.existsByParticipantId(
                participantId)) {

            throw new IllegalStateException(
                    "Contest already started");
        }

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
                .expiresAt(LocalDateTime.now().plusSeconds(Long.parseLong(contest.getDurationInSeconds())))
                .build();

        contestAttemptRepository.save(attempt);

        contestServiceClient.setParticipantStatus(contestId , userId , ParticipantStatus.STARTED);

        return attemptMapper.toResponse(attempt);
    }

    @Override
    public AttemptResponse getAttempt(Long attemptId, Long userId) {
        return null;
    }

    @Override
    @Transactional
    public String submitContest(Long contestId, Long userId , SubmitRequestDto request) {

        Long participantId = contestServiceClient.getParticipantId(contestId, userId);

        ContestAttempt attempt =
                contestAttemptRepository.findByParticipantId(participantId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Attempt not found"));
        if (attempt.getStatus()
                != AttemptStatus.IN_PROGRESS) {

            throw new IllegalStateException(
                    "Attempt already submitted");
        }
        // Basic validation
        if (request.getAnswers() == null || request.getAnswers().isEmpty()) {
            throw new IllegalArgumentException("No answers submitted.");
        }

        AssessmentSubmittedEvent event =
                AssessmentSubmittedEvent.builder()
                        .contestId(contestId)
                        .participantId(participantId)
                        .userId(userId)
                        .answers(request.getAnswers())
                        .submittedAt(LocalDateTime.now())
                        .build();

        try {

            producer.publish(event).get(); // Wait for Kafka acknowledgement

            attempt.setStatus(AttemptStatus.SUBMITTED);
            contestAttemptRepository.save(attempt);

            return "Submission accepted. Evaluation started.";

        } catch (Exception e) {

            throw new RuntimeException("Failed to publish submission to Kafka", e);
        }

    }

    @Override
    public ContestResultResponse getResult(Long attemptId, Long userId) {
        return null;
    }

    @Override
    @Transactional
    public void updateEvaluation(Long participantId, EvaluationResultDto dto) {

        ContestAttempt attempt = contestAttemptRepository
                .findByParticipantId(participantId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Contest Attempt not found"));

        if (attempt.getStatus() == AttemptStatus.EVALUATED) {
            log.info("Evaluation already processed for participant {}", participantId);
            return;
        }

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
