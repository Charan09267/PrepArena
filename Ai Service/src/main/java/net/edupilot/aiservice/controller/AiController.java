package net.edupilot.aiservice.controller;

import jakarta.validation.Valid;
import net.edupilot.aiservice.dto.request.InterviewEvaluationRequest;
import net.edupilot.aiservice.dto.response.InterviewEvaluationResponse;
import net.edupilot.aiservice.service.InterviewEvaluationService;
import net.edupilot.aiservice.service.InterviewServiceImpl;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai/v1")
public class AiController {

    private final InterviewEvaluationService evaluationService;
    private final InterviewServiceImpl generationService;

    public AiController(
            InterviewEvaluationService evaluationService,
            InterviewServiceImpl generationService) {

        this.evaluationService = evaluationService;
        this.generationService = generationService;
    }

    @PostMapping("/interviews/evaluate")
    public InterviewEvaluationResponse evaluateInterview(
            @Valid @RequestBody InterviewEvaluationRequest request) {

        return evaluationService.evaluateInterview(request);
    }

    @PostMapping("/initial-question")
    public InitialQuestionResponse generateInitialQuestion(
            @RequestBody InitialQuestionRequest request) {

        return interviewService.generateInitialQuestion(request);
    }


    @PostMapping("/next-question")
    public NextQuestionResponse generateNextQuestion(
            @Valid @RequestBody NextQuestionRequest request) {

        return interviewService.generateNextQuestion(request);

    }
}
