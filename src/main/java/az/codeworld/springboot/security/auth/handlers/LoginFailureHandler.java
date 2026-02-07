package az.codeworld.springboot.security.auth.handlers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import az.codeworld.springboot.admin.services.ActivityService;
import az.codeworld.springboot.admin.services.serviceImpl.ActivityServiceImpl;
import az.codeworld.springboot.security.services.auditservices.AuditService;
import az.codeworld.springboot.utilities.constants.accounterror;
import az.codeworld.springboot.utilities.constants.eventtype;
import az.codeworld.springboot.utilities.constants.exceptionmessages;
import az.codeworld.springboot.utilities.constants.source;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final ActivityService activityService;
    private final AuditService auditService;

    public LoginFailureHandler(
        AuditService auditService,
        ActivityService activityService
    ) {
        //setDefaultFailureUrl("/restricted/?error=Login%20Failure");

        this.auditService = auditService;
        this.activityService = activityService;
    }

    @Override
    public void onAuthenticationFailure(
        HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException authException
    ) throws IOException, ServletException {

        HttpServletRequest modifiedRequest = request;

        if (authException instanceof DisabledException) {
            setDefaultFailureUrl("/restricted/?error=" + accounterror.ACCOUNT_BANNED.getAccountErrorString());
        }

        if (authException instanceof LockedException) {
            setDefaultFailureUrl("/restricted/?error=" + exceptionmessages.USER_BLOCKED.getExceptionMessageString());
        }

        if (authException instanceof BadCredentialsException) {
            setDefaultFailureUrl("/restricted/?error=" + authException.getLocalizedMessage());
        }

        String userName = (String) request.getParameter("username");
       
        if (userName != null) auditService.recordFailure(userName);

        activityService.recordActivity(
            eventtype.LOGIN_FAILURE, 
            "Login unsuccessful", 
            "Failed Login Attempt for: " + userName, 
            source.SYSTEM.getSourceString(), 
            userName
        );

        super.onAuthenticationFailure(modifiedRequest, response, authException);
    }
    
}
