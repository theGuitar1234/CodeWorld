package az.codeworld.springboot.admin.records;

import az.codeworld.springboot.admin.entities.Money;
import az.codeworld.springboot.utilities.constants.roles;

public record UserCombineRecord(
    Long id, 
    String firstName, 
    String lastName, 
    String payment,
    String nextDate,
    String email,
    roles role,
    String affiliationDate,
    Boolean isBanned
) {}
