package az.codeworld.springboot.security.auth.handlers;

import java.io.IOException;
// import java.time.Instant;

// import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties.Jwt;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import az.codeworld.springboot.admin.services.UserService;
import az.codeworld.springboot.security.services.auditservices.AuditService;
import az.codeworld.springboot.utilities.constants.accountstatus;
import az.codeworld.springboot.utilities.constants.roles;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final AuditService auditService;

    private final UserService userService;

    public LoginSuccessHandler(
        AuditService auditService,
        UserService userService
    ) {
        setDefaultTargetUrl("/?continue");
        setAlwaysUseDefaultTargetUrl(false);

        this.auditService = auditService;
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        auditService.recordLogin(authentication.getName(), request.getRemoteAddr());
        userService.updateLastActiveAtByUserName(authentication.getName());

        // if (authentication.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals(roles.GUEST.getRoleString()))) {
        //     response.sendRedirect("/");
        //     return;
        // }

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
