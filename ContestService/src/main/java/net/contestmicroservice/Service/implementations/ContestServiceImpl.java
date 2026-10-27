package net.contestmicroservice.Service.implementations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.company.common.Exception.ResourceNotFoundException;
import net.contestmicroservice.Mapper.ContestMapper;
import net.contestmicroservice.Repo.ContestRepo;
import net.contestmicroservice.Repo.QuestionRepo;
import net.contestmicroservice.Service.interfaces.ContestService;
import net.contestmicroservice.dto.request.CreateContestRequest;
import net.contestmicroservice.dto.request.UpdateContestRequest;
import net.contestmicroservice.dto.response.AnswerKeyDto;
import net.contestmicroservice.dto.response.ContestResponse;
import net.contestmicroservice.entity.Contest;
import net.contestmicroservice.entity.ContestOption;
import net.contestmicroservice.entity.enums.ContestStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class ContestServiceImpl implements ContestService {

    private final ContestRepo contestRepository;
    private final ContestMapper contestMapper;
    private final QuestionRepo questionRepository;

    /**
     * Creates a new contest in draft status.
     *
     * @param userId  the ID of the user creating the contest
     * @param request the contest configuration
     * @return the created contest details
     */
    @Override
    public ContestResponse createContest(
            Long userId,
            CreateContestRequest request) {

        // Initialize the contest with the supplied details and draft status.
        Contest contest = Contest.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .createdBy(userId)
                .visibility(request.getVisibility())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .durationSeconds(request.getDurationSeconds())
                .maxParticipants(request.getMaxParticipants())
                .contestType(request.getContestType())
                .status(ContestStatus.DRAFT)
                .build();

        Contest savedContest = contestRepository.save(contest);

        return contestMapper.toResponse(savedContest);
    }

    /**
     * Retrieves a contest by its ID.
     *
     * @param contestId the ID of the contest
     * @return the contest details
     */
    @Override
    public ContestResponse getContestById(Long contestId) {

        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Contest not found")
                );

        return contestMapper.toResponse(contest);
    }

    /**
     * Retrieves all contests.
     *
     * @return the list of contest details
     */
    @Override
    public List<ContestResponse> getAllContests() {

        return contestRepository.findAll()
                .stream()
                .map(contestMapper::toResponse)
                .toList();
    }

    /**
     * Updates a contest if it is editable and belongs to the requesting user.
     *
     * @param contestId the ID of the contest to update
     * @param request   the updated contest details
     * @param userId    the ID of the requesting user
     * @return the updated contest details
     */
    @Override
    public ContestResponse updateContest(
            Long contestId,
            UpdateContestRequest request,
            Long userId) {

        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Contest not found")
                );

        // Prevent updates to terminal/live contests and contests not owned
        // by the requesting user.
        if (contest.getStatus() == ContestStatus.LIVE
                || contest.getStatus() == ContestStatus.COMPLETED
                || contest.getStatus() == ContestStatus.CANCELLED
                || contest.getCreatedBy() != userId) {

            throw new IllegalStateException("Contest cannot be updated");
        }

        // Apply the updated contest configuration.
        contest.setTitle(request.getTitle());
        contest.setDescription(request.getDescription());
        contest.setVisibility(request.getVisibility());
        contest.setStartTime(request.getStartTime());
        contest.setEndTime(request.getEndTime());
        contest.setDurationSeconds(request.getDurationSeconds());
        contest.setMaxParticipants(request.getMaxParticipants());
        contest.setContestType(request.getContestType());

        Contest updatedContest = contestRepository.save(contest);

        return contestMapper.toResponse(updatedContest);
    }

    /**
     * Deletes a contest if the requesting user owns it and it is not live.
     *
     * @param contestId the ID of the contest to delete
     * @param userId    the ID of the requesting user
     */
    @Override
    public void deleteContest(Long contestId, Long userId) {

        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Contest not found")
                );

        // Only the contest owner may delete it.
        if (!userId.equals(contest.getCreatedBy())) {
            throw new IllegalStateException(
                    "User is not allowed to delete contest"
            );
        }

        // A live contest cannot be deleted.
        if (contest.getStatus() == ContestStatus.LIVE) {
            throw new IllegalStateException(
                    "Cannot delete a contest which is live..."
            );
        }

        contestRepository.delete(contest);
    }

    /**
     * Publishes a draft contest after validating its schedule and questions.
     *
     * @param contestId the ID of the contest to publish
     * @param userId    the ID of the requesting user
     * @return the updated contest details
     */
    @Override
    public ContestResponse publishContest(Long contestId, Long userId) {

        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Contest not found!")
                );

        // Verify that the requesting user owns the contest.
        if (!userId.equals(contest.getCreatedBy())) {
            throw new IllegalStateException(
                    "User is not allowed to publish contest"
            );
        }

        // Only draft contests can be published.
        if (contest.getStatus() != ContestStatus.DRAFT) {
            throw new IllegalStateException(
                    "Contest status is not DRAFT, so it may already been published...."
            );
        }

        // Validate the configured contest time range.
        if (contest.getStartTime().isAfter(contest.getEndTime())) {
            throw new IllegalStateException(
                    "Start time must be before end time"
            );
        }

        // A contest must contain at least one question before publication.
        long questionCount = questionRepository.countByContestId(contestId);

        if (questionCount == 0) {
            throw new IllegalStateException(
                    "Contest must contain at least one question"
            );
        }

        // Published contests become upcoming until their scheduled start.
        contest.setStatus(ContestStatus.UPCOMING);
        contestRepository.save(contest);

        return contestMapper.toResponse(contest);
    }

    /**
     * Cancels a contest if the current status allows cancellation.
     *
     * @param contestId the ID of the contest to cancel
     * @param userId    the ID of the requesting user
     * @return the updated contest details
     */
    @Override
    public ContestResponse cancelContest(Long contestId, Long userId) {

        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Contest not found")
                );

        // Validate the requesting user's permission to cancel the contest.
        if (userId.equals(contest.getCreatedBy())) {
            throw new IllegalStateException(
                    "User is not allowed to cancel contest"
            );
        }

        // Live or completed contests cannot be cancelled.
        if (contest.getStatus() == ContestStatus.LIVE
                || contest.getStatus() == ContestStatus.COMPLETED) {

            throw new IllegalStateException(
                    "Live or completed contests cannot be cancelled"
            );
        }

        contest.setStatus(ContestStatus.CANCELLED);
        contestRepository.save(contest);

        return contestMapper.toResponse(contest);
    }

    /**
     * Activates contests whose scheduled start time has been reached.
     *
     * The repository query determines which contests are eligible for activation.
     */
    @Override
    @Transactional
    public void activateContests() {

        List<Contest> contests =
                contestRepository.findContestsToActivate(LocalDateTime.now());

        // Update eligible contests to live status.
        contests.forEach(contest -> {
            contest.setStatus(ContestStatus.LIVE);
            log.info("Contest {} activated", contest.getId());
        });
    }

    /**
     * Completes contests whose scheduled end time has been reached.
     *
     * The repository query determines which contests are eligible for completion.
     */
    @Override
    @Transactional
    public void completeContests() {

        List<Contest> contests =
                contestRepository.findContestsToComplete(LocalDateTime.now());

        // Update eligible contests to completed status.
        contests.forEach(contest -> {
            contest.setStatus(ContestStatus.COMPLETED);
            log.info("Contest {} completed", contest.getId());
        });
    }

    /**
     * Retrieves the correct option IDs for each question in a contest.
     *
     * @param contestId the ID of the contest
     * @return the answer key containing question metadata and correct option IDs
     */
    @Override
    public List<AnswerKeyDto> getAnswerKey(Long contestId) {

        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Contest not found for the given id..."
                        )
                );

        // Build one answer-key entry for each question in the contest.
        return contest.getQuestions()
                .stream()
                .map(question -> {

                    // Collect the IDs of options marked as correct.
                    List<Long> correctOptionIds = question.getOptions()
                            .stream()
                            .filter(ContestOption::getIsCorrect)
                            .map(ContestOption::getId)
                            .toList();

                    return AnswerKeyDto.builder()
                            .questionId(question.getId())
                            .marks(question.getMarks())
                            .questionType(question.getQuestionType())
                            .correctOptionIds(correctOptionIds)
                            .build();
                })
                .toList();
    }
}
