package az.codeworld.springboot.admin.services;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;

import az.codeworld.springboot.admin.dtos.TeacherDTO;
import az.codeworld.springboot.admin.dtos.UserDTO;
import az.codeworld.springboot.admin.dtos.auth.UserAuthDTO;
import az.codeworld.springboot.admin.dtos.transactions.UserPayableDTO;
import az.codeworld.springboot.admin.dtos.update.UserAdminUpdateDTO;
import az.codeworld.springboot.admin.dtos.update.UserUpdateDTO;
import az.codeworld.springboot.admin.entities.User;
import az.codeworld.springboot.admin.projections.UserAdminProjection;
import az.codeworld.springboot.admin.projections.UserLogoutProjection;
import az.codeworld.springboot.admin.records.UserAuthRecord;
import az.codeworld.springboot.admin.records.UserLatestRecord;
import az.codeworld.springboot.exceptions.UserNotFoundException;
import az.codeworld.springboot.security.dtos.LoginAuditDTO;
import az.codeworld.springboot.utilities.constants.accountstatus;
import az.codeworld.springboot.utilities.constants.dtotype;
import az.codeworld.springboot.utilities.constants.roles;

public interface UserService {
    void defaultMethod();

    Object getUserById(Long id, dtotype dtotype);
    Object getUserByEmail(String email, dtotype dtotype);
    Object getUserByUserName(String userName, dtotype dtotype);

    List<User> getAllUsersByEmail(String email);
    List<User> getAll();

    boolean existsUserByEmail(String email);

    void saveUser(User user);

    UserDTO updateUser(UserUpdateDTO userUpdateDTO, String userName) throws UserNotFoundException;

    void deleteUserById(Long id);
    void deleteUserByEmail(String email);
    void deleteUserByUserName(String userName);
    void removeUser(User user);

    Object createNewUser(UserAuthDTO userAuthDTO, dtotype dtotype);

    LoginAuditDTO getUserAudit(String email);

    void updateLastActiveAtByUserName(String userName);

    String getProfileImageId(String userName);

    void updateProfileImageId(String userName, String imageId);

    Long updatePassword(String userName, String password);

    Page<UserPayableDTO> getPaginatedPayableUsers(roles role, int pageIndex, int pageSize, String sortBy, Direction direction);

    UserAuthRecord getUserRecordByUserName(String userName);

    void enable2FA(String email);

    UserAdminProjection updateUserAdmin(UserAdminUpdateDTO userAdminUpdateDTO, Long userId) throws UserNotFoundException;

    void banUnbanUser(Long userId);

    int countTotalBannedUsers();
    int countTotalNewThisMonth();
    int countTotalActiveUsers();
    int countTotalInActiveUsers();
    Long countAllUsers();

    List<UserLatestRecord> getLatestUsers();

    <T> T getUserProjectionById(Long id, Class<T> type);
    <T> T getUserProjectionByUserName(String userName, Class<T> type);
}
