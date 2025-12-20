package az.codeworld.springboot.admin.controllers;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.catalina.connector.Response;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import az.codeworld.springboot.admin.dtos.RequestDTO;
import az.codeworld.springboot.admin.dtos.transactions.TransactionDTO;
import az.codeworld.springboot.admin.services.RequestService;
import az.codeworld.springboot.admin.services.TransactionService;
import az.codeworld.springboot.utilities.constants.roles;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/index")
public class OpenAPIController {

    private final RequestService requestService;

    private final TransactionService transactionService;

    public OpenAPIController(
        RequestService requestService,
        TransactionService transactionService
    ) {
        this.requestService = requestService;
        this.transactionService = transactionService;
    }

    @GetMapping({"", "/"})
    public void swagger(HttpServletResponse response) throws IOException {
        response.sendRedirect("swagger-ui/index.html");
    }

    @GetMapping("/getPrincipal")
    public Principal principal(Principal principal) {
        return principal;
    }
    

    @GetMapping("/getRecentRequests")
    public ResponseEntity<?> getRecentRequests() {
        return ResponseEntity.ok(requestService.getRecentRequests());
    }
    
    @GetMapping("/getTransactions")
    public List<TransactionDTO> getAllTransactions(Authentication authentication) {
        
        String role = authentication
            .getAuthorities()
            .stream()
            .filter(g -> g.getAuthority().startsWith("ROLE_"))
            .findFirst().get().getAuthority();
        
        roles rolez = null;
        
        switch (role) {
            case "ROLE_ADMIN":
                rolez = roles.ADMIN;
                break;
            case "ROLE_STUDENT":
                rolez = roles.STUDENT;
            case "ROLE_TEACHER":
                rolez = roles.TEACHER;
            default:
                break;
        }

        return transactionService.getAllTransactions(rolez);
    }
    @GetMapping("/getRecentTeacherTransactions")
    public List<TransactionDTO> getRecentTeacherTransactions() {
        return transactionService.getRecentTransactions(roles.ADMIN);
    }

    @GetMapping("/role")
    public @Nullable String role(Authentication authentication) {
        return authentication
            .getAuthorities()
            .stream()
            .filter(g -> g.getAuthority().startsWith("ROLE_"))
            .findFirst().get().getAuthority();
    }

    @GetMapping("/filterTransactions")
    public List<TransactionDTO> filterTransactions(
        @RequestParam(
            required = false,
            name = "sortBy",
            defaultValue = "transactionDate"
        ) String sortBy,
        @RequestParam(
            required = false,
            name = "perPage",
            defaultValue = "1"
        ) String perPage,
        @RequestParam(
            required = false,
            name = "page",
            defaultValue = "1"
        ) String page,
        @RequestParam(
            required = false,
            name = "role",
            defaultValue = "ADMIN"
        ) roles role
    ) {
        Page<TransactionDTO> transactionsOnPage = transactionService   
            .getPaginatedTransactions(
                role,
                Integer.parseInt(page) - 1,
                Integer.parseInt(perPage),
                sortBy,
                Direction.ASC
            );
        
        return transactionsOnPage.getContent();
    }

    @GetMapping("/fragment")
    public String fragment() {
        return "fragments/transactions-filter.html :: transactions-filter";
    }
    
    
}
