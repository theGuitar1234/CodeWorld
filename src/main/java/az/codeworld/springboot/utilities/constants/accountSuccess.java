package az.codeworld.springboot.utilities.constants;

public enum accountsuccess {
    ACCOUNT_ADDED("Successfully added the account"),
    ACCEPT_REQUEST_SUCCESS("Successfully sent the acceptance email"),
    REJECT_REQUEST_SUCCESS("Successfully sent the rejection email");

    private String accountSuccessString;

    private accountsuccess(String accountSuccessString) {
        this.accountSuccessString = accountSuccessString;
    }

    public String getAccountSuccessString() {
        return this.accountSuccessString;
    }
}
