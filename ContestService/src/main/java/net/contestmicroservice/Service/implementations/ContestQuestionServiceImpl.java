package net.contestmicroservice.Service.implementations;

import lombok.RequiredArgsConstructor;
import net.company.common.Exception.ResourceNotFoundException;
import net.contestmicroservice.Mapper.QuestionMapper;
import net.contestmicroservice.Repo.ContestRepo;
import net.contestmicroservice.Repo.QuestionRepo;
import net.contestmicroservice.Service.interfaces.ContestQuestionService;
import net.contestmicroservice.dto.request.CreateQuestionRequest;
import net.contestmicroservice.dto.response.QuestionResponse;
import net.contestmicroservice.entity.Contest;
import net.contestmicroservice.entity.ContestOption;
import net.contestmicroservice.entity.ContestQuestion;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ContestQuestionServiceImpl implements ContestQuestionService {

    private final QuestionRepo questionRepository;
    private final ContestRepo contestRepository;
    private final QuestionMapper questionMapper;

    /**
     * Adds a question and its options to a contest.
     *
     * @param contestId the ID of the contest
     * @param request   the question details and associated options
     * @return the created question
     */
    @Override
    public QuestionResponse addQuestion(
            Long contestId,
            CreateQuestionRequest request) {

        // Retrieve the contest to which the question will belong.
        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Contest not found")
                );

        // Build the question using the supplied request data.
        ContestQuestion question = ContestQuestion.builder()
                .contest(contest)
                .questionText(request.getQuestionText())
                .questionType(request.getQuestionType())
                .marks(request.getMarks())
                .explanation(request.getExplanation())
                .orderIndex(request.getOrderIndex())
                .build();

        // Create the options and associate each one with its question.
        List<ContestOption> options = request.getOptions()
                .stream()
                .map(optionRequest ->
                        ContestOption.builder()
                                .question(question)
                                .optionText(optionRequest.getOptionText())
                                .isCorrect(optionRequest.getIsCorrect())
                                .build()
                )
                .toList();

        // Attach the options before persisting the question.
        question.getOptions().addAll(options);

        ContestQuestion savedQuestion = questionRepository.save(question);

        return questionMapper.toResponse(savedQuestion);
    }

    /**
     * Retrieves a contest's questions in their configured order.
     *
     * @param contestId the ID of the contest
     * @return the ordered list of questions
     */
    @Override
    public List<QuestionResponse> getQuestions(Long contestId) {

        List<ContestQuestion> questions =
                questionRepository.findByContestIdOrderByOrderIndex(contestId);

        return questions.stream()
                .map(questionMapper::toResponse)
                .toList();
    }

    /**
     * Updates a question and replaces its existing options.
     *
     * @param questionId the ID of the question to update
     * @param request    the updated question details and options
     * @return the updated question
     */
    @Override
    public QuestionResponse updateQuestion(
            Long questionId,
            CreateQuestionRequest request) {

        // Retrieve the question or fail if it does not exist.
        ContestQuestion question = questionRepository.findById(questionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Question not found")
                );

        // Apply the updated question fields.
        question.setQuestionText(request.getQuestionText());
        question.setQuestionType(request.getQuestionType());
        question.setMarks(request.getMarks());
        question.setExplanation(request.getExplanation());
        question.setOrderIndex(request.getOrderIndex());

        // Replace the existing options with the newly supplied options.
        question.getOptions().clear();

        List<ContestOption> newOptions = request.getOptions()
                .stream()
                .map(optionRequest ->
                        ContestOption.builder()
                                .question(question)
                                .optionText(optionRequest.getOptionText())
                                .isCorrect(optionRequest.getIsCorrect())
                                .build()
                )
                .toList();

        question.getOptions().addAll(newOptions);

        ContestQuestion updatedQuestion = questionRepository.save(question);

        return questionMapper.toResponse(updatedQuestion);
    }

    /**
     * Deletes a question by its ID.
     *
     * @param questionId the ID of the question to delete
     */
    @Override
    public void deleteQuestion(Long questionId) {

        ContestQuestion question = questionRepository.findById(questionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Question not found")
                );

        questionRepository.delete(question);
    }
}
