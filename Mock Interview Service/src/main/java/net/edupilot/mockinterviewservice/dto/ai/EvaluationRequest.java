package net.edupilot.mockinterviewservice.dto.ai;

import lombok.Data;
import net.edupilot.mockinterviewservice.dto.redis.ConversationTurn;

import java.util.List;

@Data
public class EvaluationRequest {

    private String interviewType;

    private String role;

    private String topic;

    private List<InterviewQuestionAnswer> questions;
}
