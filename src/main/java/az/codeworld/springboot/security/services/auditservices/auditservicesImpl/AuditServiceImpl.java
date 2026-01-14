package az.codeworld.springboot.security.services.auditservices.auditservicesImpl;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import org.springframework.stereotype.Service;

import az.codeworld.springboot.admin.entities.User;
import az.codeworld.springboot.admin.repositories.UserRepository;
import az.codeworld.springboot.security.entities.LoginAudit;
import az.codeworld.springboot.security.services.auditservices.AuditService;
import az.codeworld.springboot.utilities.configurations.ApplicationProperties;
import jakarta.transaction.Transactional;

@Service
public class AuditServiceImpl implements AuditService {

    private final ApplicationProperties applicationProperties;

    private final UserRepository userRepository;

    public AuditServiceImpl(UserRepository userRepository, ApplicationProperties applicationProperties) {
        this.userRepository = userRepository;
        this.applicationProperties = applicationProperties;
    }

    @Override
    @Transactional
    public void recordLogin(String userName, String ip) {
        User user = userRepository.findByUserName(userName).orElseThrow(() -> new RuntimeException("User not Found"));
        if (user.getLoginAudit() == null) {
            LoginAudit audit = new LoginAudit();
            user.setLoginAudit(audit);
            audit.setUser(user);
        }
        user.getLoginAudit().setLastLoginAt(Instant.now());
        user.getLoginAudit().setLastLoginIp(ip);
        user.getLoginAudit().setFailedAttempts(0);
        user.getLoginAudit().setLoginCount(user.getLoginAudit().getLoginCount() + 1);
    }

    @Override
    @Transactional
    public void recordFailure(String userName) {
        Optional<User> userOptional = userRepository.findByUserName(userName);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getLoginAudit() == null) {
                LoginAudit audit = new LoginAudit();
                user.setLoginAudit(audit);
                audit.setUser(user);
            }

            if (user.getLoginAudit().getFailedAttempts() >= applicationProperties.getLogin().getAttempts().getThreshold()-1) {
                user.getLoginAudit().setBlockExpiry(Instant.now().plus(Duration.ofMinutes(applicationProperties.getLogin().getBlock().getExpiryMinutes())).toEpochMilli());
                user.getLoginAudit().setBlocked(true);
            } else {
                user.getLoginAudit().setFailedAttempts(user.getLoginAudit().getFailedAttempts() + 1);
            }
            
            userRepository.save(user);
        }
    }

    @Override
    @Transactional 
    public void unBlockAccount(String userName) {
        Optional<User> userOptional = userRepository.findByUserName(userName);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getLoginAudit() == null) {
                LoginAudit audit = new LoginAudit();
                user.setLoginAudit(audit);
                audit.setUser(user);
            }
            user.getLoginAudit().setBlocked(false);
            user.getLoginAudit().setBlockExpiry(0);
            user.getLoginAudit().setFailedAttempts(0);
            userRepository.save(user);
        }
    }
}
