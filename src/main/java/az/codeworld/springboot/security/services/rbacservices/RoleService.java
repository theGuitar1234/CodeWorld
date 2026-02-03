package az.codeworld.springboot.security.services.rbacservices;

import java.util.Set;

import org.springframework.stereotype.Component;

import az.codeworld.springboot.security.entities.Role;

public interface RoleService {
    void saveRole(Role role);
    Role getRoleByRoleId(Long roleId);

    void addRolesToUser(Long userId, Set<Long> roleIds);
    void removeRolesFromUser(Long userId, Set<Long> roleIds);
}