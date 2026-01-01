package az.codeworld.springboot.admin.services.serviceImpl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import az.codeworld.springboot.admin.dtos.UserDTO;
import az.codeworld.springboot.admin.dtos.transactions.UserTransactionDTO;
import az.codeworld.springboot.admin.entities.User;
import az.codeworld.springboot.admin.mappers.UserMapper;
import az.codeworld.springboot.admin.mappers.UserTransactionMapper;
import az.codeworld.springboot.admin.repositories.UserRepository;
import az.codeworld.springboot.admin.services.UserService;
import az.codeworld.springboot.security.dtos.UserAuthDTO;
import az.codeworld.springboot.security.services.rbacservices.RoleService;
import az.codeworld.springboot.utilities.constants.roles;
import az.codeworld.springboot.utilities.generators.UsernameGenerator;
import jakarta.transaction.Transactional;

@Service
@Profile("dev")
public class JpaUserServiceImplDev implements UserService {

    private final UserRepository userRepository;

    private final RoleService roleService;
    
    public JpaUserServiceImplDev(
        UserRepository userRepository,
        RoleService roleService
    ) {
        this.userRepository = userRepository;
        this.roleService = roleService;
    }

    @Override
    public void saveUser(User user) {
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(LocalDateTime.now());
        }
        userRepository.save(user);
        userRepository.flush();
    }

    @Override
    public void defaultMethod() {}

    @Override
    public UserDTO getUserById(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        User user = userOptional.orElseThrow(() -> new RuntimeException("User not Found By ID"));
        return UserMapper.toUserDTO(
            user.getUsername(),
            user.getFirstName(), 
            user.getLastName(), 
            user.getEmail(),
            user.getCreatedAt() 
        );
    }

    @Override
    public UserTransactionDTO getUserByUsername(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        User user = userOptional.orElseThrow(() -> new RuntimeException("User Not Found By Username"));
        return UserTransactionMapper.toUserTransactionDTO(
            user.getTransactions()
        );
    }

    @Override
    public UserDTO getUserByEmail(String email) { return null; }

    @Override
    public List<User> getAllUsersByEmail(String email) { return null; }

    @Override
    public List<User> getAll() { return null; }

    @Override
    public boolean existsUserByEmail(String email) { return false; }

    @Override
    public void updateUser(Map<String, Object> field, String email) {}

    @Override
    public void deleteUserById(Long id) {}

    @Override
    public void deleteUserByEmail(String email) {}

    @Override
    public void removeUser(User user) {}

    @Override
    @Transactional
    public UserDTO createNewUser(UserAuthDTO userAuthDTO) {
        User user = new User();
        user.setUsername(UsernameGenerator.generateUsername(userAuthDTO.getRole().getRoleNameString()));
        user.setFirstName(userAuthDTO.getFirstName());
        user.setLastName(userAuthDTO.getLastName());
        user.setEmail(userAuthDTO.getEmail());
        user.setPassword(userAuthDTO.getPassword());

        saveUser(user);

        roleService.addRolesToUser(user.getUsername(), Set.of(roles.USER.getRoleId(), userAuthDTO.getRole().getRoleId()));

        return UserMapper.toUserDTO(
            user.getUsername(),
            user.getFirstName(), 
            user.getLastName(), 
            user.getEmail(),
            user.getCreatedAt() 
        );
    }

    @Override
    public void deleteUserByUsername(String username) {
        userRepository.deleteByUsername(username);
        userRepository.flush();
    }
    
}
