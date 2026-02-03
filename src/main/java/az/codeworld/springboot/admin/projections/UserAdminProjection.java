package az.codeworld.springboot.admin.projections;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import az.codeworld.springboot.admin.entities.Money;
import az.codeworld.springboot.security.entities.Role;
import az.codeworld.springboot.utilities.constants.roles;
import az.codeworld.springboot.web.entities.CourseOffering;

public interface UserAdminProjection {
    Long getId();
    String getFirstName();
    String getLastName();
    String getEmail();
    Money getPayment();
    Instant getNextDate();
    Set<Role> getRoles(); 
    String getPassword();
    boolean isBanned();
    LocalDate getAffiliationDate();
    
    default String getRole() {
        return new ArrayList<>(getRoles()).get(0).getRoleNameString();
    }
}
