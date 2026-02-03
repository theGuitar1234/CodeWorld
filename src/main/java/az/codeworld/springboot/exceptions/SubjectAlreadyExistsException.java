package az.codeworld.springboot.exceptions;

import org.springframework.stereotype.Component;

import az.codeworld.springboot.utilities.constants.exceptionmessages;

@Component
public class SubjectAlreadyExistsException extends Exception {

    public String exceptionMessage = exceptionmessages.SUBJECT_ALREADY_EXISTS.getExceptionMessageString();

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public SubjectAlreadyExistsException(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public SubjectAlreadyExistsException() {

    }

}
