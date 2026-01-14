package az.codeworld.springboot.security.services.authservices;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import az.codeworld.springboot.admin.dtos.RequestDTO;
import az.codeworld.springboot.security.dtos.UserAuthDTO;

public interface RegistrationService {

    void registerUser(UserAuthDTO userAuthDTO);

    void sendAcceptanceEmail(RequestDTO requestDTO);
    void sendRejectionEmail(RequestDTO requestDTO);
}
