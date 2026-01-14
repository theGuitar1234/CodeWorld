package az.codeworld.springboot.admin.records;

import az.codeworld.springboot.utilities.constants.roles;

public record RequestRecord(
    String firstName, 
    String lastName, 
    String email, 
    roles role
) {}
