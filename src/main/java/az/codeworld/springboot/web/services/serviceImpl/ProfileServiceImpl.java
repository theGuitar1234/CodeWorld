package az.codeworld.springboot.web.services.serviceImpl;

import java.time.LocalDateTime;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import az.codeworld.springboot.admin.entities.User;
import az.codeworld.springboot.admin.repositories.UserRepository;
import az.codeworld.springboot.web.entities.ProfilePicture;
import az.codeworld.springboot.web.repositories.ProfilePictureRepository;
import az.codeworld.springboot.web.services.ProfileService;

@Service
public class ProfileServiceImpl implements ProfileService {

    public static final String DEFAULT_PROFILE_PHOTO = "/assets/sprites/profile-thumb.jpg";

    private final ProfilePictureRepository profilePictureRepository;
    private final UserRepository userRepository;

    public ProfileServiceImpl(ProfilePictureRepository profilePictureRepository, UserRepository userRepository) {
        this.profilePictureRepository = profilePictureRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void addProfilePictureToUser(ProfilePicture profilePicture) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new RuntimeException("User not found: " + userName));

        ProfilePicture existing = profilePictureRepository.findByUser(user).orElse(null);
        if (existing != null) {
            // Treat as update of metadata only
            existing.setProfileTitle(profilePicture.getProfileTitle());
            existing.setDescription(profilePicture.getDescription());
            saveProfilePicture(existing);
            return;
        }

        profilePicture.setUser(user);
        if (profilePicture.getAdded() == null) {
            profilePicture.setAdded(LocalDateTime.now());
        }
        if (profilePicture.getProfilePhoto() == null || profilePicture.getProfilePhoto().isBlank()) {
            profilePicture.setProfilePhoto(DEFAULT_PROFILE_PHOTO);
        }

        profilePictureRepository.save(profilePicture);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfilePicture getProfileByProfileId(Long profileId) {
        return profilePictureRepository.findById(profileId)
                .orElseThrow(() -> new RuntimeException("ProfilePicture not found: " + profileId));
    }

    @Override
    @Transactional
    public ProfilePicture getOrCreateForUser(String userName) {
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new RuntimeException("User not found: " + userName));

        return profilePictureRepository.findByUser(user).orElseGet(() -> {
            ProfilePicture p = new ProfilePicture();
            p.setUser(user);
            p.setAdded(LocalDateTime.now());
            p.setProfilePhoto(DEFAULT_PROFILE_PHOTO);
            return profilePictureRepository.save(p);
        });
    }

    @Override
    @Transactional
    public ProfilePicture saveProfilePicture(ProfilePicture profilePicture) {
        if (profilePicture.getAdded() == null) {
            profilePicture.setAdded(LocalDateTime.now());
        }
        if (profilePicture.getProfilePhoto() == null || profilePicture.getProfilePhoto().isBlank()) {
            profilePicture.setProfilePhoto(DEFAULT_PROFILE_PHOTO);
        }
        return profilePictureRepository.save(profilePicture);
    }
}
