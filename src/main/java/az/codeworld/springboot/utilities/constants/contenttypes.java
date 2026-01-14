package az.codeworld.springboot.utilities.constants;

public enum contenttypes {
    JPEG(".jpg"),
    PNG(".png");

    private String contentTypeString;

    private contenttypes(String contentTypeString) {
        this.contentTypeString = contentTypeString;
    }

    public String getContentTypeString() {
        return this.contentTypeString;
    }
}
