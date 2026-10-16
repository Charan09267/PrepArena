package net.UserService.Controller;


import lombok.RequiredArgsConstructor;
import net.UserService.Dto.request.UserProfileRequest;
import net.UserService.Entity.UserEntity;
import net.UserService.Entity.UserProfile;
import net.UserService.Repo.UserRepository;
import net.UserService.Service.UserProfileService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/me/profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<UserProfile> getMyProfile(
            Authentication authentication) {

        UserEntity user = getAuthenticatedUser(authentication);

        UserProfile profile =
                userProfileService.getProfile(user.getId());

        return ResponseEntity.ok(profile);
    }

    @PutMapping
    public ResponseEntity<UserProfile> updateMyProfile(
            Authentication authentication,
            @RequestBody UserProfileRequest request) {

        UserEntity user = getAuthenticatedUser(authentication);

        UserProfile profile =
                userProfileService.saveOrUpdateProfile(
                        user.getId(),
                        request
                );

        // Keep the existing MySQL flag synchronized with profile submission.
        if (!Boolean.TRUE.equals(user.getProfileCompleted())) {
            user.setProfileCompleted(true);
            userRepository.save(user);
        }

        return ResponseEntity.ok(profile);
    }

    private UserEntity getAuthenticatedUser(
            Authentication authentication) {

        if (authentication == null
                || !authentication.isAuthenticated()) {
            throw new org.springframework.security.access
                    .AccessDeniedException("Authentication required");
        }

        return userRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new org.springframework.security.core
                                .userdetails.UsernameNotFoundException(
                                "Authenticated user not found"
                        )
                );
    }
}
