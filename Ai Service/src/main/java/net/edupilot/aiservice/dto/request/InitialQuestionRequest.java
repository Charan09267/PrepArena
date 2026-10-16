package net.edupilot.aiservice.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InitialQuestionRequest {

    private String targetRole;

    private String experienceLevel;

    private String interviewInstructions;

    private Integer questionLimit;

    private Integer durationMinutes;
}
