package az.codeworld.springboot.admin.services.serviceImpl;

import java.security.Principal;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import az.codeworld.springboot.admin.projections.UserLogoutProjection;
import az.codeworld.springboot.admin.services.LogoutService;
import az.codeworld.springboot.admin.services.UserService;
import az.codeworld.springboot.security.services.sessionservices.SessionKiller;
import az.codeworld.springboot.utilities.constants.accountstatus;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Service
public class LogoutServiceImpl implements LogoutService {

    private final UserService userService;
    private final SessionKiller sessionKiller;

    public LogoutServiceImpl(
        UserService userService,
        SessionKiller sessionKiller
    ) {
        this.userService = userService;
        this.sessionKiller = sessionKiller;
    }

    @Override
    public void exterminate(Long userId, HttpServletRequest request, HttpServletResponse response) {
        
        SecurityContextHolder.clearContext();

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        UserLogoutProjection userLogoutProjection = userService.getUserProjectionById(userId, UserLogoutProjection.class);
        sessionKiller.invalidateUserSessions(userLogoutProjection.getUserName());

        expireCookie(request, response, "JSESSIONID");
        expireCookie(request, response, "SESSION");      
        expireCookie(request, response, "remember-me");  

        //userService.deleteUserByUsername(username);
    }

    private void expireCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, "");
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(request.isSecure());
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

}
