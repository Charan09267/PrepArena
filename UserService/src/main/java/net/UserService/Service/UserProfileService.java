package net.UserService.Service;


import lombok.RequiredArgsConstructor;
import net.UserService.Dto.request.UserProfileRequest;
import net.UserService.Entity.UserProfile;
import net.UserService.Repo.UserProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository profileRepository;

    public UserProfile getOrCreateProfile(Long userId) {
        return profileRepository.findByUserId(userId)
                .orElseGet(() ->
                        profileRepository.save(
                                UserProfile.builder()
                                        .userId(userId)
                                        .build()
                        )
                );
    }

    public UserProfile getProfile(Long userId) {
        return getOrCreateProfile(userId);
    }

    @Transactional
    public UserProfile saveOrUpdateProfile(
            Long userId,
            UserProfileRequest request) {

        UserProfile profile = getOrCreateProfile(userId);

        profile.setEducation(request.getEducation());
        profile.setExperience(request.getExperience());
        profile.setProjects(request.getProjects());

        profile.setTechnicalSkills(request.getTechnicalSkills());
        profile.setSoftSkills(request.getSoftSkills());
        profile.setCertifications(request.getCertifications());

        profile.setCurrentRole(request.getCurrentRole());
        profile.setTargetRole(request.getTargetRole());
        profile.setCareerGoal(request.getCareerGoal());
        profile.setCareerPath(request.getCareerPath());

        profile.setInterests(request.getInterests());
        profile.setPreferredLearningTopics(
                request.getPreferredLearningTopics()
        );
        profile.setPreferredLearningStyle(
                request.getPreferredLearningStyle()
        );
        profile.setWeeklyLearningHours(request.getWeeklyLearningHours());

        profile.setGithubUrl(request.getGithubUrl());
        profile.setLinkedInUrl(request.getLinkedInUrl());
        profile.setPortfolioUrl(request.getPortfolioUrl());

        profile.setCountry(request.getCountry());
        profile.setCity(request.getCity());

        return profileRepository.save(profile);
    }
}
