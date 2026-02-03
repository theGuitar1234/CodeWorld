package az.codeworld.springboot.security.services.authservices;

public interface OtpService {

    void createOtpCode(String email);
    boolean isOtpValid(String email, String otpCode);
}