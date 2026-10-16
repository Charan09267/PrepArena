package net.edupilot.aiservice.dto;

import lombok.Data;

@Data
public class ConversationTurnDto {

    private Integer questionNumber;

    private String question;

    private String answer;
}