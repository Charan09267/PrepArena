package net.edupilot.mockinterviewservice.client;

import net.edupilot.mockinterviewservice.dto.ai.EvaluationRequest;
import net.edupilot.mockinterviewservice.dto.ai.EvaluationResponse;
import net.edupilot.mockinterviewservice.dto.ai.NextQuestionResponse;
import net.edupilot.mockinterviewservice.dto.request.IntialQuestionRequest;
import net.edupilot.mockinterviewservice.dto.request.NextQuestionRequest;
import net.edupilot.mockinterviewservice.dto.response.IntialQuestionResponse;

public interface AiServiceClient {

    IntialQuestionResponse generateInitialQuestion(
            IntialQuestionRequest request
    );

    EvaluationResponse evaluateInterview(
            EvaluationRequest request
    );

    NextQuestionResponse generateNextQuestion(
            NextQuestionRequest request
    );
}
