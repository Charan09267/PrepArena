package net.contestmicroservice.Service.interfaces;

import net.contestmicroservice.dto.request.CreateContestRequest;
import net.contestmicroservice.dto.request.UpdateContestRequest;
import net.contestmicroservice.dto.response.AnswerKeyDto;
import net.contestmicroservice.dto.response.ContestResponse;

import java.util.List;


public interface ContestService {

    /**
     * Creates a contest for the specified user.
     *
     * @param userId  the ID of the user creating the contest
     * @param request the contest creation details
     * @return the created contest
     */
    ContestResponse createContest(
            Long userId,
            CreateContestRequest request
    );

    /**
     * Retrieves a contest by its ID.
     *
     * @param contestId the ID of the contest
     * @return the contest details
     */
    ContestResponse getContestById(Long contestId);

    /**
     * Retrieves all contests.
     *
     * @return the list of contests
     */
    List<ContestResponse> getAllContests();

    /**
     * Updates a contest owned by the specified user.
     *
     * @param contestId the ID of the contest
     * @param request   the updated contest details
     * @param userId    the ID of the requesting user
     * @return the updated contest
     */
    ContestResponse updateContest(
            Long contestId,
            UpdateContestRequest request,
            Long userId
    );

    /**
     * Deletes a contest owned by the specified user.
     *
     * @param contestId the ID of the contest
     * @param userId    the ID of the requesting user
     */
    void deleteContest(Long contestId, Long userId);

    /**
     * Publishes a contest.
     *
     * @param contestId the ID of the contest
     * @param userId    the ID of the requesting user
     * @return the published contest
     */
    ContestResponse publishContest(Long contestId, Long userId);

    /**
     * Cancels a contest.
     *
     * @param contestId the ID of the contest
     * @param userId    the ID of the requesting user
     * @return the cancelled contest
     */
    ContestResponse cancelContest(Long contestId, Long userId);

    /**
     * Activates contests whose scheduled start time has been reached.
     */
    void activateContests();

    /**
     * Completes contests whose scheduled end time has been reached.
     */
    void completeContests();

    /**
     * Retrieves the answer key for a contest.
     *
     * @param contestId the ID of the contest
     * @return the list of correct answers
     */
    List<AnswerKeyDto> getAnswerKey(Long contestId);
}

