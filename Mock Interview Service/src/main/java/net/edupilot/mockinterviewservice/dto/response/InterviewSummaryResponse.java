package net.edupilot.mockinterviewservice.dto.response;



import net.edupilot.mockinterviewservice.enums.InterviewStatus;
import net.edupilot.mockinterviewservice.enums.InterviewType;

import java.time.LocalDateTime;

public class InterviewSummaryResponse {

    private Long id;
    private InterviewType type;
    private InterviewStatus status;
    private String targetRole;
    private Integer durationMinutes;
    private Integer questionLimit;
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public InterviewType getType() {
        return type;
    }

    public void setType(InterviewType type) {
        this.type = type;
    }

    public InterviewStatus getStatus() {
        return status;
    }

    public void setStatus(InterviewStatus status) {
        this.status = status;
    }

    public String getTargetRole() {
        return targetRole;
    }

    public void setTargetRole(String targetRole) {
        this.targetRole = targetRole;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Integer getQuestionLimit() {
        return questionLimit;
    }

    public void setQuestionLimit(Integer questionLimit) {
        this.questionLimit = questionLimit;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}