package az.codeworld.springboot.security.listeners;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import az.codeworld.springboot.security.records.EmailRequestedEvent;
import az.codeworld.springboot.security.services.emailservices.EmailService;
import jakarta.mail.MessagingException;

@Component
public class EmailListener {

    private final EmailService emailService;

    public EmailListener(
        EmailService emailService
    ) {
        this.emailService = emailService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onAfterCommit(EmailRequestedEvent emailRequestedEvent) throws MessagingException {
        emailService.sendEmail(emailRequestedEvent.outBoxId());
    }
}
