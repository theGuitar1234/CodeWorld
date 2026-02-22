package az.codeworld.springboot.admin.controllers;

import java.io.IOException;

import java.security.Principal;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import az.codeworld.springboot.admin.repositories.TeacherRepository;
import az.codeworld.springboot.admin.services.RequestService;
import az.codeworld.springboot.admin.services.TransactionService;
import az.codeworld.springboot.admin.services.UserService;
import az.codeworld.springboot.web.services.ProfileService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.ResponseStatus;

@RestController
@Profile("dev")
@RequestMapping("/api")
public class ApiController {

    private final RequestService requestService;

    private final UserService userService;
    
    private final ProfileService profileService;

    private final TransactionService transactionService;

    private final TeacherRepository teacherRepository;

    public ApiController(
        RequestService requestService,
        TransactionService transactionService,
        TeacherRepository teacherRepository,
        UserService userService,
        ProfileService profileService
    ) {
        this.requestService = requestService;
        this.transactionService = transactionService;
        this.teacherRepository = teacherRepository;
        this.userService = userService;
        this.profileService = profileService;
    }

    @GetMapping(value = {"", "/"}, consumes = "Application/JSON")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponse(responseCode = "400", description = "Swagger not available. Please consider to use the web-mvc-ui dependency for Swagger.")
    @ApiResponse(responseCode = "200", description = "Swagger loaded and available.")
    @Operation(summary = "Handle endpoints, test the application.")
    @PreAuthorize("hasRole('ADMIN')")
    public void swagger(HttpServletResponse response) throws IOException {
        response.sendRedirect("swagger-ui/index.html");
    }

    @GetMapping(value = "/getPrincipal", consumes = "Application/JSON")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponse(responseCode = "400", description = "No Principal available, consider setting a security context.")
    @ApiResponse(responseCode = "200", description = "Security Context is set and principal is retrieved.")
    @Operation(summary = "View the current authenticated principal in the security context")
    @PreAuthorize("hasRole('ADMIN')")
    public Principal principal(Principal principal) {
        return principal;
    }
    
}
