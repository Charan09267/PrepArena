package net.edupilot.mockinterviewservice.dto.response;

import net.edupilot.mockinterviewservice.enums.InterviewStatus;

public class StartInterviewResponse {

    private Long interviewId;
    private InterviewStatus status;
    private String firstQuestion;
    private Integer questionsAsked;
    private Integer questionLimit;

    public Long getInterviewId() {
        return interviewId;
    }

    public void setInterviewId(Long interviewId) {
        this.interviewId = interviewId;
    }

    public InterviewStatus getStatus() {
        return status;
    }

    public void setStatus(InterviewStatus status) {
        this.status = status;
    }

    public String getFirstQuestion() {
        return firstQuestion;
    }

    public void setFirstQuestion(String firstQuestion) {
        this.firstQuestion = firstQuestion;
    }

    public Integer getQuestionsAsked() {
        return questionsAsked;
    }

    public void setQuestionsAsked(Integer questionsAsked) {
        this.questionsAsked = questionsAsked;
    }

    public Integer getQuestionLimit() {
        return questionLimit;
    }

    public void setQuestionLimit(Integer questionLimit) {
        this.questionLimit = questionLimit;
    }
}