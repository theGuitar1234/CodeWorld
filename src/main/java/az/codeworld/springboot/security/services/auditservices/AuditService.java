package az.codeworld.springboot.security.services.auditservices;

public interface AuditService {
    
    void recordLogin(String email, String ip);
    void recordFailure(String email);
    void unBlockAccount(String email);
}