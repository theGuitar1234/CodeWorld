package az.codeworld.springboot.admin.services.serviceImpl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import az.codeworld.springboot.admin.dtos.UserDTO;
import az.codeworld.springboot.admin.dtos.auth.UserAuthDTO;
import az.codeworld.springboot.admin.dtos.transactions.UserPayableDTO;
import az.codeworld.springboot.admin.dtos.update.UserAdminUpdateDTO;
import az.codeworld.springboot.admin.dtos.update.UserUpdateDTO;
import az.codeworld.springboot.admin.entities.Money;
import az.codeworld.springboot.admin.entities.User;
import az.codeworld.springboot.admin.mappers.UserMapper;
import az.codeworld.springboot.admin.projections.UserAdminProjection;
import az.codeworld.springboot.admin.projections.UserLogoutProjection;
import az.codeworld.springboot.admin.records.UserAuthRecord;
import az.codeworld.springboot.admin.records.UserLatestRecord;
import az.codeworld.springboot.admin.repositories.UserRepository;
import az.codeworld.springboot.admin.services.UserService;
import az.codeworld.springboot.exceptions.UserNotFoundException;
import az.codeworld.springboot.security.dtos.LoginAuditDTO;
import az.codeworld.springboot.security.entities.Role;
import az.codeworld.springboot.security.services.rbacservices.RoleService;
import az.codeworld.springboot.utilities.configurations.ApplicationProperties;
import az.codeworld.springboot.utilities.constants.accountstatus;
import az.codeworld.springboot.utilities.constants.dtotype;
import az.codeworld.springboot.utilities.constants.roles;
import az.codeworld.springboot.utilities.generators.UsernameGenerator;
import jakarta.transaction.Transactional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Caching;

