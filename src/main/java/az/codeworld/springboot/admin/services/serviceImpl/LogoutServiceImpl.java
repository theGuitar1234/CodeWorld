package az.codeworld.springboot.admin.services.serviceImpl;

import java.security.Principal;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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
    public void exterminate(String username, HttpServletRequest request, HttpServletResponse response) {
        
        SecurityContextHolder.clearContext();

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        sessionKiller.invalidateUserSessions(username);

        for (Cookie c : request.getCookies()) {
            if ("JSESSIONID".equals(c.getName())) {
                Cookie cookie = new Cookie("JSESSIONID", null);
                cookie.setHttpOnly(true);
                cookie.setSecure(request.isSecure());
                cookie.setPath("/");
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
        }

        //userService.deleteUserByUsername(username);
    }

}
