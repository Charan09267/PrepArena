package net.edupilot.mockinterviewservice.service.interfaces;

import net.edupilot.mockinterviewservice.dto.ai.EvaluationResponse;
import net.edupilot.mockinterviewservice.dto.request.CreateInterviewRequest;
import net.edupilot.mockinterviewservice.dto.request.SubmitAnswerRequest;
import net.edupilot.mockinterviewservice.dto.response.*;

import java.util.List;

public interface InterviewService {
    InterviewResponse createInterview(CreateInterviewRequest request);

    InterviewResponse getInterview(Long interviewId);

    StartInterviewResponse startInterview(Long interviewId);

    List<InterviewSummaryResponse> getMyInterviews();

    SubmitAnswerResponse submitAnswer(
            Long interviewId,
            SubmitAnswerRequest request
    );

    InterviewResultResponse getInterviewResult(Long interviewId);

    EvaluationResponse evaluateInterview(Long interviewId);
}
