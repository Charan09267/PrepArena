package net.edupilot.mockinterviewservice.dto.response;



import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.edupilot.mockinterviewservice.enums.InterviewStatus;
import net.edupilot.mockinterviewservice.enums.InterviewType;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class InterviewResponse {

    private Long id;
    private InterviewType type;
    private InterviewStatus status;

    private String targetRole;
    private String experienceLevel;

    private Integer durationMinutes;
    private Integer questionLimit;

    private String programmingLanguage;
    private String difficulty;
    private String topic;

    private LocalDateTime createdAt;
}
