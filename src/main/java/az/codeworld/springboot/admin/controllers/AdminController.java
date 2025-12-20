package az.codeworld.springboot.admin.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import az.codeworld.springboot.admin.dtos.RequestDTO;
import az.codeworld.springboot.admin.dtos.transactions.TransactionDTO;
import az.codeworld.springboot.admin.services.RequestService;
import az.codeworld.springboot.admin.services.TransactionService;
import az.codeworld.springboot.security.services.authservices.RegistrationService;
import az.codeworld.springboot.security.services.emailservices.EmailService;
import az.codeworld.springboot.utilities.constants.emailstatus;
import az.codeworld.springboot.utilities.constants.roles;
import jakarta.servlet.Registration;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final RequestService requestService;
    private final TransactionService transactionService;
    private final EmailService emailService;
    private final RegistrationService registrationService;

    public AdminController(
        RequestService requestService,
        TransactionService transactionService,
        EmailService emailService,
        RegistrationService registrationService
    ) {
        this.requestService = requestService;
        this.transactionService = transactionService;
        this.emailService = emailService;
        this.registrationService = registrationService;
    }

    @GetMapping({"", "/"})
    @PreAuthorize("hasAuthority('ACCESS_ADMIN_PANEL')")
    public String admin(Model model) {
        try {
            model.addAllAttributes(Map.of(
                "requests", requestService.getRecentRequests(),
                "studentTransactions", transactionService.getAllTransactions(roles.STUDENT),
                "teacherTransactions", transactionService.getAllTransactions(roles.TEACHER)
            ));
        } catch (RuntimeException e) {
            model.addAllAttributes(Map.of(
                "requests", new ArrayList<RequestDTO>(),
                "studentTransactions", new ArrayList<TransactionDTO>(),
                "teacherTransactions", new ArrayList<TransactionDTO>()
            ));
        }
        return "admin/admin.html";
    }

    @ResponseBody
    @DeleteMapping("/rejectRequest/{requestId}")
    @PreAuthorize("hasAuthority('ACCESS_ADMIN_PANEL')")
    public void rejectRequest(@PathVariable("requestId") Long requestId) {
        RequestDTO requestDTO = requestService.getRequestById(requestId);
        requestService.deleteRequestByRequestId(requestId);
        registrationService.sendRejectionEmail(requestDTO);
    }

    @ResponseBody
    @DeleteMapping("/acceptRequest/{requestId}")
    @PreAuthorize("hasAuthority('ACCESS_ADMIN_PANEL')")
    public void acceptRequest(@PathVariable("requestId") Long requestId) {
        RequestDTO requestDTO = requestService.getRequestById(requestId);
        //requestService.deleteRequestByRequestId(requestId);
        registrationService.sendAcceptanceEmail(requestDTO);
    }
    
    
}
