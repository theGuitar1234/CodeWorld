package az.codeworld.springboot.security.services.serviceImpl;

import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;

import az.codeworld.springboot.security.entities.Role;
import az.codeworld.springboot.security.repositories.RoleRepository;
import az.codeworld.springboot.security.services.RoleService;
import az.codeworld.springboot.utilities.constants.roles;
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
    }

    @Override
    public Role getRoleByRoleId(Long roleId) {
        Optional<Role> roleOptional = roleRepository.findById(roleId);
        Role role = roleOptional.orElseThrow(() -> new RuntimeException("Role does not exist"));
        return role;
    }

    @Override
    @Transactional
    public void addRolesToUser(String username, Set<Long> roleIds) {
        
        Optional<User> userOptional = userRepository.findByUsername(username);
        User user = userOptional.orElseThrow(() -> new RuntimeException("User Not Foumd"));

        for (Long roleId : roleIds) {
            Role role = getRoleByRoleId(roleId);
            user.addRole(role);
        }
    }

    @Override
    @Transactional
    public void removeRolesFromUser(String email, Set<Long> roleIds) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        User user = userOptional.orElseThrow(() -> new RuntimeException("User Not Found"));

        for (long roleId : roleIds) {
            user.removeRole(getRoleByRoleId(roleId));
        }
    }

}
