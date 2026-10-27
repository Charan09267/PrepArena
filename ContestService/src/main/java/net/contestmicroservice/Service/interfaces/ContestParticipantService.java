package net.contestmicroservice.Service.interfaces;

import net.contestmicroservice.dto.response.ParticipantResponse;
import net.contestmicroservice.entity.enums.ParticipantStatus;

import java.util.List;


public interface ContestParticipantService {

    /**
     * Registers a user for a contest.
     *
     * @param userId    the ID of the user
     * @param contestId the ID of the contest
     * @return the participant details
     */
    ParticipantResponse joinContest(Long userId, Long contestId);

    /**
     * Retrieves all participants registered for a contest.
     *
     * @param contestId the ID of the contest
     * @return the list of participants
     */
    List<ParticipantResponse> getParticipants(Long contestId);

    /**
     * Finds the participant ID associated with a user and contest.
     *
     * @param contestId the ID of the contest
     * @param userId    the ID of the user
     * @return the participant ID
     */
    Long getParticipantId(Long contestId, Long userId);

    /**
     * Updates the status of a contest participant.
     *
     * @param contestId     the ID of the contest
     * @param participantId the ID of the participant
     * @param status        the new participant status
     */
    void setParticipantStatus(
            Long contestId,
            Long participantId,
            ParticipantStatus status
    );
}
