package az.codeworld.springboot.security.services.authservices.authservicesImpl;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import az.codeworld.springboot.admin.dtos.RequestDTO;
import az.codeworld.springboot.admin.entities.User;
import az.codeworld.springboot.admin.repositories.UserRepository;
import az.codeworld.springboot.admin.services.LogoutService;
import az.codeworld.springboot.aop.LogExecutionTime;
import az.codeworld.springboot.security.entities.EmailOutbox;
import az.codeworld.springboot.security.entities.PasswordResetToken;
import az.codeworld.springboot.security.records.EmailRequestedEvent;
import az.codeworld.springboot.security.repositories.PasswordResetTokenRepository;
import az.codeworld.springboot.security.services.authservices.PasswordResetTokenService;
import az.codeworld.springboot.security.services.emailservices.EmailOutboxService;
import az.codeworld.springboot.utilities.generators.PasswordResetTokenGenerator;
import az.codeworld.springboot.web.services.ThymeleafService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

@Service
public class PasswordResetTokenServiceImpl implements PasswordResetTokenService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    private final PasswordEncoder passwordEncoder;

    private final LogoutService logoutService;

    private final ThymeleafService thymeleafService;

    private final EmailOutboxService emailOutboxService;
    private final ApplicationEventPublisher applicationEventPublisher;

    private final String port;

    public PasswordResetTokenServiceImpl(
        UserRepository userRepository,
        PasswordResetTokenRepository passwordResetTokenRepository,
        PasswordEncoder passwordEncoder,
        LogoutService logoutService,
        ThymeleafService thymeleafService,
        EmailOutboxService emailOutboxService,
        ApplicationEventPublisher applicationEventPublisher,
        String getPort
    ) {
        this.userRepository = userRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.logoutService = logoutService;
        this.thymeleafService = thymeleafService;
        this.emailOutboxService = emailOutboxService;
        this.applicationEventPublisher = applicationEventPublisher;
        this.port = getPort;
    }


    @Override
    @Transactional
    @LogExecutionTime("createPasswordResetToken")
    public void createPasswordResetToken(String email) {

        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (user.getPasswordResetToken() != null) {
                PasswordResetToken passwordResetToken = user.getPasswordResetToken();
                user.setPasswordResetToken(null);
                passwordResetTokenRepository.delete(passwordResetToken);
                passwordResetTokenRepository.flush();
            }

            String token = PasswordResetTokenGenerator.generateToken();
            long expirationDate = Instant.now().plus(Duration.ofMinutes(15)).toEpochMilli();
            
            PasswordResetToken passwordResetToken = new PasswordResetToken();
            passwordResetToken.setEmail(email);
            passwordResetToken.setToken(token);
            passwordResetToken.setExpiration_date(expirationDate);
            passwordResetToken.setUser(user);
            user.setPasswordResetToken(passwordResetToken);

            passwordResetTokenRepository.save(passwordResetToken);

            sendPasswordResetLink(email, token);
        } else {
            
            throw new RuntimeException("Email not Found");
        }
    }

    private void sendPasswordResetLink(String email, String token) {

        String html = thymeleafService.render(
            "auth/reset-password/reset_email", 
            Map.of(
                "email", email.substring(0, 3) + "*".repeat(email.length() - 3),
                "verifyUrl", "http://localhost:" + port + "/restricted/tokenResetPassword?token=" + token,
                "date", LocalDateTime.now().plusMinutes(15)
            )
        );

        EmailOutbox emailOutbox = EmailOutbox
            .pending(
                email,
                "Here is the link to reset your password!", 
                html
            );

        emailOutboxService.saveEmailOutbox(emailOutbox);

        applicationEventPublisher.publishEvent(new EmailRequestedEvent(emailOutbox.getOutBoxId()));
    }

    public String getPort() {
        return port;
    }

    @Override
    public boolean isTokenValid(String token) {
        Optional<PasswordResetToken> passwordResetTokenOptional = passwordResetTokenRepository.findByToken(token);

        if (passwordResetTokenOptional.isPresent() && passwordResetTokenOptional.get().getExpiration_date() > System.currentTimeMillis()) {
            passwordResetTokenRepository.delete(passwordResetTokenOptional.get());
            passwordResetTokenRepository.flush();
            return true;
        } else {
            return false;
        }
    }

    @Transactional
    public boolean tokenResetPassword(String token, String password, HttpServletRequest request, HttpServletResponse response) {
        Optional<PasswordResetToken> passwordResetTokenOptional = passwordResetTokenRepository.findByToken(token);
        PasswordResetToken passwordResetToken = passwordResetTokenOptional.orElseThrow(() -> new RuntimeException("Token Couldn't be found"));
        
        if (passwordResetToken != null && passwordResetToken.getExpiration_date() > System.currentTimeMillis()) {
            User user = passwordResetToken.getUser();
            user.setPassword(passwordEncoder.encode(password));
            userRepository.save(user);
            userRepository.flush();

            logoutService.exterminate(user.getId(), request, response);

            passwordResetTokenRepository.delete(passwordResetTokenOptional.get());
            passwordResetTokenRepository.flush();

            return true;
        } else {
            return false;
        }
    }
    
}
