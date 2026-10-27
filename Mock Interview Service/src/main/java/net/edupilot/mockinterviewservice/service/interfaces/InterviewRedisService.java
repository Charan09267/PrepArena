package net.edupilot.mockinterviewservice.service.interfaces;

import net.edupilot.mockinterviewservice.dto.redis.InterviewContext;


/**
 * Defines operations for storing and managing temporary interview context
 * in Redis.
 *
 * <p>The context contains the session state and conversation history needed
 * while a mock interview is in progress.</p>
 */
public interface InterviewRedisService {

    /**
     * Saves or updates the temporary context of an interview.
     *
     * @param context interview context to store
     */
    void saveContext(InterviewContext context);

    /**
     * Retrieves the temporary context for an interview.
     *
     * @param interviewId unique identifier of the interview
     * @return the interview context, or {@code null} if no context exists
     */
    InterviewContext getContext(Long interviewId);

    /**
     * Deletes the temporary context associated with an interview.
     *
     * @param interviewId unique identifier of the interview
     */
    void deleteContext(Long interviewId);
}
