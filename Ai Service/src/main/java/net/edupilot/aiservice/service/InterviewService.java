package net.edupilot.aiservice.service;

import net.edupilot.aiservice.dto.request.InitialQuestionRequest;
import net.edupilot.aiservice.dto.response.InitialQuestionResponse;
import net.edupilot.aiservice.dto.request.NextQuestionRequest;
import net.edupilot.aiservice.dto.response.NextQuestionResponse;

public interface InterviewService {
    public InitialQuestionResponse generateInitialQuestion(
            InitialQuestionRequest request);

    public NextQuestionResponse generateNextQuestion(
            NextQuestionRequest request);
}
