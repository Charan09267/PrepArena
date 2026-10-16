package net.edupilot.aiservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InterviewQuestion {

    private int questionNumber;
    private String question;
}
