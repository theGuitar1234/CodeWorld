package az.codeworld.springboot.security.services.emailservices;

import az.codeworld.springboot.security.entities.EmailOutbox;

public interface EmailOutboxService {

    void defaultMethod();

    void saveEmailOutbox(EmailOutbox emailOutbox);

    EmailOutbox getEmailOutboxById(Long outBoxId);
}
