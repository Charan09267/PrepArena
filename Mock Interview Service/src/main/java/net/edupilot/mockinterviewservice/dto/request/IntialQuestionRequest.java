package net.edupilot.mockinterviewservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class IntialQuestionRequest {
    private String targetRole;
    private String experienceLevel;
    private String interviewInstructions;
    private Integer questionLimit;
    private Integer durationMinutes;
}
