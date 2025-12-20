package az.codeworld.springboot.admin.services;

import java.security.Principal;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public interface LogoutService {
    void exterminate(Principal principal, HttpServletRequest request, HttpServletResponse response);
}
