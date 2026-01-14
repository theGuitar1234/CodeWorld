package az.codeworld.springboot.utilities.constants;

public enum presence {
    OFFLINE("Offline"),
    ONLINE("Online");

    private String presenceString;

    private presence(String presenceString) {
        this.presenceString = presenceString;
    }

    public String getPresenceString() {
        return this.presenceString;
    }
}
