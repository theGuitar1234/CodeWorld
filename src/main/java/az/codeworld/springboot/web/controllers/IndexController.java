package az.codeworld.springboot.web.controllers;

import java.security.Principal;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import az.codeworld.springboot.admin.dtos.UserDTO;
import az.codeworld.springboot.admin.dtos.transactions.UserTransactionDTO;
import az.codeworld.springboot.admin.services.UserService;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class IndexController {

    private UserService userService;

    public IndexController(
        UserService userService
    ) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        try {
            model.addAttribute("user", userService.getUserByUsername(principal.getName()));
        } catch (RuntimeException e) {
            e.printStackTrace();
            model.addAllAttributes(Map.of(
                "user", new UserTransactionDTO(), 
                "error", e.getLocalizedMessage())
            );
        }
        return "dashboard/dashboard.html";
    }
    
    @GetMapping("/transactions")
    public String transactions() {
        return "buss/transactions";
    }

    @GetMapping("/paymentMethods")
    public String paymentMethods() {
        return "buss/payments";
    }

    @GetMapping("/404")
    public String notFound() {
        return "error/404";
    }
    
}
