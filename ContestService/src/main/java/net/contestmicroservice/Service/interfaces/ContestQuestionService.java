package net.contestmicroservice.Service.interfaces;

import net.contestmicroservice.dto.request.CreateQuestionRequest;
import net.contestmicroservice.dto.response.QuestionResponse;

import java.util.List;


public interface ContestQuestionService {

    /**
     * Adds a question to a contest.
     *
     * @param contestId the ID of the contest
     * @param request   the question creation details
     * @return the created question
     */
    QuestionResponse addQuestion(
            Long contestId,
            CreateQuestionRequest request
    );

    /**
     * Retrieves all questions belonging to a contest.
     *
     * @param contestId the ID of the contest
     * @return the list of questions
     */
    List<QuestionResponse> getQuestions(Long contestId);

    /**
     * Updates an existing question.
     *
     * @param questionId the ID of the question
     * @param request    the updated question details
     * @return the updated question
     */
    QuestionResponse updateQuestion(
            Long questionId,
            CreateQuestionRequest request
    );

    /**
     * Deletes a question by its ID.
     *
     * @param questionId the ID of the question to delete
     */
    void deleteQuestion(Long questionId);
}

