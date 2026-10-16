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

    @Override
    public InterviewResponse createInterview(CreateInterviewRequest request) {

        Interview interview = new Interview();

        // Temporary value until JWT integration
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

        Interview savedInterview = interviewRepository.save(interview);

        return mapToResponse(savedInterview);
    }

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

    @Override
    public InterviewResponse getInterview(Long interviewId) {

        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() ->
                        new RuntimeException("Interview not found"));

        return mapToResponse(interview);
    }

    @Override
    public StartInterviewResponse startInterview(Long interviewId) {

        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() ->
                        new RuntimeException("Interview not found"));

        if (interview.getStatus() != InterviewStatus.CREATED) {
            throw new RuntimeException(
                    "Interview cannot be started in current state"
            );
        }

        LocalDateTime startedAt = LocalDateTime.now();

        interview.setStatus(InterviewStatus.IN_PROGRESS);
        interview.setStartedAt(startedAt);

        Interview savedInterview = interviewRepository.save(interview);

        IntialQuestionRequest aiRequest =
                new IntialQuestionRequest();

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

        if (aiResponse == null ||
                aiResponse.getQuestion() == null ||
                aiResponse.getQuestion().isBlank()) {

            throw new RuntimeException(
                    "AI Service failed to generate initial question"
            );
        }

        String firstQuestion = aiResponse.getQuestion();
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

        ConversationTurn firstTurn = new ConversationTurn();

        firstTurn.setQuestion(firstQuestion);
        firstTurn.setAnswer(null);
        firstTurn.setQuestionNumber(1);
        firstTurn.setTimestamp(LocalDateTime.now());

        context.getConversationHistory().add(firstTurn);

        interviewRedisService.saveContext(context);

        StartInterviewResponse response = new StartInterviewResponse();

        response.setInterviewId(savedInterview.getId());
        response.setStatus(savedInterview.getStatus());
        response.setFirstQuestion(firstQuestion);
        response.setQuestionsAsked(context.getQuestionsAsked());
        response.setQuestionLimit(savedInterview.getQuestionLimit());

        return response;
    }


    @Override
    public List<InterviewSummaryResponse> getMyInterviews() {

        // Temporary user ID until JWT integration
        Long userId = 1L;

        return interviewRepository.findAllByUserId(userId)
                .stream()
                .map(this::mapToSummaryResponse)
                .toList();
    }

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

    @Override
    public SubmitAnswerResponse submitAnswer(
            Long interviewId,
            SubmitAnswerRequest request
    ) {

        InterviewContext context =
                interviewRedisService.getContext(interviewId);

        if (context == null) {
            throw new RuntimeException("Interview session not found or expired");
        }

        List<ConversationTurn> history =
                context.getConversationHistory();

        if (history.isEmpty()) {
            throw new RuntimeException("Interview conversation is empty");
        }

        ConversationTurn currentTurn =
                history.get(history.size() - 1);

        if (currentTurn.getAnswer() != null) {
            throw new RuntimeException("Current question has already been answered");
        }

        // Save answer
        currentTurn.setAnswer(request.getAnswer());


        // Check whether this was the final question
        if (context.getQuestionsAsked() >= context.getQuestionLimit()) {

            completeInterview(interviewId, context);

            SubmitAnswerResponse response = new SubmitAnswerResponse();

            response.setInterviewId(interviewId);
            response.setQuestionsAsked(context.getQuestionsAsked());
            response.setQuestionLimit(context.getQuestionLimit());
            response.setInterviewCompleted(true);

            interviewRedisService.saveContext(context);

            return response;
        }


        // Generate next question
        String nextQuestion =
                generateTemporaryQuestion(context);

        int nextQuestionNumber =
                context.getQuestionsAsked() + 1;

        ConversationTurn nextTurn = new ConversationTurn();

        nextTurn.setQuestion(nextQuestion);
        nextTurn.setAnswer(null);
        nextTurn.setQuestionNumber(nextQuestionNumber);
        nextTurn.setTimestamp(LocalDateTime.now());

        history.add(nextTurn);

        context.setQuestionsAsked(nextQuestionNumber);
        context.setCurrentQuestion(nextQuestion);

        interviewRedisService.saveContext(context);


        SubmitAnswerResponse response =
                new SubmitAnswerResponse();

        response.setInterviewId(interviewId);
        response.setNextQuestion(nextQuestion);
        response.setQuestionsAsked(nextQuestionNumber);
        response.setQuestionLimit(context.getQuestionLimit());
        response.setInterviewCompleted(false);

        return response;
    }

    private String generateTemporaryQuestion(InterviewContext context) {

        NextQuestionRequest aiRequest = new NextQuestionRequest();

        aiRequest.setTargetRole(context.getTargetRole());
        aiRequest.setExperienceLevel(context.getExperienceLevel());
        aiRequest.setInterviewInstructions(context.getInterviewInstructions());
        aiRequest.setQuestionLimit(context.getQuestionLimit());
        aiRequest.setQuestionsAsked(context.getQuestionsAsked());
        aiRequest.setDurationMinutes(context.getDurationMinutes());
        aiRequest.setConversationHistory(context.getConversationHistory());

        NextQuestionResponse aiResponse =
                aiServiceClient.generateNextQuestion(aiRequest);

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

    private void completeInterview(
            Long interviewId,
            InterviewContext context
    ) {

        Interview interview = interviewRepository
                .findById(interviewId)
                .orElseThrow(() ->
                        new RuntimeException("Interview not found"));

        interview.setStatus(InterviewStatus.EVALUATING);
        interview.setEndedAt(LocalDateTime.now());

        interviewRepository.save(interview);
    }

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

        if (interview.getStatus() != InterviewStatus.EVALUATING) {
            throw new RuntimeException(
                    "Interview is not ready for evaluation");
        }

        // Convert completed Redis conversation into Q&A pairs.
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

        EvaluationRequest request = new EvaluationRequest();

        request.setInterviewType("NORMAL");
        request.setRole(context.getTargetRole());
        request.setTopic(context.getInterviewInstructions());
        request.setQuestions(questions);

        // Obtain the complete report from the AI Service.
        EvaluationResponse evaluation =
                aiServiceClient.evaluateInterview(request);

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

        // Persist only the overall score, as in your existing design.
        InterviewResult result = new InterviewResult();

        result.setInterviewId(interviewId);
        result.setOverallScore(BigDecimal.valueOf(evaluation.getOverallScore()));
        result.setEvaluatedAt(LocalDateTime.now());

        interviewResultRepository.save(result);

        interview.setStatus(InterviewStatus.COMPLETED);
        interviewRepository.save(interview);

        // Return the report before removing temporary conversation data.
        interviewRedisService.deleteContext(interviewId);

        return evaluation;
    }



}
