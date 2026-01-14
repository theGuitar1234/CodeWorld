package az.codeworld.springboot.utilities.constants;

public enum accountError {
    TOKEN_GENERATION_ERROR("TOKEN_GENERATION_ERROR"),
    ADD_ACCOUNT_ERROR("ADD_ACCOUNT_ERROR"),
    ACCOUNT_ERROR("ACCOUNT_ERROR");

    private String accountErrorString;

    private accountError(String accountErrorString) {
        this.accountErrorString = accountErrorString;
    }

    public String getAccountErrorString() {
        return this.accountErrorString;
    }
}
