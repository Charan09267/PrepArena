package net.edupilot.aiservice.dto.request;

import jakarta.validation.Valid;
import lombok.Data;
import net.edupilot.aiservice.dto.InterviewQuestionAnswer;

import java.util.List;

@Data
public class InterviewEvaluationRequest {

    private String interviewType;

    private String role;

    private String topic;

    @Valid
    private List<InterviewQuestionAnswer> questions;
}
