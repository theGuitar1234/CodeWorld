package az.codeworld.springboot.exceptions;

import org.springframework.stereotype.Component;

import az.codeworld.springboot.utilities.constants.exceptionmessages;

@Component
public class UserBlockedException extends Exception {

    public String exceptionMessage = exceptionmessages.USER_BLOCKED.getExceptionMessageString();

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public UserBlockedException(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public UserBlockedException() {

    }

}
