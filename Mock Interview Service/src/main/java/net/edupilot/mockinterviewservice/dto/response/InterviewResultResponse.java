package net.edupilot.mockinterviewservice.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class InterviewResultResponse {
    private Long interviewId;
    private BigDecimal overallScore;
    private LocalDateTime evaluatedAt;

}
