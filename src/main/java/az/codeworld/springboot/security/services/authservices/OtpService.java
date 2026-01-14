package az.codeworld.springboot.security.services.authservices;

public interface OtpService {

    void createOtpCode(String email);
    void sendOtpCode(String email, String otpCode);

    boolean isOtpValid(String email, String otpCode);
}