package az.codeworld.springboot.utilities.constants;

public enum profileSuccess {
    PROFILE_ADDED("PROFILE_ADDED");

    private String profileSuccessString;

    private profileSuccess(String profileSuccessString) {
        this.profileSuccessString = profileSuccessString;
    }

    public String getProfileSuccessString() {
        return this.profileSuccessString;
    }
}