package net.contestmicroservice.Controller;

import lombok.RequiredArgsConstructor;
import net.company.common.Exception.ResourceNotFoundException;
import net.contestmicroservice.Service.implementations.ContestServiceImpl;
import net.contestmicroservice.Service.interfaces.ContestService;
import net.contestmicroservice.dto.request.CreateContestRequest;
import net.contestmicroservice.dto.request.UpdateContestRequest;
import net.contestmicroservice.dto.response.AnswerKeyDto;
import net.contestmicroservice.dto.response.ContestResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/contests")
@RequiredArgsConstructor
public class ContestController {

    private final ContestService contestService;

    /**
     * Creates a new contest for the specified user.
     *
     * @param userId  the ID of the user creating the contest
     * @param request the contest creation details
     * @return the created contest
     */
    @PostMapping
    public ResponseEntity<ContestResponse> createContest(
            @RequestHeader("X-USER-ID") Long userId,
            @RequestBody CreateContestRequest request) {

        return ResponseEntity.ok(
                contestService.createContest(userId, request)
        );
    }

    /**
     * Retrieves a contest by its ID.
     *
     * @param id the ID of the contest
     * @return the contest details, or 404 if the contest does not exist
     */
    @GetMapping("/{id}")
    public ResponseEntity<ContestResponse> getContest(
            @PathVariable Long id) {

        try {
            return ResponseEntity.ok(
                    contestService.getContestById(id)
            );
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Retrieves all contests.
     *
     * @return the list of contests
     */
    @GetMapping
    public ResponseEntity<List<ContestResponse>> getAllContests() {

        return ResponseEntity.ok(
                contestService.getAllContests()
        );
    }

    /**
     * Updates an existing contest owned by the specified user.
     *
     * @param id      the ID of the contest to update
     * @param request the updated contest details
     * @param userId  the ID of the requesting user
     * @return the updated contest
     */
    @PutMapping("/{id}")
    public ResponseEntity<ContestResponse> updateContest(
            @PathVariable Long id,
            @RequestBody UpdateContestRequest request,
            @RequestHeader("X-USER-ID") Long userId) {

        return ResponseEntity.ok(
                contestService.updateContest(id, request, userId)
        );
    }

    /**
     * Deletes a contest owned by the specified user.
     *
     * @param id     the ID of the contest to delete
     * @param userId the ID of the requesting user
     * @return an empty response with HTTP 204 status
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContest(
            @PathVariable Long id,
            @RequestHeader("X-USER-ID") Long userId) {

        contestService.deleteContest(id, userId);

        return ResponseEntity.noContent().build();
    }

    /**
     * Publishes a contest using the contest service.
     *
     * @param contestId the ID of the contest to publish
     * @param userId    the ID of the requesting user
     * @return the updated contest
     */
    @PostMapping("/{contestId}/publish")
    public ResponseEntity<ContestResponse> publishContest(
            @PathVariable Long contestId,
            @RequestHeader("X-USER-ID") Long userId) {

        return ResponseEntity.ok(
                contestService.publishContest(contestId, userId)
        );
    }

    /**
     * Cancels a contest using the contest service.
     *
     * @param contestId the ID of the contest to cancel
     * @param userId    the ID of the requesting user
     * @return the updated contest
     */
    @PostMapping("/{contestId}/cancel")
    public ResponseEntity<ContestResponse> cancelContest(
            @PathVariable Long contestId,
            @RequestHeader("X-USER-ID") Long userId) {

        return ResponseEntity.ok(
                contestService.cancelContest(contestId, userId)
        );
    }

    /**
     * Retrieves the answer key for a contest.
     *
     * @param contestId the ID of the contest
     * @return the list of correct answers
     */
    @GetMapping("/{contestId}/answer-key")
    public ResponseEntity<List<AnswerKeyDto>> getAnswerKey(
            @PathVariable Long contestId) {

        return ResponseEntity.ok(
                contestService.getAnswerKey(contestId)
        );
    }
}
