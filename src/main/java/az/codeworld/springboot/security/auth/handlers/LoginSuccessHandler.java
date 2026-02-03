package az.codeworld.springboot.security.auth.handlers;

import java.io.IOException;
// import java.time.Instant;

import org.springframework.security.authentication.RememberMeAuthenticationToken;
// import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties.Jwt;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import az.codeworld.springboot.admin.services.ActivityService;
import az.codeworld.springboot.admin.services.UserService;
import az.codeworld.springboot.security.services.auditservices.AuditService;
import az.codeworld.springboot.utilities.constants.accountstatus;
import az.codeworld.springboot.utilities.constants.eventtype;
import az.codeworld.springboot.utilities.constants.roles;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
//public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final AuditService auditService;
    private final UserService userService;
    private final ActivityService activityService;

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    //private final RequestCache requestCache = new HttpSessionRequestCache();

    public LoginSuccessHandler(
        AuditService auditService,
        UserService userService,
        ActivityService activityService
    ) {
        setDefaultTargetUrl("/?continue");
        setAlwaysUseDefaultTargetUrl(false);

        this.auditService = auditService;
        this.userService = userService;
        this.activityService = activityService;
    }

    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication
    ) throws IOException, ServletException {

        boolean isPre2fa = authentication.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_" + roles.PRE_2FA.getRoleNameString()));

        if (isPre2fa) {
            redirectStrategy.sendRedirect(request, response, "/restricted/2fa?userName=" + authentication.getName());
            return;
        }

        auditService.recordLogin(authentication.getName(), request.getRemoteAddr());
        userService.updateLastActiveAtByUserName(authentication.getName());

        if (!(authentication instanceof RememberMeAuthenticationToken)) { 
            activityService.recordActivity(
                eventtype.LOGIN_SUCCESS,
                "Login successful",
                authentication.getName() + " logged in",
                authentication.getName(),
                authentication.getName()       
            );
        }

        // SavedRequest savedRequest = requestCache.getRequest(request, response);
        // if (savedRequest != null) { 
        //     redirectStrategy.sendRedirect(request, response, savedRequest.getRedirectUrl());
        // } else {
        //     redirectStrategy.sendRedirect(request, response, getDefaultTargetUrl());
        // }
        
        if (request.getRequestURI().startsWith("/restricted")) {
            redirectStrategy.sendRedirect(request, response, getDefaultTargetUrl());
            return;
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
