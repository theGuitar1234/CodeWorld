package az.codeworld.springboot.web.services;

import az.codeworld.springboot.web.entities.ProfilePicture;

public interface ProfileService {
    void addProfilePictureToUser(ProfilePicture profilePicture);
    ProfilePicture getProfileByProfileId(Long profileId);
    void saveProfilePicture(ProfilePicture profilePicture);
}
