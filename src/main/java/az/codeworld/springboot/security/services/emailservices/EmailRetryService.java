package az.codeworld.springboot.security.services.emailservices;

public interface EmailRetryService {
    
    void sendWithRetry(Long outBoxId);
    void recover(Exception e, Long outBoxId);
} 
