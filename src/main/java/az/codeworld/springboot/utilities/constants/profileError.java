package az.codeworld.springboot.utilities.constants;

public enum profileError {
    ADD_PROFILE_ERROR("ADD_PROFILE_ERROR"),
    PROFILE_ERROR("PROFILE_ERROR");

    private String profileErrorString;

    private profileError(String profileErrorString) {
        this.profileErrorString = profileErrorString;
    }

    public String getProfileErrorString() {
        return this.profileErrorString;
    }
}