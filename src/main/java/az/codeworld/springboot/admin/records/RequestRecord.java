package az.codeworld.springboot.admin.records;

import az.codeworld.springboot.utilities.constants.roles;

public record RequestRecord(
    String firstname, 
    String lastname, 
    String email, 
    roles role
) {}
