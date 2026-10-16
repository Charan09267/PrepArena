package net.edupilot.mockinterviewservice.dto.request;

import lombok.Data;
import net.edupilot.mockinterviewservice.dto.redis.ConversationTurn;

import java.util.List;

@Data
public class NextQuestionRequest {

    private String targetRole;
    private String experienceLevel;
    private String interviewInstructions;
    private Integer questionLimit;
    private Integer questionsAsked;
    private Integer durationMinutes;
    private List<ConversationTurn> conversationHistory;

}
