package net.contestmicroservice.Controller;

import lombok.RequiredArgsConstructor;
import net.contestmicroservice.Service.interfaces.ContestParticipantService;
import net.contestmicroservice.dto.response.ParticipantResponse;
import net.contestmicroservice.entity.enums.ParticipantStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/contests")
@RequiredArgsConstructor
public class ContestParticipantController {

    private final ContestParticipantService participantService;

    /**
     * Registers a user as a participant in a contest.
     *
     * @param userId    the ID of the user joining the contest
     * @param contestId the ID of the contest to join
     * @return the participant details
     */
    @PostMapping("/{contestId}/join")
    public ResponseEntity<ParticipantResponse> joinContest(
            @RequestHeader("X-USER-ID") Long userId,
            @PathVariable Long contestId) {

        return ResponseEntity.ok(
                participantService.joinContest(userId, contestId)
        );
    }

    /**
     * Retrieves the participants registered for a contest.
     *
     * @param contestId the ID of the contest
     * @return the list of participants
     */
    @GetMapping("/{contestId}/participants")
    public ResponseEntity<List<ParticipantResponse>> getParticipants(
            @PathVariable Long contestId) {

        return ResponseEntity.ok(
                participantService.getParticipants(contestId)
        );
    }

    /**
     * Retrieves the participant ID for a user in a contest.
     *
     * @param contestId the ID of the contest
     * @param userId    the ID of the user
     * @return the participant ID
     */
    @GetMapping("/{contestId}/participants/{userId}")
    public ResponseEntity<Long> getParticipant(
            @PathVariable Long contestId,
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                participantService.getParticipantId(contestId, userId)
        );
    }

    /**
     * Endpoint intended to update a participant's status.
     *
     * TODO: The current implementation retrieves the participant ID
     * instead of invoking the participant status update operation.
     *
     * @param contestId the ID of the contest
     * @param userId    the ID of the user
     * @param status    the requested participant status
     * @return the participant ID returned by the current implementation
     */
    @PostMapping("/{contestId}/participants/{userId}")
    public ResponseEntity<Long> setParticipantStatus(
            @PathVariable Long contestId,
            @PathVariable Long userId,
            @RequestBody ParticipantStatus status) {

        return ResponseEntity.ok(
                participantService.getParticipantId(contestId, userId)
        );
    }
}

