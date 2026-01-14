package az.codeworld.springboot.security.auth.handlers;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import az.codeworld.springboot.security.services.auditservices.AuditService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final AuditService auditService;

    public LoginFailureHandler(AuditService auditService) {
        setDefaultFailureUrl("/?continue");

        this.auditService = auditService;
    }

    @Override
    public void onAuthenticationFailure(
        HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException authException
    ) throws IOException, ServletException {
    
        String userName = (String) request.getAttribute("userName");
       
        if (userName != null) auditService.recordFailure(userName);
        super.onAuthenticationFailure(request, response, authException);
    }
    
}
