package az.codeworld.springboot.web.services;

import az.codeworld.springboot.web.entities.ProfilePicture;

public interface ProfileService {

    /**
     * Legacy signature used in existing controllers.
     * Implementations should attach the profile to the current authenticated user.
     */
    void addProfilePictureToUser(ProfilePicture profilePicture);

    /** Get ProfilePicture by its database id. */
    ProfilePicture getProfileByProfileId(Long profileId);

    /** Legacy alias used in some controllers. */
    default ProfilePicture getProfileId(Long profileId) {
        return getProfileByProfileId(profileId);
    }

    /** Get or create the profile picture row for a user. */
    ProfilePicture getOrCreateForUser(String userName);

    /** Persist updates. */
    ProfilePicture saveProfilePicture(ProfilePicture profilePicture);
}
