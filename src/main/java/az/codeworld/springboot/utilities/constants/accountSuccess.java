package az.codeworld.springboot.utilities.constants;

public enum accountsuccess {
    ACCOUNT_ADDED("ACCOUNT_ADDED");

    private String accountSuccessString;

    private accountsuccess(String accountSuccessString) {
        this.accountSuccessString = accountSuccessString;
    }

    public String getAccountSuccessString() {
        return this.accountSuccessString;
    }
}
