package az.codeworld.springboot.security.services.emailservices.emailservicesImpl;

import java.time.Instant;
import java.util.Optional;

import org.springframework.stereotype.Service;

import az.codeworld.springboot.security.entities.EmailOutbox;
import az.codeworld.springboot.security.repositories.EmailOutBoxRepository;
import az.codeworld.springboot.security.services.emailservices.EmailOutboxService;

@Service
public class EmailOutboxServiceImpl implements EmailOutboxService {

    private final EmailOutBoxRepository emailOutBoxRepository;

    public EmailOutboxServiceImpl(
        EmailOutBoxRepository emailOutBoxRepository
    ) {
        this.emailOutBoxRepository = emailOutBoxRepository;
    }

    @Override
    public void defaultMethod() {}

    @Override
    public void saveEmailOutbox(EmailOutbox emailOutbox) {
        if (emailOutbox.getCreatedAt() == null) {
            emailOutbox.setCreatedAt(Instant.now());
        }
        emailOutBoxRepository.save(emailOutbox);
        emailOutBoxRepository.flush();
    }

    @Override
    public EmailOutbox getEmailOutboxById(Long outBoxId) {
        Optional<EmailOutbox> emailOutboxOptional = emailOutBoxRepository.findByOutBoxId(outBoxId);
        return emailOutboxOptional.orElseThrow(() -> new RuntimeException("OutBox Not Found"));
    }
    
}
