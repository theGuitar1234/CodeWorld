package az.codeworld.springboot.security.services.authservices.authservicesImpl;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Map;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;

import az.codeworld.springboot.admin.dtos.RequestDTO;
import az.codeworld.springboot.admin.dtos.UserDTO;
import az.codeworld.springboot.admin.dtos.auth.UserAuthDTO;
import az.codeworld.springboot.admin.dtos.auth.UserRequestDTO;
import az.codeworld.springboot.admin.dtos.create.UserCreateDTO;
import az.codeworld.springboot.admin.services.UserService;
import az.codeworld.springboot.admin.services.serviceImpl.JpaUserServiceImplDev;
import az.codeworld.springboot.aop.LogExecutionTime;
import az.codeworld.springboot.exceptions.PasswordsMustMatchException;
import az.codeworld.springboot.security.entities.EmailOutbox;
import az.codeworld.springboot.security.records.EmailRequestedEvent;
import az.codeworld.springboot.security.services.authservices.RegistrationService;
import az.codeworld.springboot.security.services.emailservices.EmailOutboxService;
import az.codeworld.springboot.utilities.constants.dtotype;
import az.codeworld.springboot.web.services.ThymeleafService;
import jakarta.transaction.Transactional;

@Service
public class RegistrationServiceImpl implements RegistrationService {

    private final EmailOutboxService emailOutboxService;
    private final UserService userService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ThymeleafService thymeleafService;

    private final String port;

    public RegistrationServiceImpl(
        UserService userService,
        EmailOutboxService emailOutboxService,
        ApplicationEventPublisher applicationEventPublisher,
        ThymeleafService thymeleafService,
        String getPort
    ) {
        this.userService = userService;
        this.emailOutboxService = emailOutboxService;
        this.applicationEventPublisher = applicationEventPublisher;
        this.thymeleafService = thymeleafService;
        this.port = getPort;
    }

    @Override
    @Transactional
    @LogExecutionTime("sendAcceptanceEmail")
    public void sendAcceptanceEmail(RequestDTO requestDTO) {

        String html = thymeleafService.render(
            "auth/registration/accept-request", 
            Map.of(
                "name", requestDTO.getFirstName(),
                "verifyUrl", "http://localhost:" + port + "/user/register?token=" + requestDTO.getRequestToken(),
                "date", LocalDate.now().plusDays(1)
            )
        );

        EmailOutbox emailOutbox = EmailOutbox
            .pending(
                requestDTO.getEmail(), 
                "Your Request has been Accepted!", 
                html
            );

        emailOutboxService.saveEmailOutbox(emailOutbox);

        applicationEventPublisher.publishEvent(new EmailRequestedEvent(emailOutbox.getOutBoxId()));
    }

    @Override
    @Transactional
    @LogExecutionTime("sendRejectionEmail")
    public void sendRejectionEmail(RequestDTO requestDTO) {

        String html = thymeleafService.render(
            "auth/registration/reject-request", 
            Map.of(
                "name", requestDTO.getFirstName()
            )
        );

        EmailOutbox emailOutbox = EmailOutbox
            .pending(
                requestDTO.getEmail(), 
                "Your Request has been Rejected :(", 
                html
            );
            
        emailOutboxService.saveEmailOutbox(emailOutbox);

        applicationEventPublisher.publishEvent(new EmailRequestedEvent(emailOutbox.getOutBoxId()));
    }

    @Override
    @Transactional
    @LogExecutionTime("registerUser")
    public void registerUser(UserAuthDTO userAuthDTO) {

        UserRequestDTO userRequestDTO = (UserRequestDTO) userService.createNewRequestUser(userAuthDTO, dtotype.REQUEST);

        String url = "http://localhost:" + port + "/";

        String html = thymeleafService.render(
            "auth/registration/registration-success", 
            Map.of(
                "name", userAuthDTO.getFirstName(),
                "url", url,
                "username", userRequestDTO.getUsername()
            )
        );

        EmailOutbox emailOutbox = EmailOutbox
            .pending(
                userAuthDTO.getEmail(), 
                "Your have been successfully registered as a new user! :)", 
                html
            );

        emailOutboxService.saveEmailOutbox(emailOutbox);

        applicationEventPublisher.publishEvent(new EmailRequestedEvent(emailOutbox.getOutBoxId()));
    }

    public String getPort() {
        return port;
    }
    
}
