package net.edupilot.mockinterviewservice.client;

import net.edupilot.mockinterviewservice.dto.ai.EvaluationRequest;
import net.edupilot.mockinterviewservice.dto.ai.EvaluationResponse;
import net.edupilot.mockinterviewservice.dto.ai.NextQuestionResponse;
import net.edupilot.mockinterviewservice.dto.request.IntialQuestionRequest;
import net.edupilot.mockinterviewservice.dto.request.NextQuestionRequest;
import net.edupilot.mockinterviewservice.dto.response.IntialQuestionResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;


/**
 * REST-based implementation of {@link AiServiceClient}.
 *
 * <p>Sends HTTP POST requests to the configured AI service and converts
 * the JSON responses into the corresponding response DTOs.</p>
 */
@Component
public class AiServiceClientImpl implements AiServiceClient {

    private final RestClient restClient;
    private final String aiServiceUrl;

    public AiServiceClientImpl(
            RestClient restClient,
            @Value("${services.ai-service.url}") String aiServiceUrl) {

        this.restClient = restClient;
        this.aiServiceUrl = aiServiceUrl;
    }

    /**
     * Requests the AI service to generate the first interview question.
     *
     * @param request parameters required to generate the initial question
     * @return initial-question response
     */
    @Override
    public IntialQuestionResponse generateInitialQuestion(
            IntialQuestionRequest request) {

        return restClient
                .post()
                .uri(aiServiceUrl + "/ai/interviews/normal/initial-question")
                .body(request)
                .retrieve()
                .body(IntialQuestionResponse.class);
    }

    /**
     * Sends the interview responses to the AI service for evaluation.
     *
     * @param request evaluation input containing interview answers
     * @return evaluation report
     */
    @Override
    public EvaluationResponse evaluateInterview(
            EvaluationRequest request) {

        return restClient
                .post()
                .uri(aiServiceUrl + "/ai/interviews/evaluate")
                .body(request)
                .retrieve()
                .body(EvaluationResponse.class);
    }

    /**
     * Requests the next interview question using the current conversation.
     *
     * @param request parameters and conversation history for question generation
     * @return next-question response
     */
    @Override
    public NextQuestionResponse generateNextQuestion(
            NextQuestionRequest request) {

        return restClient
                .post()
                .uri(aiServiceUrl + "/ai/interviews/normal/next-question")
                .body(request)
                .retrieve()
                .body(NextQuestionResponse.class);
    }
}
