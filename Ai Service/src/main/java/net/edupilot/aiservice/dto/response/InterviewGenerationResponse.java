package net.edupilot.aiservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.edupilot.aiservice.dto.InterviewQuestion;

import java.util.List;

@Data
@AllArgsConstructor
public class InterviewGenerationResponse {

    private List<InterviewQuestion> questions;
}
