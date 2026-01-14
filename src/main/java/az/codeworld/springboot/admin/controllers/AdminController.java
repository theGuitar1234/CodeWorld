package az.codeworld.springboot.admin.controllers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import az.codeworld.springboot.admin.dtos.RequestDTO;
import az.codeworld.springboot.admin.dtos.UserDTO;
import az.codeworld.springboot.admin.dtos.transactions.TransactionDTO;
import az.codeworld.springboot.admin.entities.Request;
import az.codeworld.springboot.admin.entities.Student;
import az.codeworld.springboot.admin.entities.Teacher;
import az.codeworld.springboot.admin.records.LinkRecord;
import az.codeworld.springboot.admin.services.RequestService;
import az.codeworld.springboot.admin.services.TransactionService;
import az.codeworld.springboot.admin.services.UserService;
import az.codeworld.springboot.security.services.authservices.RegistrationService;
import az.codeworld.springboot.security.services.emailservices.EmailService;
import az.codeworld.springboot.utilities.constants.dtotype;
import az.codeworld.springboot.utilities.constants.emailstatus;
import az.codeworld.springboot.utilities.constants.roles;
import az.codeworld.springboot.utilities.constants.spa;
import az.codeworld.springboot.web.entities.ProfilePicture;
import az.codeworld.springboot.web.services.ProfileService;
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
    private final ProfileService profileService;
    private final UserService userService;

    public AdminController(
        RequestService requestService,
        TransactionService transactionService,
        EmailService emailService,
        RegistrationService registrationService,
        ProfileService profileService,
        UserService userService
    ) {
        this.requestService = requestService;
        this.transactionService = transactionService;
        this.emailService = emailService;
        this.registrationService = registrationService;
        this.profileService = profileService;
        this.userService = userService;
    }

    @GetMapping({ "", "/" })
    @PreAuthorize("hasAuthority('ACCESS_ADMIN_PANEL')")
    public String admin(Model model) {
        model.addAllAttributes(
                Map.of(
                        "requests", requestService.getRecentRequests(),
                        "studentTransactions", transactionService.getRecentTransactions(roles.STUDENT),
                        "teacherTransactions", transactionService.getRecentTransactions(roles.TEACHER)));

        model.addAllAttributes(
                Map.of(
                        "transactions", Page.empty(),
                        "transactionsOnPage", Page.empty(),
                        "mode", "PREVIEW",
                        "spa", spa.HOME.getSpaString(),
                        "pages", Page.empty()));

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
        registrationService.sendAcceptanceEmail(requestDTO);
    }

    @GetMapping("/teachers")
    public String teachers(
            @RequestParam(required = false, name = "sortBy", defaultValue = "transactionAmount") String sortBy,
            @RequestParam(required = false, name = "perPage", defaultValue = "8") int perPage,
            @RequestParam(required = false, name = "pageIndex", defaultValue = "1") int pageIndex,
            @RequestParam(required = false, name = "direction", defaultValue = "ASC") Direction direction,
            @RequestParam(required = false, name = "role", defaultValue = "TEACHER") roles role,
            @RequestParam(required = false, name = "mode", defaultValue = "PREVIEW") String mode,
            @RequestParam(required = false, name = "fragment", defaultValue = "false") boolean fragment,
            Model model) {
        Page<TransactionDTO> transactionsOnPage = transactionService
                .getPaginatedTransactions(
                        role,
                        pageIndex - 1,
                        perPage,
                        sortBy,
                        direction);

        LinkRecord linkRecord;
        List<LinkRecord> pages = new ArrayList<>();
        for (int i = 0; i < transactionsOnPage.getTotalPages(); i++) {
            String isActive = "";
            if (i == transactionsOnPage.getNumber()) {
                isActive = "current";
            }
            linkRecord = new LinkRecord(isActive, perPage, i + 1, direction, role);
            pages.add(linkRecord);
        }

        model.addAllAttributes(
                Map.of(
                        "transactions", transactionsOnPage.getContent(),
                        "transactionsOnPage", transactionsOnPage,
                        "mode", "VIEW_ALL",
                        "spa", spa.TEACHERS.getSpaString(),
                        "pages", pages));

        model.addAllAttributes(
                Map.of(
                        "teacherTransactions", new ArrayList<Teacher>()));

        if (fragment)
            return "admin/fragments/main/teachers-main.html :: teachers-main";

        return "admin/admin.html";
    }

    @GetMapping("/students")
    public String students(
            @RequestParam(required = false, name = "sortBy", defaultValue = "transactionAmount") String sortBy,
            @RequestParam(required = false, name = "perPage", defaultValue = "8") int perPage,
            @RequestParam(required = false, name = "pageIndex", defaultValue = "1") int pageIndex,
            @RequestParam(required = false, name = "direction", defaultValue = "ASC") Direction direction,
            @RequestParam(required = false, name = "role", defaultValue = "STUDENT") roles role,
            @RequestParam(required = false, name = "mode", defaultValue = "PREVIEW") String mode,
            @RequestParam(required = false, name = "fragment", defaultValue = "false") boolean fragment,
            Model model) {
        Page<TransactionDTO> transactionsOnPage = transactionService
                .getPaginatedTransactions(
                        role,
                        pageIndex - 1,
                        perPage,
                        sortBy,
                        direction);

        LinkRecord linkRecord;
        List<LinkRecord> pages = new ArrayList<>();
        for (int i = 0; i < transactionsOnPage.getTotalPages(); i++) {
            String isActive = "";
            if (i == transactionsOnPage.getNumber()) {
                isActive = "current";
            }
            linkRecord = new LinkRecord(isActive, perPage, i + 1, direction, role);
            pages.add(linkRecord);
        }

        model.addAllAttributes(
                Map.of(
                        "transactions", transactionsOnPage.getContent(),
                        "transactionsOnPage", transactionsOnPage,
                        "spa", spa.STUDENTS.getSpaString(),
                        "mode", "VIEW_ALL",
                        "pages", pages));

        model.addAllAttributes(
                Map.of(
                        "studentTransactions", new ArrayList<Student>()));

        if (fragment)
            return "admin/fragments/main/students-main.html :: students-main";

        return "admin/admin.html";
    }

    @GetMapping("/requests")
    public String requests(
            @RequestParam(required = false, name = "sortBy", defaultValue = "transactionAmount") String sortBy,
            @RequestParam(required = false, name = "perPage", defaultValue = "8") int perPage,
            @RequestParam(required = false, name = "pageIndex", defaultValue = "1") int pageIndex,
            @RequestParam(required = false, name = "direction", defaultValue = "ASC") Direction direction,
            @RequestParam(required = false, name = "role", defaultValue = "STUDENT") roles role,
            @RequestParam(required = false, name = "mode", defaultValue = "PREVIEW") String mode,
            @RequestParam(required = false, name = "fragment", defaultValue = "false") boolean fragment,
            Model model) {
        Page<TransactionDTO> transactionsOnPage = transactionService
                .getPaginatedTransactions(
                        role,
                        pageIndex - 1,
                        perPage,
                        sortBy,
                        direction);

        LinkRecord linkRecord;
        List<LinkRecord> pages = new ArrayList<>();
        for (int i = 0; i < transactionsOnPage.getTotalPages(); i++) {
            String isActive = "";
            if (i == transactionsOnPage.getNumber()) {
                isActive = "current";
            }
            linkRecord = new LinkRecord(isActive, perPage, i + 1, direction, role);
            pages.add(linkRecord);
        }

        model.addAllAttributes(
                Map.of(
                        "transactions", transactionsOnPage.getContent(),
                        "transactionsOnPage", transactionsOnPage,
                        "spa", spa.REQUESTS.getSpaString(),
                        "mode", "VIEW_ALL",
                        "pages", pages));

        model.addAllAttributes(
                Map.of(
                        "requests", new ArrayList<Request>()));

        if (fragment)
            return "admin/fragments/main/requests-main.html :: requests-main";

        return "admin/admin.html";
    }

    @GetMapping("/transactions")
    public String transactions(
            @RequestParam(required = false, name = "sortBy", defaultValue = "transactionAmount") String sortBy,
            @RequestParam(required = false, name = "perPage", defaultValue = "8") int perPage,
            @RequestParam(required = false, name = "pageIndex", defaultValue = "1") int pageIndex,
            @RequestParam(required = false, name = "direction", defaultValue = "ASC") Direction direction,
            @RequestParam(required = false, name = "role", defaultValue = "STUDENT") roles role,
            @RequestParam(required = false, name = "fragment", defaultValue = "false") boolean fragment,
            Model model) {
        Page<TransactionDTO> transactionsOnPage = transactionService
                .getPaginatedTransactions(
                        role,
                        pageIndex - 1,
                        perPage,
                        sortBy,
                        direction);

        LinkRecord linkRecord;
        List<LinkRecord> pages = new ArrayList<>();
        for (int i = 0; i < transactionsOnPage.getTotalPages(); i++) {
            String isActive = "";
            if (i == transactionsOnPage.getNumber()) {
                isActive = "current";
            }
            linkRecord = new LinkRecord(isActive, perPage, i + 1, direction, role);
            pages.add(linkRecord);
        }

        model.addAllAttributes(
                Map.of(
                        "transactions", transactionsOnPage.getContent(),
                        "transactionsOnPage", transactionsOnPage,
                        "spa", spa.TANSACTIONS.getSpaString(),
                        "mode", "VIEW_ALL",
                        "pages", pages));

        if (fragment)
            return "admin/fragments/main/transactions-main.html :: transactions-main";

        return "admin/admin.html";
    }

    @GetMapping("/downloadPhoto")
    public ResponseEntity<Resource> downloadPhoto(
            @RequestParam("albumId") Long profileId,
            @RequestParam("photoId") Long photoId, Authentication authentication) throws IOException {
        
        String email = authentication.getName();
        
        UserDTO userDTO = (UserDTO) userService.getUserByEmail(email, dtotype.FULL);
        ProfilePicture profilePicture = profileService.getProfileByProfileId(profileId);
        if (userDTO.getUserName() != profilePicture.getUser().getUserName()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        String filename = profilePicture.getProfilePhoto();

        Path path = Paths.get("uploads").resolve(filename);
        Resource resource = new UrlResource(path.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            throw new FileNotFoundException("File not found: " + filename);
        }

        String contentType = Files.probeContentType(path);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    // @GetMapping("/export")
    // public ResponseEntity<Resource> exportReport() {
    //     byte[] data = generateCsvReport();
    //     Resource resource = new ByteArrayResource(data);
    //     return ResponseEntity.ok()
    //             .contentType(MediaType.parseMediaType("text/csv"))
    //             .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"report.csv\"")
    //             .body(resource);
    // }

}
