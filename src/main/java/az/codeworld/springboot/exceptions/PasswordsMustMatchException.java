package az.codeworld.springboot.exceptions;

import org.springframework.stereotype.Component;

import az.codeworld.springboot.utilities.constants.accounterror;

@Component
public class PasswordsMustMatchException extends Exception {

    public String exceptionMessage = accounterror.PASSWORDS_MUST_MATCH.getAccountErrorString();

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public PasswordsMustMatchException(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public PasswordsMustMatchException() {

    }

}
