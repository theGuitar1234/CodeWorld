package az.codeworld.springboot.utilities.constants;

public enum spa {
    HOME("HOME"), 
    TEACHERS("TEACHERS"), 
    STUDENTS("STUDENTS"), 
    STUDENT_TRANSACTIONS("STUDENT_TRANSACTIONS"),
    TEACHER_TRANSACTIONS("TEACHER_TRANSACTIONS"),
    REQUESTS("REQUESTS"), 
    USERS("USERS"),
    TANSACTIONS("TRANSACTIONS"),
    COURSE_OFFERINGS("COURSE_OFFERINGS"),
    SUBJECTS("SUBJECTS");

    private String spaString;

    private spa(String spaString) {
        this.spaString = spaString;
    }

    public String getSpaString() {
        return this.spaString;
    }
}
