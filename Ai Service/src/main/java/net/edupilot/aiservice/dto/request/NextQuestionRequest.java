package net.edupilot.aiservice.dto.request;

import lombok.Data;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import net.edupilot.aiservice.dto.ConversationTurnDto;

import java.util.List;

@Data
public class NextQuestionRequest {

    @NotBlank
    private String targetRole;

    @NotBlank
    private String experienceLevel;

    private String interviewInstructions;

    @NotNull
    @Min(1)
    private Integer questionLimit;

    @NotNull
    @Min(1)
    private Integer questionsAsked;

    @NotNull
    @Min(1)
    private Integer durationMinutes;

    @NotEmpty
    @Valid
    private List<ConversationTurnDto> conversationHistory;
}