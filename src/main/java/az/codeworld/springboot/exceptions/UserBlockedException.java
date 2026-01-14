package az.codeworld.springboot.exceptions;

import org.springframework.stereotype.Component;

@Component
public class UserBlockedException extends Exception {

    public String exceptionMessage = "The User is blocked because of too many failed login attempts";

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
