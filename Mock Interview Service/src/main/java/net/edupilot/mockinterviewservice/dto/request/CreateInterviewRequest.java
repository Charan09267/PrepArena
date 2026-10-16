package net.edupilot.mockinterviewservice.dto.request;


import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.edupilot.mockinterviewservice.enums.InterviewType;

@Getter @Setter
@NoArgsConstructor
public class CreateInterviewRequest {

    @NotNull
    private InterviewType type;

    @NotBlank
    private String targetRole;

    @NotBlank
    private String experienceLevel;

    @NotNull
    @Min(5)
    @Max(120)
    private Integer durationMinutes;

    @NotNull
    @Min(1)
    @Max(50)
    private Integer questionLimit;

    private String interviewInstructions;
    private String programmingLanguage;

    private String difficulty;

    private String topic;

}
