package net.ContestAttempMicroService.service;

import net.ContestAttempMicroService.dto.AttemptResponse;
import net.ContestAttempMicroService.dto.ContestResultResponse;
import net.ContestAttempMicroService.dto.EvaluationResultDto;
import net.ContestAttempMicroService.dto.SubmitRequestDto;

public interface ContestAttemptService {

    /**
     * Creates and starts an attempt for a participant in a contest.
     *
     * @param contestId the ID of the contest
     * @param userId    the ID of the user
     * @return the created attempt details
     */
    AttemptResponse startContest(Long contestId, Long userId);

    /**
     * Retrieves an attempt belonging to the specified user.
     *
     * @param attemptId the ID of the attempt
     * @param userId    the ID of the user requesting the attempt
     * @return the attempt details
     */
    AttemptResponse getAttempt(Long attemptId, Long userId);

    /**
     * Submits contest answers for asynchronous evaluation.
     *
     * @param contestId        the ID of the contest
     * @param userId           the ID of the submitting user
     * @param submitRequestDto the submitted answers and related request data
     * @return a message describing the submission outcome
     */
    String submitContest(
            Long contestId,
            Long userId,
            SubmitRequestDto submitRequestDto
    );

    /**
     * Retrieves the evaluation result for a user's attempt.
     *
     * @param attemptId the ID of the attempt
     * @param userId    the ID of the user requesting the result
     * @return the contest result
     */
    ContestResultResponse getResult(Long attemptId, Long userId);

    /**
     * Persists evaluation results for a participant's attempt.
     *
     * @param participantId the ID of the participant
     * @param dto            the evaluation result data
     */
    void updateEvaluation(Long participantId, EvaluationResultDto dto);
}

