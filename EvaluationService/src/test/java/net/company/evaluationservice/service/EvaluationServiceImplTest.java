package net.company.evaluationservice.service;

import net.company.evaluationservice.Client.AttemptFeignClient;
import net.company.evaluationservice.Client.ContestFeignClient;
import net.company.evaluationservice.dto.AnswerDto;
import net.company.evaluationservice.dto.AnswerKeyDto;
import net.company.evaluationservice.dto.AssessmentSubmittedEvent;
import net.company.evaluationservice.dto.EvaluationResult;
import net.company.evaluationservice.entity.QuestionType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EvaluationServiceImplTest {

    @Mock
    private ContestFeignClient contestFeignClient;

    @Mock
    private AttemptFeignClient attemptFeignClient;

    @InjectMocks
    private EvaluationServiceImpl evaluationService;

    @Test
    void shouldEvaluateAllAnswersCorrectly() {

        // Arrange
        AssessmentSubmittedEvent event = AssessmentSubmittedEvent.builder()
                .contestId(1L)
                .participantId(10L)
                .userId(100L)
                .submittedAt(LocalDateTime.now())
                .answers(List.of(
                        new AnswerDto(1L, List.of(11L)),
                        new AnswerDto(2L, List.of(21L))
                ))
                .build();

        List<AnswerKeyDto> answerKey = List.of(
                AnswerKeyDto.builder()
                        .questionId(1L)
                        .correctOptionIds(List.of(11L))
                        .marks(5)
                        .questionType(QuestionType.MCQ)
                        .build(),

                AnswerKeyDto.builder()
                        .questionId(2L)
                        .correctOptionIds(List.of(21L))
                        .marks(10)
                        .questionType(QuestionType.MCQ)
                        .build()
        );

        when(contestFeignClient.getAnswerKey(1L))
                .thenReturn(answerKey);

        // Act
        evaluationService.evaluate(event);

        // Assert
        ArgumentCaptor<EvaluationResult> captor =
                ArgumentCaptor.forClass(EvaluationResult.class);

        verify(attemptFeignClient).updateEvaluation(anyLong() , captor.capture());

        EvaluationResult result = captor.getValue();

        assertEquals(BigDecimal.valueOf(15), result.getScore());
        assertEquals(2, result.getCorrectAnswers());
        assertEquals(0, result.getWrongAnswers());
        assertEquals(0, result.getUnansweredQuestions());
        assertEquals(2, result.getTotalQuestions());

        assertEquals(1L, result.getContestId());
        assertEquals(10L, result.getParticipantId());
        assertEquals(100L, result.getUserId());
    }
}