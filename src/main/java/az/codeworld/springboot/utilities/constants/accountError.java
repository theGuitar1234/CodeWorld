package az.codeworld.springboot.utilities.constants;

public enum accounterror {
    ACCOUNT_BANNED("The User Account is Banned"),
    ACCOUNT_IS_BANNED("The Account is Banned"),
    PASSWORDS_MUST_MATCH("Passwords didn't match"),
    PASSWORDS_MUST_BE_PRESENT("Passwords, both the written and repeated one, must not be null or empty, or blank"),
    ACCEPT_REQUEST_ERROR("Failed to send the request email"),
    REJECT_REQUEST_ERROR("Failed to send the request email");

    private String accountErrorString;

    private accounterror(String accountErrorString) {
        this.accountErrorString = accountErrorString;
    }

    public String getAccountErrorString() {
        return this.accountErrorString;
    }
}
