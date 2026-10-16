package net.edupilot.mockinterviewservice.service.interfaces;

import net.edupilot.mockinterviewservice.dto.redis.InterviewContext;

public interface InterviewRedisService {

    void saveContext(InterviewContext context);

    InterviewContext getContext(Long interviewId);

    void deleteContext(Long interviewId);
}