package az.codeworld.springboot.exceptions;

import org.springframework.stereotype.Component;

@Component
public class UserNotFoundException extends Exception {

    public String field;

    public String exceptionMessage = "User Not Found by " + field;

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
