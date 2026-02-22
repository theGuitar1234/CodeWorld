package az.codeworld.springboot.security.services.authservices;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface PasswordResetTokenService {
    void createPasswordResetToken(String email);
    boolean isTokenValid(String token);
    boolean tokenResetPassword(String token, String password, HttpServletRequest request, HttpServletResponse response);
}
