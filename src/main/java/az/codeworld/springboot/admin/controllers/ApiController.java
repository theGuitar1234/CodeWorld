package az.codeworld.springboot.admin.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.catalina.connector.Response;
import org.jspecify.annotations.Nullable;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import az.codeworld.springboot.admin.dtos.RequestDTO;
import az.codeworld.springboot.admin.dtos.TeacherDTO;
import az.codeworld.springboot.admin.dtos.UserDTO;
import az.codeworld.springboot.admin.dtos.transactions.TransactionDTO;
import az.codeworld.springboot.admin.entities.Teacher;
import az.codeworld.springboot.admin.repositories.TeacherRepository;
import az.codeworld.springboot.admin.services.RequestService;
import az.codeworld.springboot.admin.services.TransactionService;
import az.codeworld.springboot.admin.services.UserService;
import az.codeworld.springboot.utilities.constants.contenttypes;
import az.codeworld.springboot.utilities.constants.dtotype;
import az.codeworld.springboot.utilities.constants.profileError;
import az.codeworld.springboot.utilities.constants.profileSuccess;
import az.codeworld.springboot.utilities.constants.roles;
import az.codeworld.springboot.web.dtos.ProfilePayloadDTO;
import az.codeworld.springboot.web.dtos.ProfileViewDTO;
import az.codeworld.springboot.web.entities.ProfilePicture;
import az.codeworld.springboot.web.entities.TeachingAssignment;
import az.codeworld.springboot.web.services.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
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
