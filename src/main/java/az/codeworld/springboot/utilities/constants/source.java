package az.codeworld.springboot.utilities.constants;

public enum source {
    CLIENT("CLIENT"), 
    SERVER("SERVER"),
    SYSTEM("SYSTEM");

    private String sourceString;

    private source(String sourceString) {
        this.sourceString = sourceString;
    }

    public String getSourceString() {
        return this.sourceString;
    }
}
