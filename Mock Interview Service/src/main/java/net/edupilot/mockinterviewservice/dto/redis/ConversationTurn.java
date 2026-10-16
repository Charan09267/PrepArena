package net.edupilot.mockinterviewservice.dto.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConversationTurn implements Serializable {
    private String question;
    private String answer;
    private Integer questionNumber;
    private LocalDateTime timestamp;
}
