package net.edupilot.mockinterviewservice.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.edupilot.mockinterviewservice.enums.InterviewStatus;
import net.edupilot.mockinterviewservice.enums.InterviewType;

import java.time.LocalDateTime;

@Entity
@Table(name = "interviews")
@Getter
@Setter
@NoArgsConstructor
public class Interview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InterviewType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InterviewStatus status;

    @Column(name = "target_role", length = 100)
    private String targetRole;

    @Column(name = "experience_level", length = 50)
    private String experienceLevel;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Column(name = "question_limit", nullable = false)
    private Integer questionLimit;

    @Column(name = "programming_language", length = 50)
    private String programmingLanguage;

    @Column(length = 30)
    private String difficulty;

    @Column(length = 100)
    private String topic;

    @Column(name = "interview_instructions", columnDefinition = "TEXT")
    private String interviewInstructions;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

}
