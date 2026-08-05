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

    //needs user for this service........
    @Override
    @Transactional
    public ParticipantResponse joinContest(Long userId , Long contestId) {

        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Contest not found"));

        // Allow joining only before the contest starts
        if (contest.getStatus() != ContestStatus.UPCOMING) {
            throw new IllegalStateException(
                    "Contest is not open for registration");
        }

        // Prevent duplicate registration
        if (participantRepository.existsByContestIdAndUserId(
                contestId,
                userId)) {

            throw new IllegalStateException(
                    "You have already joined this contest");
        }

        // Check participant limit
        long participants =
                participantRepository.countByContestId(contestId);

        if (contest.getMaxParticipants() != null &&
                participants >= contest.getMaxParticipants()) {

            throw new IllegalStateException(
                    "Maximum participants reached");
        }

        ContestParticipant participant =
                ContestParticipant.builder()
                        .contest(contest)
                        .userId(userId)
                        .joinedAt(LocalDateTime.now())
                        .status(ParticipantStatus.REGISTERED)
                        .build();

        ContestParticipant saved =
                participantRepository.save(participant);

        return participantMapper.toResponse(saved);

    }


    @Override
    public List<ParticipantResponse> getParticipants(Long contestId) {

        if (!contestRepository.existsById(contestId)) {
            throw new ResourceNotFoundException("Contest not found");
        }

        return participantRepository.findByContestId(contestId)
                .stream()
                .map(participantMapper::toResponse)
                .toList();
    }

    @Override
    public Long getParticipantId(Long contestId, Long userId) {
        ContestParticipant participantId = participantRepository.findByContestIdAndUserId(contestId , userId).orElseThrow(()->
                new IllegalStateException("You are not registered to the contest")
        );
        return participantId.getId();
    }

    @Override
    public void setParticipantStatus(Long contestId, Long participantId, ParticipantStatus status) {
        ContestParticipant participant = participantRepository.findByContestIdAndUserId(contestId , participantId).orElseThrow(
                ()-> new  IllegalStateException("You are not registered to the contest")
        );

        participant.setStatus(status);
    }


}
