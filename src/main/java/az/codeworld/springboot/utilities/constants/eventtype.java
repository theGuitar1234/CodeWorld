package az.codeworld.springboot.utilities.constants;

public enum eventtype {
    USER_REGISTERED("#00ff00"), 
    USER_BANNED("#ff0000"), 
    LOGIN_SUCCESS("#00ad00"), 
    LOGIN_FAILURE("#ad0000"), 
    USER_UNBANNED("#0000ff"), 
    USER_UPDATED("#808080"), 
    USER_DELETED("#340077"),
    SUBJECT_DELETED("#ff008c"),
    COURSE_OFFERING_DELETED("#c300ff");

    private String eventColor;

    private eventtype(String eventColor) {
        this.eventColor = eventColor;
    }

    public String getEventColor() {
        return this.eventColor;
    }
}