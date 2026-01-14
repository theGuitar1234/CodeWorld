package az.codeworld.springboot.utilities.constants;

public enum dtotype {
    TRANSACTION("TRANSACTION"), 
    DASHBOARD("DASHBOARD"), 
    CREATE("CREATE"),
    LOGIN_AUDIT("LOGIN_AUDIT"),
    FULL("FULL");

    String dtoTypeString;

    private dtotype(String dtoTypeString) {
        this.dtoTypeString = dtoTypeString;
    }

    public String getDtoTypeString() {
        return this.dtoTypeString;
    }
}
