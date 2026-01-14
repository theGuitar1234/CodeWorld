package az.codeworld.springboot.security.services.authservices.authservicesImpl;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import az.codeworld.springboot.admin.entities.User;
import az.codeworld.springboot.admin.repositories.UserRepository;
import az.codeworld.springboot.aop.LogExecutionTime;
import az.codeworld.springboot.security.entities.EmailOutbox;
import az.codeworld.springboot.security.entities.OtpCode;
import az.codeworld.springboot.security.records.EmailRequestedEvent;
import az.codeworld.springboot.security.repositories.OtpRepository;
import az.codeworld.springboot.security.services.authservices.OtpService;
import az.codeworld.springboot.security.services.emailservices.EmailOutboxService;
import az.codeworld.springboot.security.services.emailservices.EmailService;
import az.codeworld.springboot.utilities.configurations.ApplicationProperties;
import az.codeworld.springboot.utilities.generators.OtpGenerator;
import az.codeworld.springboot.web.services.ThymeleafService;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;

@Service
public class OtpServiceImpl implements OtpService {

    private final OtpRepository otpRepository;
    private final UserRepository userRepository;

    private final EmailOutboxService emailOutboxService;
    private final ThymeleafService thymeleafService;

    private final ApplicationEventPublisher applicationEventPublisher;
    private final ApplicationProperties applicationProperties;

    public OtpServiceImpl(
        OtpRepository otpRepository, 
        UserRepository userRepository, 
        EmailOutboxService emailOutboxService,
        ThymeleafService thymeleafService,
        ApplicationEventPublisher applicationEventPublisher,
        ApplicationProperties applicationProperties
    ) {
        this.otpRepository = otpRepository;
        this.userRepository = userRepository;
        this.emailOutboxService = emailOutboxService;
        this.thymeleafService = thymeleafService;
        this.applicationEventPublisher = applicationEventPublisher;
        this.applicationProperties = applicationProperties;
    }

    @Override
    @Transactional
    public void createOtpCode(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (user.getOtpCode() != null) {
                OtpCode otpCode = user.getOtpCode();
                user.setOtpCode(null);
                otpRepository.delete(otpCode);
                otpRepository.flush();
            }

            String otpCode = OtpGenerator.generateOtp();
            long expiresAt = Instant.now().plus(Duration.ofMinutes(applicationProperties.getOtp().getExpiryMinutes())).toEpochMilli();

            OtpCode otpCodeTemp = new OtpCode();
            otpCodeTemp.setEmail(email);
            otpCodeTemp.setOtpCode(otpCode);
            otpCodeTemp.setExpiresAtEpochMillis(expiresAt);
            user.setOtpCode(otpCodeTemp);
            otpCodeTemp.setUser(user);

            otpRepository.save(otpCodeTemp);
            
            sendOtpCode(email, otpCode);
        } else {
            throw new RuntimeException("Email not Found");
        }
    }

    @LogExecutionTime("sendOtpCode")
    @Override
    public void sendOtpCode(String email, String otpCode) {
        String html = thymeleafService.render(
            "auth/otp-code/otp_code",
            Map.of(
                "email", email,
                "otpCode", otpCode,
                "date", LocalDateTime.now().plusMinutes(applicationProperties.getOtp().getExpiryMinutes())
            )
        );

        EmailOutbox emailOutbox = EmailOutbox  
            .pending(
                email, 
                "Here is your OtpCode!", 
                html
            );
        
        emailOutboxService.saveEmailOutbox(emailOutbox);
        
        applicationEventPublisher.publishEvent(new EmailRequestedEvent(emailOutbox.getOutBoxId()));
    }

    @Override
    public boolean isOtpValid(String email, String otpCode) {
        Optional<OtpCode> otpCodeOptional = otpRepository.findByEmailAndOtpCode(email, otpCode);
        
        if (otpCodeOptional.isPresent() && otpCodeOptional.get().getExpiresAtEpochMillis() > System.currentTimeMillis()) {
            otpRepository.delete(otpCodeOptional.get());
            otpRepository.flush();
            return true;
        }
        return false;
    }
}
