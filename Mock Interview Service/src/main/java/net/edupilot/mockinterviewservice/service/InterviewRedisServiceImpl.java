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


/**
 * Redis-backed implementation for managing temporary interview context.
 *
 * <p>Interview context contains session information and conversation history
 * required while an interview is in progress. This data is stored temporarily
 * and expires automatically after two hours.</p>
 */
@Service
public class InterviewRedisServiceImpl implements InterviewRedisService {

    private static final String KEY_PREFIX = "interview:context:";

    private final RedisTemplate<String, Object> redisTemplate;

    public InterviewRedisServiceImpl(
            RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Stores an interview context in Redis with a two-hour expiration.
     *
     * @param context temporary state and conversation history of the interview
     */
    @Override
    public void saveContext(InterviewContext context) {

        String key = KEY_PREFIX + context.getInterviewId();

        redisTemplate.opsForValue().set(
                key,
                context,
                Duration.ofHours(2)
        );
    }

    /**
     * Retrieves an interview context from Redis.
     *
     * <p>The stored value may already be an {@link InterviewContext} or may
     * be represented as a map after deserialization. The latter is converted
     * back into the application's context model.</p>
     *
     * @param interviewId unique identifier of the interview
     * @return the reconstructed context, or {@code null} if no value exists
     */
    @Override
    public InterviewContext getContext(Long interviewId) {

        String key = KEY_PREFIX + interviewId;

        Object value = redisTemplate
                .opsForValue()
                .get(key);

        // A missing key can indicate that the session expired or was deleted.
        if (value == null) {
            return null;
        }

        // Use the stored object directly when Redis returns the expected type.
        if (value instanceof InterviewContext context) {
            return context;
        }

        // Reconstruct the context when deserialization produces a map.
        Map<String, Object> data = (Map<String, Object>) value;

        return mapToInterviewContext(data);
    }

    /**
     * Removes the temporary context associated with an interview.
     *
     * @param interviewId unique identifier of the interview
     */
    @Override
    public void deleteContext(Long interviewId) {

        String key = KEY_PREFIX + interviewId;

        redisTemplate.delete(key);
    }

    /**
     * Converts a deserialized map into an {@link InterviewContext}.
     *
     * @param data map containing the stored interview context fields
     * @return reconstructed interview context
     */
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

        // Restore the interview start time when the stored value is available.
        Object startedAt = data.get("startedAt");

        if (startedAt != null) {
            context.setStartedAt(
                    LocalDateTime.parse(startedAt.toString())
            );
        }

        // Rebuild the conversation history from its deserialized representation.
        context.setConversationHistory(
                mapToConversationHistory(
                        (List<Map<String, Object>>)
                                data.get("conversationHistory")
                )
        );

        return context;
    }

    /**
     * Converts serialized conversation turns into domain objects.
     *
     * @param turns list of serialized conversation turns
     * @return reconstructed conversation history, or an empty list if absent
     */
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

            // Restore the timestamp only when one was stored.
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
