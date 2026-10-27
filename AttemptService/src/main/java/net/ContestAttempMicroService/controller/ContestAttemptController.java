package net.ContestAttempMicroService.controller;


import lombok.RequiredArgsConstructor;
import net.ContestAttempMicroService.dto.*;
import net.ContestAttempMicroService.service.ContestAttemptService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/attempts")
public class ContestAttemptController {

    private final ContestAttemptService contestAttemptService;

    /**
     * Starts a contest attempt for the authenticated user.
     *
     * @param contestId the ID of the contest to start
     * @param userId    the ID of the user starting the contest
     * @return the newly created attempt details
     */
    @PostMapping("/{contestId}/start")
    public ResponseEntity<AttemptResponse> startContest(
            @PathVariable Long contestId,
            @RequestHeader("X-USER-ID") Long userId) {

        return ResponseEntity.ok(
                contestAttemptService.startContest(contestId, userId)
        );
    }

    /**
     * Submits the user's answers for evaluation.
     *
     * @param userId  the ID of the submitting user
     * @param request the contest submission request containing answers
     * @return a message indicating the submission result
     */
    @PostMapping("/submit")
    public ResponseEntity<String> submitContest(
            @RequestHeader("X-USER-ID") Long userId,
            @RequestBody SubmitRequestDto request) {

        return ResponseEntity.ok(
                contestAttemptService.submitContest(
                        request.getContestId(), userId, request
                )
        );
    }

    /**
     * Retrieves the details of a specific attempt.
     *
     * @param attemptId the ID of the attempt
     * @param userId    the ID of the requesting user
     * @return the attempt details
     */
    @GetMapping("/{attemptId}")
    public ResponseEntity<AttemptResponse> getAttempt(
            @PathVariable Long attemptId,
            @RequestHeader("X-USER-ID") Long userId) {

        return ResponseEntity.ok(
                contestAttemptService.getAttempt(attemptId, userId)
        );
    }

    /*
     * Previously available result endpoint.
     * Kept commented out as in the original implementation.
     */
    // @GetMapping("/{attemptId}/result")
    // public ResponseEntity<ContestResultResponse> getResult(
    //         @PathVariable Long attemptId,
    //         @RequestHeader("X-USER-ID") Long userId) {
    //
    //     return ResponseEntity.ok(
    //             contestAttemptService.getResult(attemptId, userId)
    //     );
    // }

    /**
     * Updates an attempt with the evaluation results.
     *
     * @param participantId the ID of the participant whose attempt was evaluated
     * @param dto            the evaluation results produced by the evaluation service
     * @return an empty response indicating the update request completed
     */
    @PutMapping("/{participantId}/evaluation")
    public ResponseEntity<Void> updateEvaluation(
            @PathVariable Long participantId,
            @RequestBody EvaluationResultDto dto) {

        contestAttemptService.updateEvaluation(participantId, dto);
        return ResponseEntity.ok().build();
    }
}

