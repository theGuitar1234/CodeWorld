package az.codeworld.springboot.security.services.emailservices;

import jakarta.mail.MessagingException;

public interface EmailService {
    void sendEmail(Long outBoxId) throws MessagingException;
} 
