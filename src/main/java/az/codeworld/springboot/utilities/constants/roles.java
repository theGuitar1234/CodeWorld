package az.codeworld.springboot.utilities.constants;

public enum roles {
    ADMIN(1L, "ADMIN"),
    TEACHER(2L, "TEACHER"),
    STUDENT(3L, "STUDENT");

    private Long roleId;
    private String roleNameString;

    private roles(Long roleId, String roleNameString) {
        this.roleId = roleId;
        this.roleNameString = roleNameString;
    }

    public Long getRoleId() {
        return this.roleId;
    }

    public String getRoleNameString() {
        return this.roleNameString;
    }
}
