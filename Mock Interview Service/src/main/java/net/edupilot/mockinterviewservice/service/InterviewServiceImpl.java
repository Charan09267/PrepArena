package net.edupilot.mockinterviewservice.service;


import lombok.RequiredArgsConstructor;
import net.edupilot.mockinterviewservice.client.AiServiceClient;
import net.edupilot.mockinterviewservice.dto.ai.EvaluationRequest;
import net.edupilot.mockinterviewservice.dto.ai.EvaluationResponse;
import net.edupilot.mockinterviewservice.dto.ai.InterviewQuestionAnswer;
import net.edupilot.mockinterviewservice.dto.ai.NextQuestionResponse;
import net.edupilot.mockinterviewservice.dto.redis.ConversationTurn;
import net.edupilot.mockinterviewservice.dto.request.CreateInterviewRequest;
import net.edupilot.mockinterviewservice.dto.request.IntialQuestionRequest;
import net.edupilot.mockinterviewservice.dto.request.NextQuestionRequest;
import net.edupilot.mockinterviewservice.dto.request.SubmitAnswerRequest;
import net.edupilot.mockinterviewservice.dto.response.*;
import net.edupilot.mockinterviewservice.dto.redis.InterviewContext;
import net.edupilot.mockinterviewservice.entity.Interview;
import net.edupilot.mockinterviewservice.entity.InterviewResult;
import net.edupilot.mockinterviewservice.enums.InterviewStatus;
import net.edupilot.mockinterviewservice.enums.InterviewType;
import net.edupilot.mockinterviewservice.repository.InterviewRepository;
import net.edupilot.mockinterviewservice.repository.InterviewResultRepository;
import net.edupilot.mockinterviewservice.service.interfaces.InterviewRedisService;
import net.edupilot.mockinterviewservice.service.interfaces.InterviewService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class InterviewServiceImpl implements InterviewService {

    private final InterviewRepository interviewRepository;
    private final InterviewRedisService interviewRedisService;
    private final InterviewResultRepository interviewResultRepository;
    private final AiServiceClient aiServiceClient;

    /**
     * Creates an interview using the configuration supplied by the client.
     *
     * @param request interview configuration
     * @return response containing the saved interview details
     */
    @Override
    public InterviewResponse createInterview(CreateInterviewRequest request) {

        Interview interview = new Interview();

        // Temporary user ID until JWT integration is completed.
        interview.setUserId(1L);

        interview.setType(request.getType());
        interview.setStatus(InterviewStatus.CREATED);

        interview.setTargetRole(request.getTargetRole());
        interview.setExperienceLevel(request.getExperienceLevel());

        interview.setDurationMinutes(request.getDurationMinutes());
        interview.setQuestionLimit(request.getQuestionLimit());

        interview.setProgrammingLanguage(request.getProgrammingLanguage());
        interview.setDifficulty(request.getDifficulty());
        interview.setTopic(request.getTopic());

        interview.setCreatedAt(LocalDateTime.now());

        // Persist the interview configuration before returning its response.
        Interview savedInterview = interviewRepository.save(interview);

        return mapToResponse(savedInterview);
    }

    /**
     * Maps an interview entity to its API response representation.
     *
     * @param interview persisted interview entity
     * @return interview response DTO
     */
    private InterviewResponse mapToResponse(Interview interview) {

        InterviewResponse response = new InterviewResponse();

        response.setId(interview.getId());
        response.setType(interview.getType());
        response.setStatus(interview.getStatus());

        response.setTargetRole(interview.getTargetRole());
        response.setExperienceLevel(interview.getExperienceLevel());

        response.setDurationMinutes(interview.getDurationMinutes());
        response.setQuestionLimit(interview.getQuestionLimit());

        response.setProgrammingLanguage(interview.getProgrammingLanguage());
        response.setDifficulty(interview.getDifficulty());
        response.setTopic(interview.getTopic());

        response.setCreatedAt(interview.getCreatedAt());

        return response;
    }

    /**
     * Retrieves an interview by its identifier.
     *
     * @param interviewId unique interview identifier
     * @return interview response DTO
     * @throws RuntimeException if the interview does not exist
     */
    @Override
    public InterviewResponse getInterview(Long interviewId) {

        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() ->
                        new RuntimeException("Interview not found"));

        return mapToResponse(interview);
    }

    /**
     * Starts a created interview and generates its first question.
     *
     * <p>The interview status and start time are persisted, the initial
     * question is requested from the AI service, and the temporary interview
     * context is stored in Redis.</p>
     *
     * @param interviewId unique interview identifier
     * @return interview start details, including the first question
     */
    @Override
    public StartInterviewResponse startInterview(Long interviewId) {

        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() ->
                        new RuntimeException("Interview not found"));

        // Only interviews in the CREATED state can be started.
        if (interview.getStatus() != InterviewStatus.CREATED) {
            throw new RuntimeException(
                    "Interview cannot be started in current state"
            );
        }

        LocalDateTime startedAt = LocalDateTime.now();

        interview.setStatus(InterviewStatus.IN_PROGRESS);
        interview.setStartedAt(startedAt);

        // Save the state transition before requesting the initial question.
        Interview savedInterview = interviewRepository.save(interview);

        // Build the request using the saved interview configuration.
        IntialQuestionRequest aiRequest = new IntialQuestionRequest();

        aiRequest.setTargetRole(interview.getTargetRole());
        aiRequest.setExperienceLevel(interview.getExperienceLevel());
        aiRequest.setInterviewInstructions(
                interview.getInterviewInstructions()
        );
        aiRequest.setQuestionLimit(interview.getQuestionLimit());
        aiRequest.setDurationMinutes(
                interview.getDurationMinutes()
        );

        IntialQuestionResponse aiResponse =
                aiServiceClient.generateInitialQuestion(aiRequest);

        // Reject an empty initial question before creating the session context.
        if (aiResponse == null
                || aiResponse.getQuestion() == null
                || aiResponse.getQuestion().isBlank()) {

            throw new RuntimeException(
                    "AI Service failed to generate initial question"
            );
        }

        String firstQuestion = aiResponse.getQuestion();

        // Initialize the temporary state used throughout the interview session.
        InterviewContext context = new InterviewContext();

        context.setInterviewId(savedInterview.getId());
        context.setUserId(savedInterview.getUserId());
        context.setType(savedInterview.getType());

        context.setTargetRole(savedInterview.getTargetRole());
        context.setExperienceLevel(savedInterview.getExperienceLevel());
        context.setInterviewInstructions(
                savedInterview.getInterviewInstructions()
        );

        context.setDurationMinutes(savedInterview.getDurationMinutes());
        context.setQuestionLimit(savedInterview.getQuestionLimit());

        context.setQuestionsAsked(1);
        context.setCurrentQuestion(firstQuestion);
        context.setStartedAt(LocalDateTime.now());

        // Add the first question as an unanswered conversation turn.
        ConversationTurn firstTurn = new ConversationTurn();

        firstTurn.setQuestion(firstQuestion);
        firstTurn.setAnswer(null);
        firstTurn.setQuestionNumber(1);
        firstTurn.setTimestamp(LocalDateTime.now());

        context.getConversationHistory().add(firstTurn);

        // Store session state temporarily instead of persisting the conversation
        // history in the relational database.
        interviewRedisService.saveContext(context);

        StartInterviewResponse response = new StartInterviewResponse();

        response.setInterviewId(savedInterview.getId());
        response.setStatus(savedInterview.getStatus());
        response.setFirstQuestion(firstQuestion);
        response.setQuestionsAsked(context.getQuestionsAsked());
        response.setQuestionLimit(savedInterview.getQuestionLimit());

        return response;
    }

    /**
     * Retrieves summary information for the current user's interviews.
     *
     * @return list of interview summaries
     */
    @Override
    public List<InterviewSummaryResponse> getMyInterviews() {

        // Temporary user ID until JWT integration is completed.
        Long userId = 1L;

        return interviewRepository.findAllByUserId(userId)
                .stream()
                .map(this::mapToSummaryResponse)
                .toList();
    }

    /**
     * Maps an interview entity to its summary DTO.
     *
     * @param interview interview entity
     * @return interview summary response
     */
    private InterviewSummaryResponse mapToSummaryResponse(
            Interview interview) {

        InterviewSummaryResponse response =
                new InterviewSummaryResponse();

        response.setId(interview.getId());
        response.setType(interview.getType());
        response.setStatus(interview.getStatus());
        response.setTargetRole(interview.getTargetRole());
        response.setDurationMinutes(interview.getDurationMinutes());
        response.setQuestionLimit(interview.getQuestionLimit());
        response.setCreatedAt(interview.getCreatedAt());

        return response;
    }

    /**
     * Records the current answer and either completes the interview or
     * generates the next question.
     *
     * @param interviewId unique interview identifier
     * @param request submitted answer
     * @return next-question details or interview completion status
     */
    @Override
    public SubmitAnswerResponse submitAnswer(
            Long interviewId,
            SubmitAnswerRequest request) {

        InterviewContext context =
                interviewRedisService.getContext(interviewId);

        if (context == null) {
            throw new RuntimeException(
                    "Interview session not found or expired");
        }

        List<ConversationTurn> history =
                context.getConversationHistory();

        if (history.isEmpty()) {
            throw new RuntimeException("Interview conversation is empty");
        }

        // The last turn represents the question currently awaiting an answer.
        ConversationTurn currentTurn =
                history.get(history.size() - 1);

        // Prevent an answer from being submitted twice for the same question.
        if (currentTurn.getAnswer() != null) {
            throw new RuntimeException(
                    "Current question has already been answered");
        }

        // Attach the submitted answer to the current conversation turn.
        currentTurn.setAnswer(request.getAnswer());

        // If the question limit has been reached, move to evaluation.
        if (context.getQuestionsAsked() >= context.getQuestionLimit()) {

            completeInterview(interviewId, context);

            SubmitAnswerResponse response = new SubmitAnswerResponse();

            response.setInterviewId(interviewId);
            response.setQuestionsAsked(context.getQuestionsAsked());
            response.setQuestionLimit(context.getQuestionLimit());
            response.setInterviewCompleted(true);

            // Preserve the final answer in Redis for the evaluation step.
            interviewRedisService.saveContext(context);

            return response;
        }

        // Generate a follow-up question using the conversation so far.
        String nextQuestion = generateTemporaryQuestion(context);

        int nextQuestionNumber = context.getQuestionsAsked() + 1;

        // Add the next question as a new unanswered turn.
        ConversationTurn nextTurn = new ConversationTurn();

        nextTurn.setQuestion(nextQuestion);
        nextTurn.setAnswer(null);
        nextTurn.setQuestionNumber(nextQuestionNumber);
        nextTurn.setTimestamp(LocalDateTime.now());

        history.add(nextTurn);

        context.setQuestionsAsked(nextQuestionNumber);
        context.setCurrentQuestion(nextQuestion);

        // Save the updated history and question count for the next request.
        interviewRedisService.saveContext(context);

        SubmitAnswerResponse response = new SubmitAnswerResponse();

        response.setInterviewId(interviewId);
        response.setNextQuestion(nextQuestion);
        response.setQuestionsAsked(nextQuestionNumber);
        response.setQuestionLimit(context.getQuestionLimit());
        response.setInterviewCompleted(false);

        return response;
    }

    /**
     * Requests the next interview question from the AI service.
     *
     * @param context current interview context and conversation history
     * @return generated question text
     */
    private String generateTemporaryQuestion(InterviewContext context) {

        NextQuestionRequest aiRequest = new NextQuestionRequest();

        aiRequest.setTargetRole(context.getTargetRole());
        aiRequest.setExperienceLevel(context.getExperienceLevel());
        aiRequest.setInterviewInstructions(
                context.getInterviewInstructions());
        aiRequest.setQuestionLimit(context.getQuestionLimit());
        aiRequest.setQuestionsAsked(context.getQuestionsAsked());
        aiRequest.setDurationMinutes(context.getDurationMinutes());
        aiRequest.setConversationHistory(context.getConversationHistory());

        NextQuestionResponse aiResponse =
                aiServiceClient.generateNextQuestion(aiRequest);

        // Validate the generated question before returning it to the caller.
        String nextQuestion = aiResponse.getQuestion();

        if (aiResponse == null
                || aiResponse.getQuestion() == null
                || aiResponse.getQuestion().isBlank()) {

            throw new IllegalStateException(
                    "AI Service returned an empty next question"
            );
        }

        return aiResponse.getQuestion().trim();
    }

    /**
     * Marks an interview as ready for evaluation.
     *
     * @param interviewId unique interview identifier
     * @param context current temporary interview context
     */
    private void completeInterview(
            Long interviewId,
            InterviewContext context) {

        Interview interview = interviewRepository
                .findById(interviewId)
                .orElseThrow(() ->
                        new RuntimeException("Interview not found"));

        interview.setStatus(InterviewStatus.EVALUATING);
        interview.setEndedAt(LocalDateTime.now());

        interviewRepository.save(interview);
    }

    /**
     * Retrieves the persisted overall result for an evaluated interview.
     *
     * @param interviewId unique interview identifier
     * @return persisted interview result response
     */
    @Override
    public InterviewResultResponse getInterviewResult(Long interviewId) {

        InterviewResult result = interviewResultRepository
                .findByInterviewId(interviewId)
                .orElseThrow(() ->
                        new RuntimeException("Interview result not found"));

        InterviewResultResponse response =
                new InterviewResultResponse();

        response.setInterviewId(result.getInterviewId());
        response.setOverallScore(result.getOverallScore());
        response.setEvaluatedAt(result.getEvaluatedAt());

        return response;
    }

    /**
     * Evaluates the completed conversation and persists the overall score.
     *
     * <p>The service extracts answered question-answer pairs from Redis,
     * delegates evaluation to the AI service, validates the returned report,
     * saves the overall result, and marks the interview as completed.</p>
     *
     * @param interviewId unique interview identifier
     * @return complete evaluation report returned by the AI service
     */
    @Override
    public EvaluationResponse evaluateInterview(Long interviewId) {

        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() ->
                        new RuntimeException("Interview not found"));

        InterviewContext context =
                interviewRedisService.getContext(interviewId);

        if (context == null) {
            throw new RuntimeException(
                    "Interview session not found or expired");
        }

        // Evaluation is allowed only after the interview enters EVALUATING.
        if (interview.getStatus() != InterviewStatus.EVALUATING) {
            throw new RuntimeException(
                    "Interview is not ready for evaluation");
        }

        // Convert completed conversation turns into AI evaluation input.
        List<InterviewQuestionAnswer> questions =
                context.getConversationHistory()
                        .stream()
                        .filter(turn ->
                                turn.getQuestion() != null
                                        && !turn.getQuestion().isBlank()
                                        && turn.getAnswer() != null
                                        && !turn.getAnswer().isBlank())
                        .map(turn -> {
                            InterviewQuestionAnswer qa =
                                    new InterviewQuestionAnswer();

                            qa.setQuestion(turn.getQuestion());
                            qa.setAnswer(turn.getAnswer());

                            return qa;
                        })
                        .toList();

        if (questions.isEmpty()) {
            throw new RuntimeException(
                    "No completed question-answer pairs found");
        }

        // Build the evaluation request from the interview context and answers.
        EvaluationRequest request = new EvaluationRequest();

        request.setInterviewType("NORMAL");
        request.setRole(context.getTargetRole());
        request.setTopic(context.getInterviewInstructions());
        request.setQuestions(questions);

        // Obtain the evaluation report from the AI service.
        EvaluationResponse evaluation =
                aiServiceClient.evaluateInterview(request);

        // Verify that the response contains a valid score and a complete
        // question-level evaluation report.
        if (evaluation == null
                || evaluation.getOverallScore() < 0
                || evaluation.getOverallScore() > 10
                || evaluation.getOverallFeedback() == null
                || evaluation.getQuestionEvaluations() == null
                || evaluation.getQuestionEvaluations().size()
                != questions.size()) {

            throw new RuntimeException(
                    "AI evaluation returned an invalid or incomplete report");
        }

        // Persist only the overall score and evaluation timestamp.
        InterviewResult result = new InterviewResult();

        result.setInterviewId(interviewId);
        result.setOverallScore(
                BigDecimal.valueOf(evaluation.getOverallScore()));
        result.setEvaluatedAt(LocalDateTime.now());

        interviewResultRepository.save(result);

        // Mark the interview completed after successfully saving its result.
        interview.setStatus(InterviewStatus.COMPLETED);
        interviewRepository.save(interview);

        // Remove temporary conversation data after evaluation succeeds.
        interviewRedisService.deleteContext(interviewId);

        return evaluation;
    }
}
