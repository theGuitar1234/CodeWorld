package az.codeworld.springboot.admin.services;

import java.security.Principal;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface LogoutService {
    void exterminate(Long userId, HttpServletRequest request, HttpServletResponse response);
}
