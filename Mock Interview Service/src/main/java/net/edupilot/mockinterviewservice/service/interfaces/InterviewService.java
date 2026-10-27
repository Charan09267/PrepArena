package net.edupilot.mockinterviewservice.service.interfaces;

import net.edupilot.mockinterviewservice.dto.ai.EvaluationResponse;
import net.edupilot.mockinterviewservice.dto.request.CreateInterviewRequest;
import net.edupilot.mockinterviewservice.dto.request.SubmitAnswerRequest;
import net.edupilot.mockinterviewservice.dto.response.*;

import java.util.List;


/**
 * Defines the business operations supported by the mock interview service.
 *
 * <p>Implementations manage interview creation, execution, answer submission,
 * result retrieval, and evaluation.</p>
 */
public interface InterviewService {

    /**
     * Creates an interview using the supplied configuration.
     *
     * @param request interview configuration
     * @return details of the created interview
     */
    InterviewResponse createInterview(CreateInterviewRequest request);

    /**
     * Retrieves an interview by its identifier.
     *
     * @param interviewId unique identifier of the interview
     * @return interview details
     */
    InterviewResponse getInterview(Long interviewId);

    /**
     * Starts an interview and obtains its initial question.
     *
     * @param interviewId unique identifier of the interview
     * @return interview start details, including the first question
     */
    StartInterviewResponse startInterview(Long interviewId);

    /**
     * Retrieves summary information for the current user's interviews.
     *
     * @return list of interview summaries
     */
    List<InterviewSummaryResponse> getMyInterviews();

    /**
     * Submits an answer to the current interview question.
     *
     * @param interviewId unique identifier of the interview
     * @param request submitted answer
     * @return next-question details or the interview completion status
     */
    SubmitAnswerResponse submitAnswer(
            Long interviewId,
            SubmitAnswerRequest request
    );

    /**
     * Retrieves the persisted result of an evaluated interview.
     *
     * @param interviewId unique identifier of the interview
     * @return interview result details
     */
    InterviewResultResponse getInterviewResult(Long interviewId);

    /**
     * Evaluates a completed interview.
     *
     * @param interviewId unique identifier of the interview
     * @return evaluation report
     */
    EvaluationResponse evaluateInterview(Long interviewId);
}
