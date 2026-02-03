package az.codeworld.springboot.security.services.auditservices.auditservicesImpl;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

import org.springframework.stereotype.Service;

import az.codeworld.springboot.admin.entities.User;
import az.codeworld.springboot.admin.repositories.UserRepository;
import az.codeworld.springboot.security.entities.LoginAudit;
import az.codeworld.springboot.security.repositories.LoginAuditRepository;
import az.codeworld.springboot.security.services.auditservices.AuditService;
import az.codeworld.springboot.utilities.configurations.ApplicationProperties;
import az.codeworld.springboot.utilities.constants.accountstatus;
import jakarta.transaction.Transactional;

@Service
public class AuditServiceImpl implements AuditService {

    private final ApplicationProperties applicationProperties;

    private final UserRepository userRepository;
    private final LoginAuditRepository loginAuditRepository;

    public AuditServiceImpl(
        UserRepository userRepository, 
        ApplicationProperties applicationProperties,
        LoginAuditRepository loginAuditRepository
    ) {
        this.userRepository = userRepository;
        this.loginAuditRepository = loginAuditRepository;
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
        user.getLoginAudit().setBlocked(false);
        user.getLoginAudit().setBlockExpiry(null);
        user.setAccountStatus(accountstatus.UNLOCKED);

        userRepository.save(user);
        userRepository.flush();
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
                user.setAccountStatus(accountstatus.LOCKED);
            } else {
                user.getLoginAudit().setFailedAttempts(user.getLoginAudit().getFailedAttempts() + 1);
            }
            
            userRepository.save(user);
            userRepository.flush();
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
            user.getLoginAudit().setBlockExpiry(null);
            user.getLoginAudit().setFailedAttempts(0);
            user.setAccountStatus(accountstatus.UNLOCKED);

            userRepository.save(user);
            userRepository.flush();
        }
    }

    @Override
    public long countTotalLoginsThisMonth() {
        ZoneId zone = ZoneId.of(applicationProperties.getTime().getZone());
        Instant start = LocalDate.now(zone).withDayOfMonth(1).atStartOfDay(zone).toInstant();
        Instant end = LocalDate.now(zone).withDayOfMonth(1).plusMonths(1).atStartOfDay(zone).toInstant();
        return loginAuditRepository.countByLastLoginAtIsBetween(start, end);
    }
}
