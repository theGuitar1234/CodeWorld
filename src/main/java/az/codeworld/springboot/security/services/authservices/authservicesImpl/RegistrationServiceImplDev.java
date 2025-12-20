package az.codeworld.springboot.security.services.authservices.authservicesImpl;

import java.util.Map;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;

import az.codeworld.springboot.admin.dtos.RequestDTO;
import az.codeworld.springboot.admin.services.UserService;
import az.codeworld.springboot.admin.services.serviceImpl.JpaUserServiceImpl;
import az.codeworld.springboot.security.dtos.UserAuthDTO;
import az.codeworld.springboot.security.entities.EmailOutbox;
import az.codeworld.springboot.security.records.EmailRequestedEvent;
import az.codeworld.springboot.security.services.authservices.RegistrationService;
import az.codeworld.springboot.security.services.emailservices.EmailOutboxService;
import az.codeworld.springboot.web.services.ThymeleafService;
import jakarta.transaction.Transactional;

@Service
@Profile("dev")
public class RegistrationServiceImplDev implements RegistrationService {

    private final EmailOutboxService emailOutboxService;
    private final UserService userService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ThymeleafService thymeleafService;

    private final String port;

    public RegistrationServiceImplDev(
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
    public void sendAcceptanceEmail(RequestDTO requestDTO) {

        String html = thymeleafService.render(
            "auth/registration/accept-request", 
            Map.of(
                "name", requestDTO.getFirstName(),
                "verifyUrl", "http://localhost:" + port + "/user/register?token=" + requestDTO.getRequestToken() 
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
    public void registerUser(UserAuthDTO userAuthDTO) {

        String html = thymeleafService.render(
            "auth/registration/accept-request",
            Map.of(
                "name", userAuthDTO.getFirstName(),
                "verifyURL", "BOZO WAIT! COMING SOON!!!"
            )    
        );
        userService.createNewUser(userAuthDTO);

        EmailOutbox emailOutbox = EmailOutbox
            .pending(
                userAuthDTO.getEmail(), 
                "Your Request has been Accepted :)", 
                html
            );

        emailOutboxService.saveEmailOutbox(emailOutbox);

        applicationEventPublisher.publishEvent(new EmailRequestedEvent(emailOutbox.getOutBoxId()));
    }

    public String getPort() {
        return port;
    }
    
}
