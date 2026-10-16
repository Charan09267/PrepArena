package net.edupilot.aiservice.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class InterviewGenerationRequest {
    @NotBlank(message = "Role is required")
    private String role;

    @NotBlank(message = "Topic is required")
    private String topic;

    @Min(value = 1, message = "Number of questions must be at least 1")
    @Max(value = 20, message = "Number of questions cannot exceed 20")
    private int numberOfQuestions;

    @Min(value = 1, message = "Time limit must be at least 1 minute")
    @Max(value = 120, message = "Time limit cannot exceed 120 minutes")
    private int timeLimit;

    private String customPrompt;
}
