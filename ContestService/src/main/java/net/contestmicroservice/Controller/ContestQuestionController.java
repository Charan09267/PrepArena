package net.contestmicroservice.Controller;

import lombok.RequiredArgsConstructor;
import net.contestmicroservice.Service.interfaces.ContestQuestionService;
import net.contestmicroservice.dto.request.CreateQuestionRequest;
import net.contestmicroservice.dto.response.QuestionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/contests")
@RequiredArgsConstructor
public class ContestQuestionController {

    private final ContestQuestionService questionService;

    /**
     * Adds a question to a contest.
     *
     * @param contestId the ID of the contest
     * @param request   the question details, including its options
     * @return the created question
     */
    @PostMapping("/{contestId}/questions")
    public ResponseEntity<QuestionResponse> addQuestion(
            @PathVariable Long contestId,
            @RequestBody CreateQuestionRequest request) {

        return ResponseEntity.ok(
                questionService.addQuestion(contestId, request)
        );
    }

    /**
     * Adds multiple questions to a contest.
     *
     * @param contestId the ID of the contest
     * @param requests  the list of questions to add
     * @return the created questions, or HTTP 204 if the list is empty
     */
    @PostMapping("/{contestId}/questions/bulk")
    public ResponseEntity<List<QuestionResponse>> addQuestions(
            @PathVariable Long contestId,
            @RequestBody List<CreateQuestionRequest> requests) {

        List<QuestionResponse> responses = requests.stream()
                .map(question -> questionService.addQuestion(contestId, question))
                .toList();

        return responses.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(responses);
    }

    /**
     * Retrieves the questions belonging to a contest.
     *
     * @param contestId the ID of the contest
     * @return the list of questions
     */
    @GetMapping("/{contestId}/questions")
    public ResponseEntity<List<QuestionResponse>> getQuestions(
            @PathVariable Long contestId) {

        return ResponseEntity.ok(
                questionService.getQuestions(contestId)
        );
    }

    /**
     * Updates an existing question.
     *
     * @param questionId the ID of the question to update
     * @param request    the updated question details
     * @return the updated question
     */
    @PutMapping("/questions/{questionId}")
    public ResponseEntity<QuestionResponse> updateQuestion(
            @PathVariable Long questionId,
            @RequestBody CreateQuestionRequest request) {

        return ResponseEntity.ok(
                questionService.updateQuestion(questionId, request)
        );
    }

    /**
     * Deletes a question.
     *
     * @param questionId the ID of the question to delete
     * @return an empty response with HTTP 204 status
     */
    @DeleteMapping("/questions/{questionId}")
    public ResponseEntity<Void> deleteQuestion(
            @PathVariable Long questionId) {

        questionService.deleteQuestion(questionId);

        return ResponseEntity.noContent().build();
    }
}


