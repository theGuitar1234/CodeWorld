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

        // File file = new File("D:\\Payyed\\BOOGER_AIDS");
        // try {
        //     Files.write(Paths.get(file.getAbsolutePath()), Arrays.asList(user.toString()), StandardOpenOption.APPEND);
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }

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
