package az.codeworld.springboot.security.services.rbacservices;

import java.util.Set;

import org.springframework.stereotype.Component;

import az.codeworld.springboot.security.entities.Role;

@Component
public interface RoleService {
    void saveRole(Role role);
    Role getRoleByRoleId(Long roleId);

    void addRolesToUser(String username, Set<Long> roleIds);
    void removeRolesFromUser(String email, Set<Long> roleIds);
}