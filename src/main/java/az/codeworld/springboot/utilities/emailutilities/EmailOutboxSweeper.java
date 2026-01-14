package az.codeworld.springboot.utilities.emailutilities;

import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import az.codeworld.springboot.security.repositories.EmailOutBoxRepository;
import az.codeworld.springboot.security.services.emailservices.EmailOutboxService;
import az.codeworld.springboot.security.services.emailservices.EmailService;
import az.codeworld.springboot.utilities.constants.emailstatus;
import jakarta.mail.MessagingException;

@Component
@Profile("prod")
public class EmailOutboxSweeper {

    private final EmailOutBoxRepository emailOutBoxRepository;

    private final EmailService emailService;

    public EmailOutboxSweeper(
        EmailOutBoxRepository emailOutBoxRepository,
        EmailService emailService
    ) {
        this.emailOutBoxRepository = emailOutBoxRepository;
        this.emailService = emailService;
    }

    @Scheduled(fixedDelay = 30000)
    public void sweep() {
        emailOutBoxRepository.findTop50ByStatusOrderByCreatedAtAsc(emailstatus.PENDING)
            .forEach(e -> {
                try {
                    emailService.sendEmail(e.getOutBoxId());
                } catch (MessagingException e1) {
                    e1.printStackTrace();
                }
            });
    }
}
