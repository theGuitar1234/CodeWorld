package az.codeworld.springboot.security.controllers;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import az.codeworld.springboot.admin.services.RequestService;
import az.codeworld.springboot.security.services.authservices.OtpService;
import az.codeworld.springboot.security.services.authservices.PasswordResetTokenService;
import az.codeworld.springboot.admin.records.RequestRecord;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequestMapping("/restricted")
public class SecurityController {

    private final RequestService requestService;
    private final PasswordResetTokenService passwordResetTokenService;
    private final OtpService otpService;

    public SecurityController(
        RequestService requestService,
        PasswordResetTokenService passwordResetTokenService,
        OtpService otpService
    ) {
        this.requestService = requestService;
        this.passwordResetTokenService = passwordResetTokenService;
        this.otpService = otpService;
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
            return "redirect:/restricted/?msg=Request is sent!";
        } catch (DataIntegrityViolationException e) {
            return "redirect:/restricted/?error=" + requestRecord.email() + " has already sent a request!";
        } catch (NullPointerException e) {
            return "redirect:/restricted/?error=Please make sure you filled all the fields";
        }
    }

    @GetMapping("/forgotPassword")
    public String forgotPassword(HttpServletRequest request, Model model) {
        System.out.println("\n\n\n\n\n\n\n\n" + "YO I MADE IT TO THE GET MAPPING!" + "\n\n\n\n\n\n\n\n");
        model.addAttribute("error", request.getParameter("error"));
        return "auth/reset_password/forgot-password";
    }

    @PostMapping("/forgotPassword")
    public void forgotPassword(@RequestParam("email") String email, Model model) {
        System.out.println("\n\n\n\n\n\n\n\n" + "YO I MADE IT TO THE POST MAPPING!" + "\n\n\n\n\n\n\n\n");
        try {
            passwordResetTokenService.createPasswordResetToken(email);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        System.out.println("\n\n\n\n\n\n\n\n" + "I AM AT THE END OF THE POST METHOD NOW, WHAT ELSE COULD HAPPEN?" + "\n\n\n\n\n\n\n\n");
    }
    
    @GetMapping("/tokenResetPassword")
    public String tokenResetPassword(@RequestParam("token") String token, Model model) {
        if (passwordResetTokenService.isTokenValid(token)) {
            model.addAttribute("token", token);
            return "auth/reset_password/reset-password";
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
            return "redirect:/restricted/?error=Someting%20went%20wrong";
        }
    }

    @GetMapping("/otpCode")
    public String otpCode(HttpServletRequest request, Model model) {
        model.addAttribute("error", request.getParameter("error"));
        return "auth/otp-code/otp_code_email";
    }

    @PostMapping("/verifyOtpCode")
    public String verifyOtpCode(
        @RequestParam("otpCode") String otpCode,
        @RequestParam("email") String email,
        HttpServletRequest request
    ) {
        if (!otpService.isOtpValid(email, otpCode)) return "redirect:/restricted/otpCode?error=Invalid+ot+expired+code";
        return "redirect:/";
    }

    @PostMapping("/refreshOtpCode/{email}")
    public String refreshOtpCode(
        HttpServletRequest request,
        @PathVariable("email") String email,
        Model model
    ) {
        otpService.createOtpCode(email);
        model.addAttribute("email", email);
        return "redirect:/restricted/otpCode";
    }
    
}
