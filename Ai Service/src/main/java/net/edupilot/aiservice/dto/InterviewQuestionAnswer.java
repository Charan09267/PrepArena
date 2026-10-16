package net.edupilot.aiservice.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class InterviewQuestionAnswer {

    @NotBlank(message = "Question cannot be empty")
    private String question;

    @NotBlank(message = "Answer cannot be empty")
    private String answer;
}