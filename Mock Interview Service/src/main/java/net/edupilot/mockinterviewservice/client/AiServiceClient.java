package net.edupilot.mockinterviewservice.client;

import net.edupilot.mockinterviewservice.dto.ai.EvaluationRequest;
import net.edupilot.mockinterviewservice.dto.ai.EvaluationResponse;
import net.edupilot.mockinterviewservice.dto.ai.NextQuestionResponse;
import net.edupilot.mockinterviewservice.dto.request.IntialQuestionRequest;
import net.edupilot.mockinterviewservice.dto.request.NextQuestionRequest;
import net.edupilot.mockinterviewservice.dto.response.IntialQuestionResponse;


/**
 * Defines the HTTP client operations used to communicate with the AI service.
 *
 * <p>The client delegates initial-question generation, follow-up question
 * generation, and interview evaluation to the AI service.</p>
 */
public interface AiServiceClient {

    /**
     * Requests the first question for a new interview.
     *
     * @param request initial-question generation parameters
     * @return response containing the generated initial question
     */
    IntialQuestionResponse generateInitialQuestion(
            IntialQuestionRequest request
    );

    /**
     * Requests an evaluation of the interview responses.
     *
     * @param request interview details and question-answer pairs
     * @return evaluation report returned by the AI service
     */
    EvaluationResponse evaluateInterview(
            EvaluationRequest request
    );

    /**
     * Requests the next question based on the current conversation.
     *
     * @param request next-question generation parameters and conversation history
     * @return response containing the generated follow-up question
     */
    NextQuestionResponse generateNextQuestion(
            NextQuestionRequest request
    );
}

