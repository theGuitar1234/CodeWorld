package az.codeworld.springboot.security.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/restricted")
public class SecurityController {

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
    
}
