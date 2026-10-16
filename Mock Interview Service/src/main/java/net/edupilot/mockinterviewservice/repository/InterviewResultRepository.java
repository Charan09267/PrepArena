package net.edupilot.mockinterviewservice.repository;

import net.edupilot.mockinterviewservice.entity.InterviewResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InterviewResultRepository
        extends JpaRepository<InterviewResult, Long> {

    Optional<InterviewResult> findByInterviewId(Long interviewId);
}
