package az.codeworld.springboot.security.services.emailservices.emailservicesImpl;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import az.codeworld.springboot.security.entities.EmailOutbox;
import az.codeworld.springboot.security.services.emailservices.EmailOutboxService;
import az.codeworld.springboot.utilities.constants.emailstatus;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;

@Service
public class EmailRetryServiceImpl {

    private Logger log = LoggerFactory.getLogger(EmailRetryServiceImpl.class);

    private final JavaMailSender javaMailSender;

    private final EmailOutboxService emailOutboxService;

    public EmailRetryServiceImpl(
        JavaMailSender javaMailSender,
        EmailOutboxService emailOutboxService
    ) {
        this.javaMailSender = javaMailSender;
        this.emailOutboxService = emailOutboxService;
    }

    @Retryable(
       retryFor = {MailException.class, MessagingException.class},
       maxAttempts = 2,
       backoff = @Backoff(
            delay = 2000,
            multiplier = 2.0,
            maxDelay = 60000,
            random = true
       ) 
    )
    @Transactional
    public void sendWithRetry(Long outBoxId) throws MessagingException {

        EmailOutbox emailOutbox = emailOutboxService.getEmailOutboxById(outBoxId);

        if (emailOutbox.getStatus() == emailstatus.SENT) return;

        emailOutbox.setStatus(emailstatus.SENDING);
        emailOutbox.setAttempts(emailOutbox.getAttempts() + 1);

        emailOutboxService.saveEmailOutbox(emailOutbox);

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(
            mimeMessage, 
            MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, 
            StandardCharsets.UTF_8.name()
        );

        mimeMessageHelper.setTo(emailOutbox.getRecipient());
        mimeMessageHelper.setSubject(emailOutbox.getSubject());
        mimeMessageHelper.setText(emailOutbox.getHtml(), true);
        //mimeMessageHelper.setFrom("BOOO! YOU THOUGHT I AM GONNA TELL YOU WHO AM I? F*CK OFF!");
        mimeMessageHelper.addInline("logo", new ClassPathResource("static/assets/sprites/logo.png"));

        System.out.println("\n\n\n\n\n\n\nI AM JUST ABOUT TO SEND IT!!!!\n\n\n\n\n");
        
        javaMailSender.send(mimeMessage);

        emailOutbox.setStatus(emailstatus.SENT);
        emailOutbox.setSentAt(Instant.now());
        emailOutbox.setLastError(null);

        emailOutboxService.saveEmailOutbox(emailOutbox);
    }

    @Recover
    @Transactional
    public void recover(Exception e, Long outBoxId) {
        EmailOutbox emailOutbox = emailOutboxService.getEmailOutboxById(outBoxId);

        emailOutbox.setStatus(emailstatus.FAILED);
        emailOutbox.setLastError(e.getClass().getSimpleName() + ": " + e.getMessage());

        emailOutboxService.saveEmailOutbox(emailOutbox);

        log.error(e.getLocalizedMessage());
        e.printStackTrace();
    }
}
