package net.edupilot.mockinterviewservice.dto.response;

import lombok.Data;

@Data
public class SubmitAnswerResponse {

    private Long interviewId;
    private String nextQuestion;
    private Integer questionsAsked;
    private Integer questionLimit;
    private boolean interviewCompleted;
}
