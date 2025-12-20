package az.codeworld.springboot.web.controllers;

import java.security.Principal;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import az.codeworld.springboot.admin.dtos.UserDTO;
import az.codeworld.springboot.admin.dtos.transactions.UserTransactionDTO;
import az.codeworld.springboot.admin.services.TransactionService;
import az.codeworld.springboot.admin.services.UserService;
import az.codeworld.springboot.utilities.constants.roles;

import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class IndexController {

    private UserService userService;

    private TransactionService transactionService;

    public IndexController(
        UserService userService,
        TransactionService transactionService
    ) {
        this.userService = userService;
        this.transactionService = transactionService;
    }

    @GetMapping("/")
    public String home() {
        return "index";
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
