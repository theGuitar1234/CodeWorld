package az.codeworld.springboot.admin.services;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import az.codeworld.springboot.admin.dtos.UserDTO;
import az.codeworld.springboot.admin.dtos.transactions.UserTransactionDTO;
import az.codeworld.springboot.admin.entities.User;
import az.codeworld.springboot.security.dtos.UserAuthDTO;

@Component
public interface UserService {
    void defaultMethod();

    UserDTO getUserById(Long id);
    UserDTO getUserByEmail(String email);
    UserTransactionDTO getUserByUsername(String username);
    List<User> getAllUsersByEmail(String email);
    List<User> getAll();

    boolean existsUserByEmail(String email);

    void saveUser(User user);

    void updateUser(Map<String, Object> field, String email);

    void deleteUserById(Long id);
    void deleteUserByEmail(String email);
    void deleteUserByUsername(String username);
    void removeUser(User user);

    void createNewUser(UserAuthDTO userAuthDTO);
}
