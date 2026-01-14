package az.codeworld.springboot.admin.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import az.codeworld.springboot.admin.dtos.RequestDTO;
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
public class IndexController {

    private final RequestService requestService;

    private final UserService userService;
    
    private final ProfileService profileService;

    private final TransactionService transactionService;

    private final TeacherRepository teacherRepository;

    public IndexController(
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

    @GetMapping("/getTeacher")
    public Teacher getTeacher(@RequestParam String userName) {
        return teacherRepository.findByUserName(userName).get();
    }

    @PostMapping(value = "/addProfilePicture", consumes = "Application/JSON")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponse(responseCode = "201", description = "Profile successfully created")
    @ApiResponse(responseCode = "400", description = "Something went wrong.")
    @Operation(summary = "A Profile should be added to the database")
    @SecurityRequirement(name = "Index API")
    public ResponseEntity<?> addProfilePicture(
        @Valid @RequestBody ProfilePayloadDTO profilePayloadDTO,
        @RequestParam Long profileId,
        @RequestPart(required = true) MultipartFile multipartFile
    ) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            UserDTO userDTO = (UserDTO) userService.getUserByUserName(username, dtotype.FULL);
            
            ProfilePicture profilePicture = new ProfilePicture();
            profilePicture.setProfileTitle(profilePayloadDTO.getProfileTitle());
            profilePicture.setDescription(profilePayloadDTO.getDescription());

            profileService.addProfilePictureToUser(profilePicture);

            if (multipartFile == null) return ResponseEntity.badRequest().body("No File selected");

            Path uploadDirectory = Paths.get(System.getProperty("user.dir"), "photos");

            if (!Files.exists(uploadDirectory)) Files.createDirectory(uploadDirectory);

            ProfilePicture profilePicture2 = profileService.getProfileByProfileId(profileId);

            Path saveFile;

            switch (multipartFile.getContentType()) {
                case "image/jpeg":
                    saveFile = uploadDirectory.resolve(UUID.randomUUID().toString() + contenttypes.JPEG.getContentTypeString());
                    break;
                case "image/png":
                    saveFile = uploadDirectory.resolve(UUID.randomUUID().toString() + contenttypes.PNG.getContentTypeString());
                default:
                    return ResponseEntity.badRequest().body("Unsupported File Format");
            }

            return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(profilePayloadDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(profileError.PROFILE_ERROR.getProfileErrorString() + ' ' + e);
        }
    }
    
}
