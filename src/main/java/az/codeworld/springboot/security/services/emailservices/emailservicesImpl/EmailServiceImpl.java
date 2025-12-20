package az.codeworld.springboot.security.services.emailservices.emailservicesImpl;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import az.codeworld.springboot.security.services.emailservices.EmailService;
import jakarta.mail.MessagingException;

@Service
public class EmailServiceImpl implements EmailService {

    private final EmailRetryServiceImpl emailRetryServiceImpl;

    public EmailServiceImpl(
        EmailRetryServiceImpl emailOutboxService
    ) {
        this.emailRetryServiceImpl = emailOutboxService;
    }

    @Async("mailExecutor")
    public void sendEmail(Long outBoxId) throws MessagingException {
        System.out.println("\n\n\n\n\n\nOH MY GOD\n\n\n\n\n\n");
        emailRetryServiceImpl.sendWithRetry(outBoxId); 
    }
}
