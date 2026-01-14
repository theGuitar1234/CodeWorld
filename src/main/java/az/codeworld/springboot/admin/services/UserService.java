package az.codeworld.springboot.admin.services;

import java.util.List;
import java.util.Map;

import az.codeworld.springboot.admin.dtos.UserDTO;
import az.codeworld.springboot.admin.dtos.update.UserUpdateDTO;
import az.codeworld.springboot.admin.entities.User;
import az.codeworld.springboot.exceptions.UserNotFoundException;
import az.codeworld.springboot.security.dtos.LoginAuditDTO;
import az.codeworld.springboot.security.dtos.UserAuthDTO;
import az.codeworld.springboot.utilities.constants.accountstatus;
import az.codeworld.springboot.utilities.constants.dtotype;

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
}
