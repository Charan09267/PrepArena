package net.contestmicroservice.Service.implementations;

import lombok.RequiredArgsConstructor;
import net.company.common.Exception.ResourceNotFoundException;
import net.contestmicroservice.Mapper.ParticipantMapper;
import net.contestmicroservice.Repo.ContestParticipantRepo;
import net.contestmicroservice.Repo.ContestRepo;
import net.contestmicroservice.Service.interfaces.ContestParticipantService;
import net.contestmicroservice.dto.response.ParticipantResponse;
import net.contestmicroservice.entity.Contest;
import net.contestmicroservice.entity.ContestParticipant;
import net.contestmicroservice.entity.enums.ContestStatus;
import net.contestmicroservice.entity.enums.ParticipantStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;


@Service
@RequiredArgsConstructor
public class ContestParticipantServiceImpl implements ContestParticipantService {

    private final ContestRepo contestRepository;
    private final ContestParticipantRepo participantRepository;
    private final ParticipantMapper participantMapper;

    /**
     * Registers a user for a contest after validating its registration status
     * and participant capacity.
     *
     * @param userId    the ID of the user joining the contest
     * @param contestId the ID of the contest
     * @return the registered participant details
     */
    @Override
    @Transactional
    public ParticipantResponse joinContest(Long userId, Long contestId) {

        // Retrieve the contest or fail if it does not exist.
        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Contest not found")
                );

        // Registration is allowed only while the contest is upcoming.
        if (contest.getStatus() != ContestStatus.UPCOMING) {
            throw new IllegalStateException(
                    "Contest is not open for registration"
            );
        }

        // Prevent the same user from registering for the contest more than once.
        if (participantRepository.existsByContestIdAndUserId(contestId, userId)) {
            throw new IllegalStateException(
                    "You have already joined this contest"
            );
        }

        // Check whether the contest has reached its participant limit.
        long participantCount = participantRepository.countByContestId(contestId);

        if (contest.getMaxParticipants() != null
                && participantCount >= contest.getMaxParticipants()) {
            throw new IllegalStateException("Maximum participants reached");
        }

        // Create the participant record with its initial registration status.
        ContestParticipant participant = ContestParticipant.builder()
                .contest(contest)
                .userId(userId)
                .joinedAt(LocalDateTime.now())
                .status(ParticipantStatus.REGISTERED)
                .build();

        // Persist the registration and return the saved participant details.
        ContestParticipant savedParticipant =
                participantRepository.save(participant);

        return participantMapper.toResponse(savedParticipant);
    }

    /**
     * Retrieves all participants registered for a contest.
     *
     * @param contestId the ID of the contest
     * @return the list of participant details
     */
    @Override
    public List<ParticipantResponse> getParticipants(Long contestId) {

        // Validate the contest before retrieving its participants.
        if (!contestRepository.existsById(contestId)) {
            throw new ResourceNotFoundException("Contest not found");
        }

        return participantRepository.findByContestId(contestId)
                .stream()
                .map(participantMapper::toResponse)
                .toList();
    }

    /**
     * Retrieves the participant ID associated with a user in a contest.
     *
     * @param contestId the ID of the contest
     * @param userId    the ID of the user
     * @return the participant ID
     */
    @Override
    public Long getParticipantId(Long contestId, Long userId) {

        ContestParticipant participant =
                participantRepository.findByContestIdAndUserId(contestId, userId)
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "You are not registered to the contest"
                                )
                        );

        return participant.getId();
    }

    /**
     * Updates the status of a participant in a contest.
     *
     * @param contestId     the ID of the contest
     * @param participantId the ID of the participant
     * @param status        the status to assign
     */
    @Override
    public void setParticipantStatus(
            Long contestId,
            Long participantId,
            ParticipantStatus status) {

        // Locate the participant using the supplied contest and identifier.
        ContestParticipant participant =
                participantRepository.findByContestIdAndUserId(
                                contestId, participantId
                        )
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "You are not registered to the contest"
                                )
                        );

        // Update the participant's status.
        participant.setStatus(status);
    }
}
