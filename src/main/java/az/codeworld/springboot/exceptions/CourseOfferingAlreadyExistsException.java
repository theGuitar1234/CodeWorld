package az.codeworld.springboot.exceptions;

import org.springframework.stereotype.Component;

import az.codeworld.springboot.utilities.constants.exceptionmessages;

@Component
public class CourseOfferingAlreadyExistsException extends Exception {

    public String exceptionMessage = exceptionmessages.COURSE_OFFERING_ALREADY_EXISTS.getExceptionMessageString();

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public CourseOfferingAlreadyExistsException(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public CourseOfferingAlreadyExistsException() {

    }

}
