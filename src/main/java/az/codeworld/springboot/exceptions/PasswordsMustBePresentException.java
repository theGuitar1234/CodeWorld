package az.codeworld.springboot.exceptions;

import org.springframework.stereotype.Component;

import az.codeworld.springboot.utilities.constants.accounterror;

@Component
public class PasswordsMustBePresentException extends Exception {

    public String exceptionMessage = accounterror.PASSWORDS_MUST_BE_PRESENT.getAccountErrorString();

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public PasswordsMustBePresentException(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public PasswordsMustBePresentException() {

    }

}
