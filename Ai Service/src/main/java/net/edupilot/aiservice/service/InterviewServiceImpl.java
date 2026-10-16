package net.edupilot.aiservice.service;

import lombok.RequiredArgsConstructor;
import net.edupilot.aiservice.dto.request.InitialQuestionRequest;
import net.edupilot.aiservice.dto.request.NextQuestionRequest;
import net.edupilot.aiservice.dto.response.InitialQuestionResponse;
import net.edupilot.aiservice.dto.response.NextQuestionResponse;
import net.edupilot.aiservice.exception.AiServiceException;
import net.edupilot.aiservice.prompt.InitialQuestionPromptBuilder;
import net.edupilot.aiservice.prompt.InterviewGenerationPromptBuilder;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class InterviewServiceImpl implements InterviewService {

    private final ChatClient chatClient;
    private final InterviewGenerationPromptBuilder interviewGenerationPromptBuilder;
    private final ObjectMapper objectMapper;
    private final InitialQuestionPromptBuilder initialPromptBuilder;

//    public InterviewGenerationResponse generateQuestions(
//            InterviewGenerationRequest request) throws AiServiceException {
//
//        try {
//            String prompt = promptBuilder.buildPrompt(request);
//
//            return chatClient
//                    .prompt()
//                    .user(prompt)
//                    .call()
//                    .entity(InterviewGenerationResponse.class);
//
//        } catch (Exception exception) {
//            throw new AiServiceException(
//                    "Failed to generate interview questions",
//                    exception
//            );
//        }
//    }

    public NextQuestionResponse generateNextQuestion(
            NextQuestionRequest request) {

        if (request.getQuestionsAsked() >= request.getQuestionLimit()) {
            throw new IllegalArgumentException(
                    "No questions remaining for this interview"
            );
        }

        String prompt = interviewGenerationPromptBuilder.buildNextQuestionPrompt(request);

        // Calls Ollama through Spring AI
        String question = chatClient
                .prompt()
                .user(prompt)
                .call()
                .content();

        if (question == null || question.isBlank()) {
            throw new AiServiceException(
                    "AI failed to generate the next question"
            );
        }

        return new NextQuestionResponse(question.trim());
    }

    public InitialQuestionResponse generateInitialQuestion(
            InitialQuestionRequest request) {

        String prompt = initialPromptBuilder.build(request);

        try {
            return chatClient
                    .prompt()
                    .user(prompt)
                    .call()
                    .entity(InitialQuestionResponse.class);

        } catch (Exception e) {
            throw new AiServiceException(
                    "Failed to generate initial interview question", e);
        }
    }
}
