package net.UserService.Entity;

import jakarta.persistence.PrePersist;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "user_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile {

    @Id
    private String id;

    // Canonical ID from MySQL users.id
    private Long userId;

    // Education details
    @Builder.Default
    private List<Education> education = new ArrayList<>();

    // Work experience and internships
    @Builder.Default
    private List<Experience> experience = new ArrayList<>();

    // Projects developed by the user
    @Builder.Default
    private List<Project> projects = new ArrayList<>();

    // Technical and soft skills
    @Builder.Default
    private List<String> technicalSkills = new ArrayList<>();

    @Builder.Default
    private List<String> softSkills = new ArrayList<>();

    @Builder.Default
    private List<String> certifications = new ArrayList<>();

    // Career information
    private String currentRole;
    private String targetRole;
    private String careerGoal;
    private String careerPath;

    // Learning preferences
    @Builder.Default
    private List<String> interests = new ArrayList<>();

    @Builder.Default
    private List<String> preferredLearningTopics = new ArrayList<>();

    private String preferredLearningStyle;
    private Integer weeklyLearningHours;

    // Optional professional links and location
    private String githubUrl;
    private String linkedInUrl;
    private String portfolioUrl;
    private String country;
    private String city;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Education {

        private String institution;
        private String degree;
        private String fieldOfStudy;

        private Integer startYear;
        private Integer endYear;

        private Double grade;
        private String gradeType;

        private Boolean currentlyStudying;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Experience {

        private String company;
        private String jobTitle;
        private String employmentType;

        private LocalDate startDate;
        private LocalDate endDate;

        private Boolean currentlyWorking;
        private String description;

        @Builder.Default
        private List<String> skillsUsed = new ArrayList<>();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Project {

        private String title;
        private String description;
        private String projectUrl;
        private String repositoryUrl;

        @Builder.Default
        private List<String> technologies = new ArrayList<>();
    }
}