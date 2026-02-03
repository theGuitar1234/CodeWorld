package az.codeworld.springboot.utilities.constants;

public enum exceptionmessages {
    CLASS_SECTION_ALREADY_EXISTS("Class Section Already Exists for this subject and for this teacher on this date"),
    COURSE_OFFERING_ALREADY_EXISTS("Course Offering Already Exists for this subject and for this teacher"),
    SUBJECT_ALREADY_EXISTS("A Subject with this name already exists"),
    USER_BLOCKED("The User is blocked because of too many failed login attempts"),
    USER_NOT_FOUND("User Not Found by: ");

    private static final String DEFAULT_EXCEPTION_MESSAGE = "Something Went Wrong";

    private String exceptionMessageString;

    private exceptionmessages(String exceptionMessageString) {
        this.exceptionMessageString = exceptionMessageString;
    }

    public String getExceptionMessageString() {
        return this.exceptionMessageString;
    }

    public static String getDefault() {
        return DEFAULT_EXCEPTION_MESSAGE;
    }
}
