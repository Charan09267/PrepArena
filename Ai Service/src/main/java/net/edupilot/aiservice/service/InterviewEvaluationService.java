package net.edupilot.aiservice.service;

import lombok.RequiredArgsConstructor;
import net.edupilot.aiservice.dto.request.InterviewEvaluationRequest;
import net.edupilot.aiservice.dto.response.InterviewEvaluationResponse;
import net.edupilot.aiservice.exception.AiServiceException;
import net.edupilot.aiservice.prompt.InterviewEvaluationPromptBuilder;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InterviewEvaluationService {

    private final ChatClient chatClient;
    private final InterviewEvaluationPromptBuilder promptBuilder;


    public InterviewEvaluationResponse evaluateInterview(
            InterviewEvaluationRequest request) {

        try {
            String prompt = promptBuilder.buildPrompt(request);

            return chatClient
                    .prompt()
                    .user(prompt)
                    .call()
                    .entity(InterviewEvaluationResponse.class);

        } catch (Exception exception) {
            throw new AiServiceException(
                    "Failed to evaluate interview",
                    exception
            );
        }
    }

}
