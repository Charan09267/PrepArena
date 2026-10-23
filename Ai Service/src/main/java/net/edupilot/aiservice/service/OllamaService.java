package net.edupilot.aiservice.service;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

/**
 * Service providing a simple interface for generating text using the configured
 * Ollama-backed language model.
 *
 * <p>Delegates prompt processing to Spring AI's ChatModel abstraction.</p>
 */
@Service
public class OllamaService {

    private final ChatModel chatModel;

    /**
     * Creates the service with the configured chat model.
     *
     * @param chatModel model used to generate AI responses
     */
    public OllamaService(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    /**
     * Generates a text response for the supplied prompt.
     *
     * @param prompt input instructions or text sent to the language model
     * @return generated text response
     */
    public String generateResponse(String prompt) {
        return chatModel.call(prompt);
    }
}