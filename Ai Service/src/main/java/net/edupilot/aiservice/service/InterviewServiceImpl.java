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

/**
 * Implements AI-powered interview question generation.
 *
 * <p>Generates the initial question for an interview and subsequent questions
 * based on the supplied interview context, using Spring AI to communicate
 * with the configured language model.</p>
 */
@Service
@RequiredArgsConstructor
public class InterviewServiceImpl implements InterviewService {

    private final ChatClient chatClient;
    private final InterviewGenerationPromptBuilder interviewGenerationPromptBuilder;
    private final ObjectMapper objectMapper;
    private final InitialQuestionPromptBuilder initialPromptBuilder;

    /**
     * Generates the next question for an ongoing mock interview.
     *
     * @param request current interview context and question-limit information
     * @return response containing the generated next question
     * @throws IllegalArgumentException if the configured question limit has been reached
     * @throws AiServiceException if the AI model fails to produce a usable question
     */
    public NextQuestionResponse generateNextQuestion(
            NextQuestionRequest request) {

        // Prevent question generation when the interview has reached its limit.
        if (request.getQuestionsAsked() >= request.getQuestionLimit()) {
            throw new IllegalArgumentException(
                    "No questions remaining for this interview"
            );
        }

        // Build a prompt using the current interview context to guide question generation.
        String prompt = interviewGenerationPromptBuilder.buildNextQuestionPrompt(request);

        // Invoke the configured AI model through Spring AI and retrieve its textual response.
        String question = chatClient
                .prompt()
                .user(prompt)
                .call()
                .content();

        // Reject empty responses so the application does not return an invalid question.
        if (question == null || question.isBlank()) {
            throw new AiServiceException(
                    "AI failed to generate the next question"
            );
        }

        // Remove surrounding whitespace before returning the generated question.
        return new NextQuestionResponse(question.trim());
    }

    /**
     * Generates the first question for a new mock interview.
     *
     * @param request interview configuration used to determine the initial question
     * @return structured response containing the initial interview question and metadata
     * @throws AiServiceException if the AI model fails to generate or map the response
     */
    public InitialQuestionResponse generateInitialQuestion(
            InitialQuestionRequest request) {

        // Build the initial-question prompt from the supplied interview configuration.
        String prompt = initialPromptBuilder.build(request);

        try {
            // Request the initial question and map the model output to the response DTO.
            return chatClient
                    .prompt()
                    .user(prompt)
                    .call()
                    .entity(InitialQuestionResponse.class);

        } catch (Exception e) {
            // Convert model invocation and response-mapping failures into a domain-specific exception.
            throw new AiServiceException(
                    "Failed to generate initial interview question", e);
        }
    }
}
