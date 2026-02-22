package az.codeworld.springboot.security.controllers;

import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import az.codeworld.springboot.admin.services.RequestService;
import az.codeworld.springboot.admin.services.UserService;
import az.codeworld.springboot.security.auth.JpaUserDetails;
import az.codeworld.springboot.security.services.authservices.OtpService;
import az.codeworld.springboot.security.services.authservices.PasswordResetTokenService;
import az.codeworld.springboot.utilities.constants.roles;
import az.codeworld.springboot.admin.records.RequestRecord;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/restricted")
public class SecurityController {

    private final RequestService requestService;
    private final PasswordResetTokenService passwordResetTokenService;
    private final OtpService otpService;

    private final UserService userService;

    private final SessionAuthenticationStrategy sessionAuthenticationStrategy;
    private final SecurityContextRepository securityContextRepository;

    public SecurityController(
        RequestService requestService,
        PasswordResetTokenService passwordResetTokenService,
        OtpService otpService,
        SessionAuthenticationStrategy sessionAuthenticationStrategy,
        SecurityContextRepository securityContextRepository,
        UserService userService
    ) {
        this.requestService = requestService;
        this.passwordResetTokenService = passwordResetTokenService;
        this.otpService = otpService;
        this.sessionAuthenticationStrategy = sessionAuthenticationStrategy; 
        this.securityContextRepository = securityContextRepository; 
        this.userService = userService;
    }

    @GetMapping("/")
    public String restricted(
        HttpServletRequest request,
        Model model
    ) {
        model.addAttribute("error", request.getParameter("error"));
        return "auth/restricted";
    }

    @GetMapping("/authenticate")
    public String authenciate() {
        return "auth/restricted";
    }

    @PostMapping("/request")
    public String request(
        @ModelAttribute RequestRecord requestRecord
    ) {
        try {
            requestService.createNewRequest(requestRecord);
            return "redirect:/restricted/?success=Request is sent!";
        } catch (DataIntegrityViolationException e) {
            return "redirect:/restricted/?error=" + requestRecord.email() + " has already sent a request!";
        } catch (NullPointerException e) {
            return "redirect:/restricted/?error=Please make sure you filled all the fields";
        }
    }

    @GetMapping("/forgotPassword")
    public String forgotPassword(HttpServletRequest request, Model model) {
        model.addAttribute("error", request.getParameter("error"));
        return "auth/reset-password/forgot_password";
    }

    @ResponseBody
    @PostMapping("/forgotPassword")
    public void forgotPassword(@RequestParam("email") String email, Model model) {
        try {
            passwordResetTokenService.createPasswordResetToken(email);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }
    
    @GetMapping("/tokenResetPassword")
    public String tokenResetPassword(@RequestParam("token") String token, Model model) {
        if (passwordResetTokenService.isTokenValid(token)) {
            model.addAttribute("token", token);
            model.addAttribute("success", "Password Reset Token Valid");
            return "auth/reset-password/reset_password";
        } else {
            return "redirect:/restricted/forgotPassword?error=Invalid%20Reset%20Token";
        }
    }

    @PostMapping("/tokenResetPassword")
    public String tokenResetPassword(
        @RequestParam("token") String token,
        @RequestParam("password") String password,
        HttpServletRequest request,
        HttpServletResponse response
    ) {
        if (passwordResetTokenService.tokenResetPassword(token, password, request, response)) {
            return "redirect:/restricted/?success=The%20Password%20is%20Updated!";
        } else { 
            return "redirect:/restricted/?error=Someting%20went%20wrong%Couldn't%20Update%20Password";
        }
    }

    @GetMapping("/2fa")
    public String get2fa(
        HttpServletRequest request, 
        Model model
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isPre2fa = auth != null && auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_" + roles.PRE_2FA.getRoleNameString()));

        if (!isPre2fa) return "redirect:/restricted/?expired";

        JpaUserDetails user = (JpaUserDetails) auth.getPrincipal();
        String email = userService.getUserRecordByUserName(user.getUsername()).email();

        otpService.createOtpCode(email);

        model.addAllAttributes(
            Map.of(
                //"error", request.getParameter("error"),
                "email", email,
                "otpFormAction", "/restricted/2fa",
                "otpRefreshAction", "/restricted/refreshOtpCode",
                "error", request.getParameter("error")
            )
        );
        return "auth/otp-code/otp_code";
    }

    @PostMapping("/2fa")
    public String post2fa(
        @RequestParam("otpCode") String otpCode,
        @RequestParam("email") String email,
        HttpServletRequest request,
        HttpServletResponse response,
        Model model
    ) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated())
            return "redirect:/restricted/?expired";

        boolean isPre2fa = authentication.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_" + roles.PRE_2FA.getRoleNameString()));

        if (!isPre2fa) return "redirect:/";

        JpaUserDetails jpaUserDetails = (JpaUserDetails) authentication.getPrincipal();

        if (!otpService.isOtpValid(email, otpCode)) {
            model.addAllAttributes(Map.of(
                "email", email,
                "otpFormAction", "/restricted/2fa",
                "otpRefreshAction", "/restricted/refreshOtpCode",
                "error", "Invalid or expired code"
            ));
            return "auth/otp-code/otp_code";
        }

        UsernamePasswordAuthenticationToken privilegedAuthentication = 
            new UsernamePasswordAuthenticationToken(
                jpaUserDetails,
                authentication.getCredentials(),
                jpaUserDetails.getAuthorities()
            );
        
        SecurityContextHolderStrategy strategy = SecurityContextHolder.getContextHolderStrategy();
        
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(privilegedAuthentication);
        strategy.setContext(securityContext);

        // HttpSession session = request.getSession(false);
        // session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);

        sessionAuthenticationStrategy.onAuthentication(privilegedAuthentication, request, response);
        securityContextRepository.saveContext(securityContext, request, response);
        
        return "redirect:/";
    }
    
    @PostMapping("/refreshOtpCode/{email}")
    public String refreshOtpCode(
        @PathVariable("email") String email,
        Model model
    ) {
        otpService.createOtpCode(email);
        model.addAllAttributes(
            Map.of(
                "email", email,
                "otpFormAction", "/restricted/2fa",
                "otpRefreshAction", "/account/refreshOtpCode"
            )
        );

        // return "redirect:/restricted/2fa";
        return "auth/otp-code/otp_code";
    }
    
}
