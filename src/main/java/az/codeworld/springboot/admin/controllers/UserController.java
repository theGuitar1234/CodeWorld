package az.codeworld.springboot.admin.controllers;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import az.codeworld.springboot.admin.dtos.RequestDTO;
import az.codeworld.springboot.admin.dtos.transactions.TransactionDTO;
import az.codeworld.springboot.admin.records.LinkRecord;
import az.codeworld.springboot.admin.services.LogoutService;
import az.codeworld.springboot.admin.services.RequestService;
import az.codeworld.springboot.admin.services.TransactionService;
import az.codeworld.springboot.admin.services.UserService;
import az.codeworld.springboot.exceptions.InvalidRequestTokenException;
import az.codeworld.springboot.security.dtos.UserAuthDTO;
import az.codeworld.springboot.security.services.authservices.RegistrationService;
import az.codeworld.springboot.utilities.constants.roles;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
@RequestMapping("/user")
public class UserController {

    private Logger log = LoggerFactory.getLogger(UserController.class);

    private final RegistrationService registrationService;

    private final TransactionService transactionService;
    private final RequestService requestService;
    private final LogoutService logoutService;

    public UserController(
        TransactionService transactionService,
        RequestService requestService,
        LogoutService logoutService
    , RegistrationService registrationService) {
        this.transactionService = transactionService;
        this.requestService = requestService;
        this.logoutService = logoutService;
        this.registrationService = registrationService;
    }

    @GetMapping("/login")
    public String login() {
        return "auth/restricted";
    }

    @GetMapping("/logout")
    public String logout(Principal principal, HttpServletRequest request, HttpServletResponse response) {
        logoutService.exterminate(principal, request, response);
        return "redirect:/restricted/";
    }
    

    @GetMapping("/register")
    public String register(
        @RequestParam(
            required = true,
            name = "token"
        ) String token,
        HttpServletResponse response,
        Model model
    ) throws IOException {
        try {
            RequestDTO requestDTO = requestService.validateRequest(token);
            model.addAllAttributes(Map.of("request", requestDTO));
            return "auth/registration/register";
        } catch (InvalidRequestTokenException e) {
            e.printStackTrace();
            log.error(e.getLocalizedMessage());
            return null;
        }
    }

    @PostMapping("/register")
    public String register(
        @RequestParam(
            required = true,
            name = "token"
        ) String token,
        @RequestParam(
            required = true,
            name = "password"
        ) String password,
        HttpServletResponse response
    ) throws IOException {
        try {
            RequestDTO requestDTO = requestService.validateRequest(token);
            registrationService.registerUser(UserAuthDTO
                .builder()
                .firstName(requestDTO.getFirstName())
                .lastName(requestDTO.getLastName())
                .email(requestDTO.getEmail())
                .role(requestDTO.getRole())
                .password(password)
                .build()
            );
            requestService.deleteRequestByRequestId(requestDTO.getRequestId());
        } catch (InvalidRequestTokenException e) {
            e.printStackTrace();
            log.error(e.getLocalizedMessage());
            return null;
        }
        return "redirect:/restricted/";
    }
    

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAllAttributes(
            Map.of(
                "transactions", transactionService.getRecentTransactions(roles.TEACHER),
                "transactionsOnPage", Page.empty(),
                "pages", Page.empty(),
                "mode", "PREVIEW"
            )
        );
        return "dashboard/dashboard.html";
    }

    @GetMapping("/transactions")
    public String transactions(
        @RequestParam(
            required = false,
            name = "sortBy",
            defaultValue = "transactionAmount"
        ) String sortBy,
        @RequestParam(
            required = false,
            name = "perPage",
            defaultValue = "8"
        ) int perPage,
        @RequestParam(
            required = false,
            name = "pageIndex",
            defaultValue = "1"
        ) int pageIndex,
        @RequestParam(
            required = false,
            name = "direction",
            defaultValue = "ASC"
        ) Direction direction,
        @RequestParam(
            required = false,
            name = "role",
            defaultValue = "TEACHER"
        ) roles role,
        @RequestParam(
            required = false,
            name = "mode",
            defaultValue = "PREVIEW"
        ) String mode,
        @RequestParam(
            required = false,
            name = "fragment",
            defaultValue = "false"
        ) boolean fragment,
        Model model
    ) {
        Page<TransactionDTO> transactionsOnPage = transactionService   
            .getPaginatedTransactions(
                role,
                pageIndex - 1,
                perPage,
                sortBy,
                direction
        );

        LinkRecord linkRecord;
        List<LinkRecord> pages = new ArrayList<>();
        for (int i = 0; i<transactionsOnPage.getTotalPages(); i++) {
            String isActive = "";
            if (i == transactionsOnPage.getNumber()) {
                isActive = "current";
            }
            linkRecord = new LinkRecord(isActive, perPage, i+1, direction, role);
            pages.add(linkRecord);
        }

        model.addAllAttributes(
            Map.of(
                "transactions", transactionsOnPage.getContent(),
                "transactionsOnPage", transactionsOnPage,
                "pages", pages,
                "mode", "VIEW_ALL"
            )
        );  

        if(fragment) return "fragments/transaction-pagination.html :: transaction-pagination";
        
        return "dashboard/dashboard.html";
    }

}
