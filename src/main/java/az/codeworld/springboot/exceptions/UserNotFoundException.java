package az.codeworld.springboot.exceptions;

import org.springframework.stereotype.Component;

import az.codeworld.springboot.utilities.constants.exceptionmessages;

@Component
public class UserNotFoundException extends RuntimeException {

    public String field;

    public String exceptionMessage = exceptionmessages.USER_NOT_FOUND.getExceptionMessageString() + field;

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public UserNotFoundException(String exceptionMessage, String field) {
        this.exceptionMessage = exceptionMessage;
        this.field = field;
    }

    public UserNotFoundException() {

    }

    public UserNotFoundException(String field) {
        this.field = field;
    }

}
