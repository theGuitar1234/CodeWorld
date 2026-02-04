package az.codeworld.springboot.security.services.authservices;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import az.codeworld.springboot.admin.dtos.RequestDTO;
import az.codeworld.springboot.admin.dtos.auth.UserAuthDTO;
import az.codeworld.springboot.admin.dtos.create.UserCreateDTO;
import az.codeworld.springboot.exceptions.PasswordsMustMatchException;

public interface RegistrationService {

    void sendAcceptanceEmail(RequestDTO requestDTO);
    void sendRejectionEmail(RequestDTO requestDTO);

    void registerUser(UserAuthDTO userAuthDTO);
}
