package az.codeworld.springboot.utilities.constants;

public enum accountSuccess {
    ACCOUNT_ADDED("ACCOUNT_ADDED");

    private String accountSuccessString;

    private accountSuccess(String accountSuccessString) {
        this.accountSuccessString = accountSuccessString;
    }

    public String getAccountSuccessString() {
        return this.accountSuccessString;
    }
}
