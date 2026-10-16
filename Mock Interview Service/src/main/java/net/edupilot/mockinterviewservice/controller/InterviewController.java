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

@RestController
@RequestMapping("/interviews")
public class InterviewController {

    private final InterviewService interviewService;

    public InterviewController(InterviewService interviewService) {
        this.interviewService = interviewService;
    }

    @PostMapping
    public ResponseEntity<InterviewResponse> createInterview(
            @Valid @RequestBody CreateInterviewRequest request) {

        InterviewResponse response = interviewService.createInterview(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{interviewId}")
    public ResponseEntity<InterviewResponse> getInterview(
            @PathVariable Long interviewId) {

        return ResponseEntity.ok(
                interviewService.getInterview(interviewId)
        );
    }

    @PostMapping("/{interviewId}/start")
    public ResponseEntity<StartInterviewResponse> startInterview(
            @PathVariable Long interviewId) {

        return ResponseEntity.ok(
                interviewService.startInterview(interviewId)
        );
    }

    @GetMapping
    public ResponseEntity<List<InterviewSummaryResponse>> getMyInterviews() {

        return ResponseEntity.ok(
                interviewService.getMyInterviews()
        );
    }

    @PostMapping("/{interviewId}/answer")
    public ResponseEntity<SubmitAnswerResponse> submitAnswer(
            @PathVariable Long interviewId,
            @Valid @RequestBody SubmitAnswerRequest request
    ) {

        return ResponseEntity.ok(
                interviewService.submitAnswer(interviewId, request)
        );
    }

    @GetMapping("/{interviewId}/result")
    public ResponseEntity<InterviewResultResponse> getInterviewResult(
            @PathVariable Long interviewId
    ) {

        return ResponseEntity.ok(
                interviewService.getInterviewResult(interviewId)
        );
    }

    @PostMapping("/{interviewId}/evaluate")
    public ResponseEntity<EvaluationResponse> evaluateInterview(
            @PathVariable Long interviewId
    ) {

        return ResponseEntity.ok(
                interviewService.evaluateInterview(interviewId)
        );
    }
}
