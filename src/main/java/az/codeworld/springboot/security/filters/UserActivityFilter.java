package az.codeworld.springboot.security.filters;

import java.io.IOException;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import az.codeworld.springboot.admin.services.UserService;
import az.codeworld.springboot.utilities.constants.accountstatus;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
//No @Component, it registeres the class as a @Bean. Remove this if you want explicit Registration via @Configuration
@Component
public class UserActivityFilter extends OncePerRequestFilter {

    private final UserService userService;

    public UserActivityFilter(UserService userService) {
        this.userService = userService;
    }

    private boolean isPublic(HttpServletRequest req) {
        String uri = req.getRequestURI();
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            return true;
        }
        return uri.startsWith("/css")
                || uri.startsWith("/scss")
                || uri.startsWith("/js")
                || uri.startsWith("/lib")
                || uri.startsWith("/img")
                || uri.startsWith("/fonts")
                || uri.startsWith("/uploads")
                || uri.startsWith("/.well-known")
                || uri.startsWith("/favicon.ico");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (isPublic(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            userService.updateLastActiveAtByUserName(authentication.getName());   
        }

        filterChain.doFilter(request, response);
    }

}
