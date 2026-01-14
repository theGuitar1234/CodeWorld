package az.codeworld.springboot.admin.services.serviceImpl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import az.codeworld.springboot.admin.dtos.UserDTO;
import az.codeworld.springboot.admin.dtos.update.UserUpdateDTO;
import az.codeworld.springboot.admin.entities.User;
import az.codeworld.springboot.admin.mappers.UserMapper;
import az.codeworld.springboot.admin.repositories.UserRepository;
import az.codeworld.springboot.admin.services.UserService;
import az.codeworld.springboot.exceptions.UserNotFoundException;
import az.codeworld.springboot.security.dtos.LoginAuditDTO;
import az.codeworld.springboot.security.dtos.UserAuthDTO;
import az.codeworld.springboot.security.services.rbacservices.RoleService;
import az.codeworld.springboot.utilities.constants.accountstatus;
import az.codeworld.springboot.utilities.constants.dtotype;
import az.codeworld.springboot.utilities.constants.roles;
import az.codeworld.springboot.utilities.generators.UsernameGenerator;
import jakarta.transaction.Transactional;

@Service
public class JpaUserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    public JpaUserServiceImpl(
        UserRepository userRepository,
        RoleService roleService,
        PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
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
    public void defaultMethod() {
    }

    @Override
    public Object getUserById(Long id, dtotype dtotype) {
        Optional<User> userOptional = userRepository.findById(id);
        User user = userOptional.orElseThrow(() -> new RuntimeException("User not Found By ID"));
        return UserMapper.toUserDTO(user, dtotype.getDtoTypeString());
    }

    @Override
    public Object getUserByUserName(String username, dtotype dtotype) {
        Optional<User> userOptional = userRepository.findByUserName(username);
        User user = userOptional.orElseThrow(() -> new RuntimeException("User Not Found By Username"));

        return UserMapper.toUserDTO(user, dtotype.getDtoTypeString());
    }

    @Override
    public UserDTO getUserByEmail(String email, dtotype dtotype) {
        return null;
    }

    @Override
    public List<User> getAllUsersByEmail(String email) {
        return null;
    }

    @Override
    public List<User> getAll() {
        return null;
    }

    @Override
    public boolean existsUserByEmail(String email) {
        return false;
    }

    @Override
    public void deleteUserById(Long id) {
    }

    @Override
    public void deleteUserByEmail(String email) {
    }

    @Override
    public void removeUser(User user) {
    }

    @Override
    @Transactional
    public Object createNewUser(UserAuthDTO userAuthDTO, dtotype dtotype) {
        User user = new User();
        user.setUserName(UsernameGenerator.generateUsername(userAuthDTO.getRole().getRoleNameString()));
        user.setFirstName(userAuthDTO.getFirstName());
        user.setLastName(userAuthDTO.getLastName());
        user.setEmail(userAuthDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userAuthDTO.getPassword()));

        saveUser(user);

        roleService.addRolesToUser(user.getUserName(),
                Set.of(roles.USER.getRoleId(), userAuthDTO.getRole().getRoleId()));

        return UserMapper.toUserDTO(user, dtotype.getDtoTypeString());
    }

    @Override
    @Transactional
    public void updateLastActiveAtByUserName(String userName) {
        userRepository.updateLastActiveAtByUserName(
            userName, 
            LocalDateTime.now(), 
            LocalDateTime.now()
                .minusSeconds(30L)
        );
    }

    @Override
    public void deleteUserByUserName(String userName) {
        userRepository.deleteByUserName(userName);
        userRepository.flush();
    }

    @Override
    public LoginAuditDTO getUserAudit(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        User user = userOptional.orElseThrow(() -> new RuntimeException("User Not Found By Email"));
        if (user.getLoginAudit() != null) {
            return (LoginAuditDTO) UserMapper.toUserDTO(user, dtotype.LOGIN_AUDIT.getDtoTypeString());
        }
        return null;
    }

    @Override
    public UserDTO updateUser(UserUpdateDTO userUpdateDTO, String userName) throws UserNotFoundException {
        Optional<User> userOptional = userRepository.findByUserName(userName);
        User user = userOptional.orElseThrow(() -> new RuntimeException("User not Found"));

        if (userUpdateDTO.getFirstName() != null) user.setFirstName(userUpdateDTO.getFirstName());
        if (userUpdateDTO.getLastName() != null) user.setLastName(userUpdateDTO.getLastName());
        if (userUpdateDTO.getEmail() != null) user.setEmail(userUpdateDTO.getEmail());
        if (userUpdateDTO.getBirthDate() != null) user.setBirthDate(LocalDate.parse(userUpdateDTO.getBirthDate()));
        if (userUpdateDTO.getStreet() != null) user.setStreet(userUpdateDTO.getStreet());
        if (userUpdateDTO.getCity() != null) user.setCity(userUpdateDTO.getCity());
        if (userUpdateDTO.getRegion() != null) user.setRegion(userUpdateDTO.getRegion());
        if (userUpdateDTO.getPostalCode() != null) user.setPostalCode(userUpdateDTO.getPostalCode());
        if (userUpdateDTO.getCountry() != null) user.setCountry(userUpdateDTO.getCountry());
        if (userUpdateDTO.getLanguage() != null) user.setLanguage(userUpdateDTO.getLanguage());
        if (userUpdateDTO.getTimeZone() != null) user.setZoneId(userUpdateDTO.getTimeZone());
        if (userUpdateDTO.getPhoneNumber() != null) user.setPhoneNumber(userUpdateDTO.getPhoneNumber());
        if (userUpdateDTO.getAge() != null) user.setAge(userUpdateDTO.getAge());

        saveUser(user);

        return (UserDTO) UserMapper.toUserDTO(user, dtotype.FULL.getDtoTypeString());
    }

    @Override
    public String getProfileImageId(String userName) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getProfileImageId'");
    }

    @Override
    public void updateProfileImageId(String userName, String imageId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateProfileImageId'");
    }

}
