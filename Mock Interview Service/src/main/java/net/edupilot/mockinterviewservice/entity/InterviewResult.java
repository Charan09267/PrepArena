package net.edupilot.mockinterviewservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "interview_results",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_interview_result_interview",
                        columnNames = "interview_id"
                )
        }
)
@Getter
@Setter
public class InterviewResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "interview_id", nullable = false)
    private Long interviewId;

    @Column(name = "overall_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal overallScore;

    @Column(name = "evaluated_at", nullable = false)
    private LocalDateTime evaluatedAt;
}