@Service
@Profile("prod")
public class JpaUserServiceImplProd implements UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationProperties applicationProperties;

    public JpaUserServiceImplProd(
        UserRepository userRepository,
        RoleService roleService,
        PasswordEncoder passwordEncoder,
        ApplicationProperties applicationProperties
    ) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.applicationProperties = applicationProperties;
    }

    @Override
    public void saveUser(User user) {
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(Instant.now());
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
    @Cacheable(
        cacheNames = "usersByUsernameFull",
        key = "#userName",
        condition = "#dtotype == T(az.codeworld.springboot.utilities.constants.dtotype).FULL"
    )
    public Object getUserByUserName(String userName, dtotype dtotype) {
        Optional<User> userOptional = userRepository.findByUserName(userName);
        User user = userOptional.orElseThrow(() -> new RuntimeException("User Not Found By Username"));

        return UserMapper.toUserDTO(user, dtotype.getDtoTypeString());
    }

    @Override
    @Cacheable(cacheNames = "userAuthRecordByUsername", key = "#userName")
    public UserAuthRecord getUserRecordByUserName(String userName) {
        Optional<User> userOptional = userRepository.findByUserName(userName);
        User user = userOptional.orElseThrow(() -> new RuntimeException("User Not Found By Username"));
        return new UserAuthRecord(user.getEmail());
    }

    @Override
    public UserDTO getUserByEmail(String email, dtotype dtotype) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        User user = userOptional.orElseThrow(() -> new RuntimeException("User Not Found By Email"));
        return (UserDTO) UserMapper.toUserDTO(user, dtotype.getDtoTypeString());
    }

    @Override
    public List<User> getAllUsersByEmail(String email) {
        return userRepository.findAllByEmail(email);
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public boolean existsUserByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
        userRepository.flush();
    }

    @Override
    public void deleteUserByEmail(String email) {
        userRepository.deleteByEmail(email);
        userRepository.flush();
    }

    @Override
    public void removeUser(User user) {
        userRepository.delete(user);
        userRepository.flush();
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

        roleService.addRolesToUser(user.getId(),
                Set.of(roles.USER.getRoleId(), userAuthDTO.getRole().getRoleId()));

        return UserMapper.toUserDTO(user, dtotype.getDtoTypeString());
    }

    @Override
    @Transactional
    public void updateLastActiveAtByUserName(String userName) {
        userRepository.updateLastActiveAtByUserName(
            userName, 
            Instant.now(), 
            Instant.now()
                .minusSeconds(30L)
        );
    }

    @Override
    @Caching(evict = {
        @CacheEvict(cacheNames = "usersByUsernameFull", key = "#userName"),
        @CacheEvict(cacheNames = "userAuthRecordByUsername", key = "#userName"),
        @CacheEvict(cacheNames = "profileImageIdByUsername", key = "#userName")
    })
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
    @Caching(
        put = {
            @CachePut(cacheNames = "usersByUsernameFull", key = "#userName"),
            @CachePut(cacheNames = "usersByIdFull", key = "#result.id")
        },
        evict = {
            @CacheEvict(cacheNames = "userAuthRecordByUsername", key = "#userName")
        }
    )
    public UserDTO updateUser(UserUpdateDTO userUpdateDTO, String userName) throws UserNotFoundException {
        Optional<User> userOptional = userRepository.findByUserName(userName);
        User user = userOptional.orElseThrow(() -> new UserNotFoundException("User not Found"));

        if (userUpdateDTO.getFirstName() != null && !userUpdateDTO.getFirstName().isEmpty()) user.setFirstName(userUpdateDTO.getFirstName());
        if (userUpdateDTO.getLastName() != null && !userUpdateDTO.getLastName().isEmpty()) user.setLastName(userUpdateDTO.getLastName());
        if (userUpdateDTO.getEmail() != null && !userUpdateDTO.getEmail().isEmpty()) user.setEmail(userUpdateDTO.getEmail());
        if (userUpdateDTO.getBirthDate() != null && !userUpdateDTO.getBirthDate().isEmpty()) user.setBirthDate(LocalDate.parse(userUpdateDTO.getBirthDate()));
        if (userUpdateDTO.getStreet() != null && !userUpdateDTO.getStreet().isEmpty()) user.setStreet(userUpdateDTO.getStreet());
        if (userUpdateDTO.getCity() != null && !userUpdateDTO.getCity().isEmpty()) user.setCity(userUpdateDTO.getCity());
        if (userUpdateDTO.getRegion() != null && !userUpdateDTO.getRegion().isEmpty()) user.setRegion(userUpdateDTO.getRegion());
        if (userUpdateDTO.getPostalCode() != null) user.setPostalCode(userUpdateDTO.getPostalCode());
        if (userUpdateDTO.getCountry() != null && !userUpdateDTO.getCountry().isEmpty()) user.setCountry(userUpdateDTO.getCountry());
        if (userUpdateDTO.getLanguage() != null && !userUpdateDTO.getLanguage().isEmpty()) user.setLanguage(userUpdateDTO.getLanguage());
        if (userUpdateDTO.getTimeZone() != null && !userUpdateDTO.getTimeZone().isEmpty()) user.setZoneId(userUpdateDTO.getTimeZone());
        if (userUpdateDTO.getPhoneNumber() != null && !userUpdateDTO.getPhoneNumber().isEmpty()) user.setPhoneNumber(userUpdateDTO.getPhoneNumber());
        if (userUpdateDTO.getAge() != null) user.setAge(userUpdateDTO.getAge());

        saveUser(user);

        return (UserDTO) UserMapper.toUserDTO(user, dtotype.FULL.getDtoTypeString());
    }

    @Cacheable(
        cacheNames = "profileImageIdByUsername",
        key = "#userName",
        unless = "#result == null"
    )
    public String getProfileImageId(String userName) {
        return userRepository.findByUserName(userName)
                .map(User::getProfileImageId)
                .orElse(null);
    }

    @Caching(evict = {
        @CacheEvict(cacheNames = "profileImageIdByUsername", key = "#userName"),
        @CacheEvict(cacheNames = "usersByUsernameFull", key = "#userName")
    })
    public void updateProfileImageId(String userName, String imageId) {
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new RuntimeException("User Not Found By Username"));
        user.setProfileImageId(imageId);
        user.setProfileImageUpdatedAt(java.time.Instant.now());
        saveUser(user);
    }

    @Override
    @CacheEvict(cacheNames = "usersByUsernameFull", key = "#userName")
    public Long updatePassword(String userName, String password) {
        User user = userRepository.findByUserName(userName)
            .orElseThrow(() -> new RuntimeException("User Not Found Bozo"));
        user.setPassword(passwordEncoder.encode(password));
        saveUser(user);
        return user.getId();
    }

    @Override
    public Page<UserPayableDTO> getPaginatedPayableUsers(roles role, int pageIndex, int pageSize, String sortBy,
            Direction direction) {
        
        Role roleEntity = roleService.getRoleByRoleId(role.getRoleId());

        return userRepository
            .findByRolesAndBillingEnabledTrueAndNextDateLessThanEqual(
                roleEntity,
                Instant.now(), 
                true,
                PageRequest.of(pageIndex, pageSize)
            )
            .map(u -> (UserPayableDTO) UserMapper.toUserDTO(u, dtotype.PAYABLE.getDtoTypeString()));
    }

    @Override
    public void enable2FA(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        User user = userOptional.orElseThrow(() -> new RuntimeException("User Not Found"));
        user.setTwoFactorEnabled(true);
        saveUser(user);
    }

    @Override
    public UserAdminProjection updateUserAdmin(UserAdminUpdateDTO userAdminUpdateDTO, Long userId) throws UserNotFoundException {
        Optional<User> userOptional = userRepository.findById(userId);
        User user = userOptional.orElseThrow(() -> new UserNotFoundException("User not Found"));

        if (userAdminUpdateDTO.getPassword() != null && !userAdminUpdateDTO.getPassword().isBlank()) {
            if (!userAdminUpdateDTO.getPassword().equals(userAdminUpdateDTO.getPassword2())) throw new IllegalArgumentException("Passwords must match");
            user.setPassword(passwordEncoder.encode(userAdminUpdateDTO.getPassword()));
        }
        if (userAdminUpdateDTO.getPayment() != null) user.setPayment(new Money(userAdminUpdateDTO.getPayment(), user.getPayment().getCurrency()));
        if (userAdminUpdateDTO.getNextDate() != null) user.setNextDate(userAdminUpdateDTO.getNextDate().atStartOfDay(ZoneId.of(applicationProperties.getTime().getZone())).toInstant());
        if (userAdminUpdateDTO.getAffiliationDate() != null) user.setAffiliationDate(userAdminUpdateDTO.getAffiliationDate());

        saveUser(user);

        return getUserProjectionById(userId, UserAdminProjection.class);
    }

    @Override
    @Transactional
    public void banUnbanUser(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        User user = userOptional.orElseThrow(() -> new UserNotFoundException("User Not Found By ID"));
        boolean isBanned = user.getRoles()
            .stream()
            .anyMatch(r -> "BANNED".equals(r.getRoleNameString()));
        
        if (isBanned) {
            roleService.removeRolesFromUser(userId, Set.of(roles.BANNED.getRoleId()));
            user.setAccountStatus(accountstatus.UNBANNED);
        } else {
            roleService.addRolesToUser(userId, Set.of(roles.BANNED.getRoleId()));
            user.setAccountStatus(accountstatus.BANNED);
        }

        saveUser(user);
    }

    @Override
    public <T> T getUserProjectionById(Long id, Class<T> type) {
        return userRepository.findById(id, type)
            .orElseThrow(() -> new RuntimeException("UserProjection Not Found By ID"));
    }

    @Override
    public <T> T getUserProjectionByUserName(String userName, Class<T> type) {
       return userRepository.findByUserName(userName, type)
        .orElseThrow(() -> new RuntimeException("UserProjection Not Found By Username"));
    }

    @Override
    public int countTotalBannedUsers() {
        return userRepository.countByIsBanned(true);
    }

    @Override
    public int countTotalNewThisMonth() {
        Instant cutoff = Instant.now().minus(Duration.ofDays(30));
        return userRepository.countByCreatedAtAfter(cutoff);
    }

    @Override
    public int countTotalInActiveUsers() {
        Instant cutoff = Instant.now().minus(Duration.ofMinutes(5));
        return userRepository.countByLastActiveAtBeforeOrLastActiveAtIsNull(cutoff);
    }

    @Override
    public int countTotalActiveUsers() {
        Instant cutoff = Instant.now().minus(Duration.ofMinutes(5));
        return userRepository.countByLastActiveAtAfter(cutoff);
    }

    @Override
    public Long countAllUsers() {
        return userRepository.countAll(); 
    }

    @Override
    public List<UserLatestRecord> getLatestUsers() {
        ZoneId zone = ZoneId.of(applicationProperties.getTime().getZone());
        Instant cutoff = LocalDate.now(zone).atStartOfDay(zone).toInstant();
        return userRepository.findTop10ByCreatedAtGreaterThanEqualOrderByCreatedAtDesc(cutoff)
            .stream()
            .map(u -> new UserLatestRecord(u.getEmail(), u.getCreatedAt(), u.getProfilePicture().getProfilePhoto()))
            .toList();
    }

}
