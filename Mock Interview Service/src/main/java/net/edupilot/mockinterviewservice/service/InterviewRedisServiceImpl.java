package net.edupilot.mockinterviewservice.service;


import net.edupilot.mockinterviewservice.dto.redis.ConversationTurn;
import net.edupilot.mockinterviewservice.dto.redis.InterviewContext;
import net.edupilot.mockinterviewservice.enums.InterviewType;
import net.edupilot.mockinterviewservice.service.interfaces.InterviewRedisService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class InterviewRedisServiceImpl implements InterviewRedisService {

    private static final String KEY_PREFIX = "interview:context:";

    private final RedisTemplate<String, Object> redisTemplate;

    public InterviewRedisServiceImpl(
            RedisTemplate<String, Object> redisTemplate) {

        this.redisTemplate = redisTemplate;
    }

    @Override
    public void saveContext(InterviewContext context) {

        String key = KEY_PREFIX + context.getInterviewId();

        redisTemplate.opsForValue().set(
                key,
                context,
                Duration.ofHours(2)
        );
    }

    @Override
    public InterviewContext getContext(Long interviewId) {

        String key = KEY_PREFIX + interviewId;

        Object value =  redisTemplate
                .opsForValue()
                .get(key);

        if( value == null) return null;

        if( value instanceof InterviewContext context) return context;

        Map<String, Object> data = (Map<String, Object>) value;

        return mapToInterviewContext(data);
    }

    @Override
    public void deleteContext(Long interviewId) {

        String key = KEY_PREFIX + interviewId;

        redisTemplate.delete(key);
    }

    private InterviewContext mapToInterviewContext(
            Map<String, Object> data) {

        InterviewContext context = new InterviewContext();

        context.setInterviewId(
                ((Number) data.get("interviewId")).longValue()
        );

        context.setUserId(
                ((Number) data.get("userId")).longValue()
        );

        context.setType(
                InterviewType.valueOf(data.get("type").toString())
        );

        context.setTargetRole((String) data.get("targetRole"));
        context.setExperienceLevel((String) data.get("experienceLevel"));
        context.setInterviewInstructions(
                (String) data.get("interviewInstructions")
        );

        context.setDurationMinutes(
                ((Number) data.get("durationMinutes")).intValue()
        );

        context.setQuestionLimit(
                ((Number) data.get("questionLimit")).intValue()
        );

        context.setQuestionsAsked(
                ((Number) data.get("questionsAsked")).intValue()
        );

        context.setCurrentQuestion((String) data.get("currentQuestion"));

        Object startedAt = data.get("startedAt");

        if (startedAt != null) {
            context.setStartedAt(
                    LocalDateTime.parse(startedAt.toString())
            );
        }

        context.setConversationHistory(
                mapToConversationHistory(
                        (List<Map<String, Object>>)
                                data.get("conversationHistory")
                )
        );

        return context;
    }

    private List<ConversationTurn> mapToConversationHistory(
            List<Map<String, Object>> turns) {

        List<ConversationTurn> history = new ArrayList<>();

        if (turns == null) {
            return history;
        }

        for (Map<String, Object> turn : turns) {

            ConversationTurn conversationTurn = new ConversationTurn();

            conversationTurn.setQuestion(
                    (String) turn.get("question")
            );

            conversationTurn.setAnswer(
                    (String) turn.get("answer")
            );

            conversationTurn.setQuestionNumber(
                    ((Number) turn.get("questionNumber")).intValue()
            );

            Object timestamp = turn.get("timestamp");

            if (timestamp != null) {
                conversationTurn.setTimestamp(
                        LocalDateTime.parse(timestamp.toString())
                );
            }

            history.add(conversationTurn);
        }

        return history;
    }
}
