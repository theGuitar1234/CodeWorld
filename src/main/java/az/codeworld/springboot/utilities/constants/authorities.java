package az.codeworld.springboot.utilities.constants;

public enum authorities {
    RESET_ANY_USER_PASSWORD(1L, "RESET_ANY_USER_PASSWORD"),
    ACCESS_ADMIN_PANEL(2L, "ACCESS_ADMIN_PANEL"),
    READ_ANY_CONTENT(3L, "READ_ANY_CONTENT"),
    WRITE_ANY_CONTENT(4L, "WRITE_ANY_CONTENT"),
    DELETE_ANY_CONTENT(5L, "DELETE_ANY_CONTENT"),
    UPDATE_ANY_CONTENT(6L, "UPDATE_ANY_CONTENT"),
    DELETE_ANY_USER(7L, "DELETE_ANY_USER"),
    SET_ANY_ROLE(8L, "SET_ANY_ROLE"),
    NO_AUTHORITIES(9L, "NO_AUTHORITIES");

    private Long authorityId;
    private String authorityString;

    private authorities(Long authorityId, String authorityString) {
        this.authorityId = authorityId;
        this.authorityString = authorityString;
    }

    public Long getAuthorityId() {
        return this.authorityId;
    }

    public String getAuthorityString() {
        return this.authorityString;
    }    
}
