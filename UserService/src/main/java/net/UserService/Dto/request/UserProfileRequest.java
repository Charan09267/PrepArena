package net.UserService.Dto.request;


import lombok.*;
import net.UserService.Entity.UserProfile;


import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileRequest {

    @Builder.Default
    private List<UserProfile.Education> education = new ArrayList<>();

    @Builder.Default
    private List<UserProfile.Experience> experience = new ArrayList<>();

    @Builder.Default
    private List<UserProfile.Project> projects = new ArrayList<>();

    @Builder.Default
    private List<String> technicalSkills = new ArrayList<>();

    @Builder.Default
    private List<String> softSkills = new ArrayList<>();

    @Builder.Default
    private List<String> certifications = new ArrayList<>();

    private String currentRole;
    private String targetRole;
    private String careerGoal;
    private String careerPath;

    @Builder.Default
    private List<String> interests = new ArrayList<>();

    @Builder.Default
    private List<String> preferredLearningTopics = new ArrayList<>();

    private String preferredLearningStyle;
    private Integer weeklyLearningHours;

    private String githubUrl;
    private String linkedInUrl;
    private String portfolioUrl;

    private String country;
    private String city;
}
