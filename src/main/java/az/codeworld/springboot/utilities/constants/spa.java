package az.codeworld.springboot.utilities.constants;

public enum spa {
    HOME("HOME"), 
    TEACHERS("TEACHERS"), 
    STUDENTS("STUDENTS"), 
    REQUESTS("REQUESTS"), 
    TANSACTIONS("TRANSACTIONS");

    private String spaString;

    private spa(String spaString) {
        this.spaString = spaString;
    }

    public String getSpaString() {
        return this.spaString;
    }
}
