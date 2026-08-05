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

    @PostMapping("/{contestId}/questions")
    public ResponseEntity<QuestionResponse> addQuestion(
            @PathVariable Long contestId,
              @RequestBody CreateQuestionRequest request) {

        return ResponseEntity.ok(
                questionService.addQuestion(contestId, request));
    }

    @PostMapping("/{contestId}/questions/bulk")
    public ResponseEntity<List<QuestionResponse>> addQuestion(
            @PathVariable Long contestId,
            @RequestBody List<CreateQuestionRequest> request) {

        List<QuestionResponse> responses = request.stream()
                .map( question -> questionService.addQuestion(contestId, question))
                .toList();
        return responses.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(responses);
    }

    @GetMapping("/{contestId}/questions")
    public ResponseEntity<List<QuestionResponse>> getQuestions(
            @PathVariable Long contestId) {

        return ResponseEntity.ok(
                questionService.getQuestions(contestId));
    }

    @PutMapping("/questions/{questionId}")
    public ResponseEntity<QuestionResponse> updateQuestion(
            @PathVariable Long questionId,
            @RequestBody CreateQuestionRequest request) {

        return ResponseEntity.ok(
                questionService.updateQuestion(questionId, request));
    }

    @DeleteMapping("/questions/{questionId}")
    public ResponseEntity<Void> deleteQuestion(
            @PathVariable Long questionId) {

        questionService.deleteQuestion(questionId);

        return ResponseEntity.noContent().build();
    }
}

