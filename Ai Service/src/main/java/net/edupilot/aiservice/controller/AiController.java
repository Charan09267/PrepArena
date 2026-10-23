package net.edupilot.aiservice.controller;

import jakarta.validation.Valid;
import net.edupilot.aiservice.dto.request.InitialQuestionRequest;
import net.edupilot.aiservice.dto.request.InterviewEvaluationRequest;
import net.edupilot.aiservice.dto.response.InitialQuestionResponse;
import net.edupilot.aiservice.dto.response.InterviewEvaluationResponse;
import net.edupilot.aiservice.service.InterviewEvaluationService;
import net.edupilot.aiservice.service.InterviewServiceImpl;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller that exposes AI-powered endpoints for mock interviews.
 *
 * <p>Provides endpoints to generate the initial interview question,
 * generate subsequent questions, and evaluate the completed interview.</p>
 */
@RestController
@RequestMapping("/ai/v1")
public class AiController {

    private final InterviewEvaluationService evaluationService;
    private final InterviewServiceImpl generationService;

    /**
     * Creates the controller with the services required for interview
     * question generation and interview evaluation.
     *
     * @param evaluationService service responsible for evaluating interviews
     * @param generationService service responsible for generating interview questions
     */
    public AiController(
            InterviewEvaluationService evaluationService,
            InterviewServiceImpl generationService) {

        this.evaluationService = evaluationService;
        this.generationService = generationService;
    }

    /**
     * Evaluates an interview using the submitted interview details and responses.
     *
     * @param request request containing the information required for evaluation
     * @return structured evaluation containing the interview assessment
     */
    @PostMapping("/interviews/evaluate")
    public InterviewEvaluationResponse evaluateInterview(
            @Valid @RequestBody InterviewEvaluationRequest request) {

        return evaluationService.evaluateInterview(request);
    }

    /**
     * Generates the first question for a new mock interview.
     *
     * @param request request containing the interview configuration and context
     * @return response containing the initial interview question and related details
     */
    @PostMapping("/initial-question")
    public InitialQuestionResponse generateInitialQuestion(
            @RequestBody InitialQuestionRequest request) {

        return interviewService.generateInitialQuestion(request);
    }

    /**
     * Generates the next interview question based on the current interview context.
     *
     * @param request request containing the interview context and question-limit information
     * @return response containing the next generated question
     */
    @PostMapping("/next-question")
    public NextQuestionResponse generateNextQuestion(
            @Valid @RequestBody NextQuestionRequest request) {

        return interviewService.generateNextQuestion(request);
    }
}