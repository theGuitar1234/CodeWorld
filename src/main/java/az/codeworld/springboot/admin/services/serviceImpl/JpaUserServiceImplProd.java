package az.codeworld.springboot.admin.services.serviceImpl;

import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import az.codeworld.springboot.admin.dtos.UserDTO;
import az.codeworld.springboot.admin.dtos.transactions.UserTransactionDTO;
import az.codeworld.springboot.admin.entities.User;
import az.codeworld.springboot.admin.repositories.UserRepository;
import az.codeworld.springboot.admin.services.UserService;
import az.codeworld.springboot.security.dtos.UserAuthDTO;

@Service
@Profile("prod")
public class JpaUserServiceImplProd implements UserService {

     private final UserRepository userRepository;
    
    public JpaUserServiceImplProd(
        UserRepository userRepository
    ) {
        this.userRepository = userRepository;
    }

    @Override
    public void defaultMethod() {}

    @Override
    public UserDTO getUserById(Long id) { return null; }

    @Override
    public UserDTO getUserByEmail(String email) { return null; }

    @Override
    public List<User> getAllUsersByEmail(String email) { return null; }

    @Override
    public List<User> getAll() { return null; }

    @Override
    public boolean existsUserByEmail(String email) { return false; }

    @Override
    public void saveUser(User user) {}

    @Override
    public void updateUser(Map<String, Object> field, String email) {}

    @Override
    public void deleteUserById(Long id) {}

    @Override
    public void deleteUserByEmail(String email) {}

    @Override
    public void removeUser(User user) {}

    @Override
    public void createNewUser(UserAuthDTO userAuthDTO) {}

    @Override
    public UserTransactionDTO getUserByUsername(String username) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getUserByUsername'");
    }
    
}
