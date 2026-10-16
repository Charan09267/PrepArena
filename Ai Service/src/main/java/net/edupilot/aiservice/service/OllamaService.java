package net.edupilot.aiservice.service;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

@Service
public class OllamaService {

    private final ChatModel chatModel;

    public OllamaService(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public String generateResponse(String prompt) {
        return chatModel.call(prompt);
    }
}
