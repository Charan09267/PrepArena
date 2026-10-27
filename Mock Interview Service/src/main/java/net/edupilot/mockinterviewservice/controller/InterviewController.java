package net.edupilot.mockinterviewservice.controller;

import jakarta.validation.Valid;
import net.edupilot.mockinterviewservice.dto.ai.EvaluationResponse;
import net.edupilot.mockinterviewservice.dto.request.CreateInterviewRequest;
import net.edupilot.mockinterviewservice.dto.request.SubmitAnswerRequest;
import net.edupilot.mockinterviewservice.dto.response.*;
import net.edupilot.mockinterviewservice.service.interfaces.InterviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * REST controller responsible for managing mock interview operations.
 *
 * <p>Delegates interview creation, retrieval, execution, answer submission,
 * and evaluation to the {@link InterviewService}.</p>
 */
@RestController
@RequestMapping("/interviews")
public class InterviewController {

    private final InterviewService interviewService;

    public InterviewController(InterviewService interviewService) {
        this.interviewService = interviewService;
    }

    /**
     * Creates a new mock interview.
     *
     * @param request interview configuration supplied by the client
     * @return the created interview with HTTP status 201 (Created)
     */
    @PostMapping
    public ResponseEntity<InterviewResponse> createInterview(
            @Valid @RequestBody CreateInterviewRequest request) {

        InterviewResponse response =
                interviewService.createInterview(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * Retrieves an interview using its ID.
     *
     * @param interviewId unique identifier of the interview
     * @return the requested interview
     */
    @GetMapping("/{interviewId}")
    public ResponseEntity<InterviewResponse> getInterview(
            @PathVariable Long interviewId) {

        return ResponseEntity.ok(
                interviewService.getInterview(interviewId)
        );
    }

    /**
     * Starts an interview and obtains its initial question.
     *
     * @param interviewId unique identifier of the interview
     * @return the interview start details and first question
     */
    @PostMapping("/{interviewId}/start")
    public ResponseEntity<StartInterviewResponse> startInterview(
            @PathVariable Long interviewId) {

        return ResponseEntity.ok(
                interviewService.startInterview(interviewId)
        );
    }

    /**
     * Retrieves the interviews associated with the current user.
     *
     * @return a list of interview summaries
     */
    @GetMapping
    public ResponseEntity<List<InterviewSummaryResponse>> getMyInterviews() {

        return ResponseEntity.ok(
                interviewService.getMyInterviews()
        );
    }

    /**
     * Submits an answer to the current interview question.
     *
     * @param interviewId unique identifier of the interview
     * @param request answer submitted by the client
     * @return the next question or the interview completion status
     */
    @PostMapping("/{interviewId}/answer")
    public ResponseEntity<SubmitAnswerResponse> submitAnswer(
            @PathVariable Long interviewId,
            @Valid @RequestBody SubmitAnswerRequest request) {

        return ResponseEntity.ok(
                interviewService.submitAnswer(interviewId, request)
        );
    }

    /**
     * Retrieves the persisted result of an evaluated interview.
     *
     * @param interviewId unique identifier of the interview
     * @return the interview result
     */
    @GetMapping("/{interviewId}/result")
    public ResponseEntity<InterviewResultResponse> getInterviewResult(
            @PathVariable Long interviewId) {

        return ResponseEntity.ok(
                interviewService.getInterviewResult(interviewId)
        );
    }

    /**
     * Evaluates a completed interview.
     *
     * @param interviewId unique identifier of the interview
     * @return the evaluation report returned by the service
     */
    @PostMapping("/{interviewId}/evaluate")
    public ResponseEntity<EvaluationResponse> evaluateInterview(
            @PathVariable Long interviewId) {

        return ResponseEntity.ok(
                interviewService.evaluateInterview(interviewId)
        );
    }
}
