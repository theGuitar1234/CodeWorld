package az.codeworld.springboot.security.services.rbacservices.rbacservicesImpl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;

import az.codeworld.springboot.security.entities.Role;
import az.codeworld.springboot.security.repositories.RoleRepository;
import az.codeworld.springboot.security.services.rbacservices.RoleService;
import az.codeworld.springboot.admin.entities.User;
import az.codeworld.springboot.admin.repositories.UserRepository;
import jakarta.transaction.Transactional;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public RoleServiceImpl(RoleRepository roleRepository, UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void saveRole(Role role) {
        roleRepository.save(role);
        roleRepository.flush();
    }

    @Override
    public Role getRoleByRoleId(Long roleId) {
        Optional<Role> roleOptional = roleRepository.findById(roleId);
        Role role = roleOptional.orElseThrow(() -> new RuntimeException("Role does not exist"));
        return role;
    }

    @Override
    @Transactional
    public void addRolesToUser(Long userId, Set<Long> roleIds) {
        
        Optional<User> userOptional = userRepository.findById(userId);
        User user = userOptional.orElseThrow(() -> new RuntimeException("User Not Foumd"));

        for (Long roleId : roleIds) {
            Role role = getRoleByRoleId(roleId);
            user.addRole(role);

            userRepository.save(user);
            userRepository.flush();

            saveRole(role);
        }
    }

    @Override
    @Transactional
    public void removeRolesFromUser(Long userId, Set<Long> roleIds) {
        Optional<User> userOptional = userRepository.findById(userId);
        User user = userOptional.orElseThrow(() -> new RuntimeException("User Not Found"));

        for (long roleId : roleIds) {
            Role role = getRoleByRoleId(roleId);
            user.removeRole(role);

            userRepository.save(user);
            userRepository.flush();

            saveRole(role);
        }
    }

}
