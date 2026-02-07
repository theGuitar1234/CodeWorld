package az.codeworld.springboot.admin.controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.math.BigDecimal;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import org.springframework.dao.DataIntegrityViolationException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import az.codeworld.springboot.admin.dtos.RequestDTO;
import az.codeworld.springboot.admin.dtos.StudentDTO;
import az.codeworld.springboot.admin.dtos.TeacherDTO;
import az.codeworld.springboot.admin.dtos.UserDTO;
import az.codeworld.springboot.admin.dtos.create.TransactionCreateDTO;
import az.codeworld.springboot.admin.dtos.create.UserCreateDTO;
import az.codeworld.springboot.admin.dtos.dashboard.UserDashboardDTO;
import az.codeworld.springboot.admin.dtos.transactions.TransactionDTO;
import az.codeworld.springboot.admin.dtos.transactions.UserPayableDTO;
import az.codeworld.springboot.admin.dtos.update.UserAdminUpdateDTO;
import az.codeworld.springboot.admin.entities.Money;
import az.codeworld.springboot.admin.entities.Request;
import az.codeworld.springboot.admin.entities.Student;
import az.codeworld.springboot.admin.entities.Teacher;
import az.codeworld.springboot.admin.entities.Transaction;
import az.codeworld.springboot.admin.projections.UserAdminProjection;
import az.codeworld.springboot.admin.projections.UserLogoutProjection;
import az.codeworld.springboot.admin.records.ActivityRecord;
import az.codeworld.springboot.admin.records.GenericLinkRecord;
import az.codeworld.springboot.admin.records.Pagination;
import az.codeworld.springboot.admin.records.PaymentDueRecord;
import az.codeworld.springboot.admin.records.PaymentOverDueRecord;
import az.codeworld.springboot.admin.records.TransactionLinkRecord;
import az.codeworld.springboot.admin.records.UserCombineRecord;
import az.codeworld.springboot.admin.records.UserLatestRecord;
import az.codeworld.springboot.admin.services.ActivityService;
import az.codeworld.springboot.admin.services.LogoutService;
import az.codeworld.springboot.admin.services.RequestService;
import az.codeworld.springboot.admin.services.StudentService;
import az.codeworld.springboot.admin.services.TeacherService;
import az.codeworld.springboot.admin.services.TransactionService;
import az.codeworld.springboot.admin.services.UserService;
import az.codeworld.springboot.admin.services.serviceImpl.ActivityServiceImpl;

import az.codeworld.springboot.exceptions.CourseOfferingAlreadyExistsException;
import az.codeworld.springboot.exceptions.PasswordsMustBePresentException;
import az.codeworld.springboot.exceptions.PasswordsMustMatchException;
import az.codeworld.springboot.exceptions.SubjectAlreadyExistsException;
import az.codeworld.springboot.exceptions.UserNotFoundException;

import az.codeworld.springboot.security.filters.UserActivityFilter;
import az.codeworld.springboot.security.services.auditservices.AuditService;
import az.codeworld.springboot.security.services.authservices.RegistrationService;
import az.codeworld.springboot.security.services.emailservices.EmailService;
import az.codeworld.springboot.security.services.sessionservices.SessionKiller;

import az.codeworld.springboot.utilities.WriteLog;
import az.codeworld.springboot.utilities.configurations.ApplicationProperties;
import az.codeworld.springboot.utilities.configurations.ApplicationProperties.Payriff;
import az.codeworld.springboot.utilities.constants.accounterror;
import az.codeworld.springboot.utilities.constants.accountstatus;
import az.codeworld.springboot.utilities.constants.accountsuccess;
import az.codeworld.springboot.utilities.constants.dtotype;
import az.codeworld.springboot.utilities.constants.emailstatus;
import az.codeworld.springboot.utilities.constants.eventtype;
import az.codeworld.springboot.utilities.constants.mode;
import az.codeworld.springboot.utilities.constants.paymentDueStatus;
import az.codeworld.springboot.utilities.constants.roles;
import az.codeworld.springboot.utilities.constants.spa;
import az.codeworld.springboot.utilities.services.contactservices.ContactService;
import az.codeworld.springboot.utilities.services.paymentservices.PaymentOverDueService;
import az.codeworld.springboot.web.dtos.CourseEnrollmentDTO;
import az.codeworld.springboot.web.dtos.CourseOfferingDTO;
import az.codeworld.springboot.web.dtos.SubjectDTO;
import az.codeworld.springboot.web.dtos.create.SubjectCreateDTO;
import az.codeworld.springboot.web.entities.CourseOffering;
import az.codeworld.springboot.web.entities.ProfilePicture;
import az.codeworld.springboot.web.repositories.CourseOfferingRepository;
import az.codeworld.springboot.web.services.CourseOfferingService;
import az.codeworld.springboot.web.services.ImpressionService;
import az.codeworld.springboot.web.services.ProfileService;
import az.codeworld.springboot.web.services.SubjectService;

import jakarta.servlet.Registration;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserActivityFilter userActivityFilter;
    private final ApplicationProperties applicationProperties;
    private final ActivityService activityService;
    private final RequestService requestService;
    private final TransactionService transactionService;
    private final EmailService emailService;
    private final RegistrationService registrationService;
    private final ProfileService profileService;
    private final UserService userService;
    private final StudentService studentService;
    private final TeacherService teacherService;
    private final LogoutService logoutService;
    private final SessionKiller sessionKiller;
    private final ImpressionService impressionService;
    private final AuditService auditService;
    private final SubjectService subjectService;
    private final ContactService contactService;
    private final CourseOfferingRepository courseOfferingRepository;
    private final CourseOfferingService courseOfferingService;
    private final PaymentOverDueService paymentOverDueService;
    
    public AdminController(
        RequestService requestService,
        TransactionService transactionService,
        EmailService emailService,
        RegistrationService registrationService,
        ProfileService profileService,
        UserService userService,
        StudentService studentService,
        TeacherService teacherService,
        LogoutService logoutService,
        SessionKiller sessionKiller,
        ImpressionService impressionService,
        AuditService auditService,
        ActivityService activityService,
        UserActivityFilter userActivityFilter,
        SubjectService subjectService,
        ApplicationProperties applicationProperties,
        ContactService contactService,
        CourseOfferingRepository courseOfferingRepository,
        CourseOfferingService courseOfferingService,
        PaymentOverDueService paymentOverDueService
    ) {
        this.requestService = requestService;
        this.transactionService = transactionService;
        this.emailService = emailService;
        this.registrationService = registrationService;
        this.profileService = profileService;
        this.userService = userService;
        this.studentService = studentService;
        this.teacherService = teacherService;
        this.logoutService = logoutService;
        this.sessionKiller = sessionKiller;
        this.impressionService = impressionService;
        this.auditService = auditService;
        this.activityService = activityService;
        this.userActivityFilter = userActivityFilter;
        this.subjectService = subjectService;
        this.applicationProperties = applicationProperties;
        this.contactService = contactService;
        this.courseOfferingRepository = courseOfferingRepository;
        this.courseOfferingService = courseOfferingService;
        this.paymentOverDueService = paymentOverDueService;
    }

    @GetMapping({ "", "/" })
    @PreAuthorize("hasAuthority('ACCESS_ADMIN_PANEL')")
    public String admin(Model model) {

        int newThisMonth = userService.countTotalNewThisMonth();
        int bannedUsers = userService.countTotalBannedUsers();
        int activeUsers = userService.countTotalActiveUsers();
        int inActiveUsers = userService.countTotalInActiveUsers();

        float total = newThisMonth + bannedUsers + activeUsers + inActiveUsers;
        if (total == 0)
            total = 1;

        long totalImpressions = impressionService.countTotalImpressionsThisMonth();
        long totalLogins = auditService.countTotalLoginsThisMonth();
        long totalUsers = userService.countAllUsers();

        List<UserLatestRecord> userLatestRecords = userService.getLatestUsers();
        List<ActivityRecord> latestActivites = activityService.getLatestActivities();

        model.addAllAttributes(
                Map.of(
                        "newThisMonth", newThisMonth,
                        "bannedUsers", bannedUsers,
                        "inActiveUsers", inActiveUsers,
                        "activeUsers", activeUsers,
                        "totalImpressions", totalImpressions,
                        "totalLogins", totalLogins,
                        "totalUsers", totalUsers,
                        "latestUsers", userLatestRecords,
                        "latestActivities", latestActivites));

        model.addAllAttributes(
                Map.of(
                        "newThisMonthPercentage", (int) Math.floor((newThisMonth / total) * 100.0),
                        "bannedUsersPercentage", (int) Math.floor((bannedUsers / total) * 100.0),
                        "inActiveUsersPercentage", (int) Math.floor((inActiveUsers / total) * 100.0),
                        "activeUsersPercentage", (int) Math.floor((activeUsers / total) * 100.0)));

        model.addAllAttributes(
                Map.of(
                        "chartPoints", transactionService.getChartPoints(),
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

    //@ResponseBody
    @DeleteMapping("/rejectRequest/{requestId}")
    @PreAuthorize("hasAuthority('ACCESS_ADMIN_PANEL')")
    public String rejectRequest(@PathVariable("requestId") Long requestId, Model model) {
        try {
            RequestDTO requestDTO = requestService.getRequestById(requestId);
            requestService.deleteRequestByRequestId(requestId);
            registrationService.sendRejectionEmail(requestDTO);
            model.addAttribute("success", accountsuccess.REJECT_REQUEST_SUCCESS.getAccountSuccessString());
            return "admin/fragments/success/success.html :: success";
        } catch (Exception e) {
            model.addAttribute("error", accounterror.REJECT_REQUEST_ERROR.getAccountErrorString());
            return "admin/fragments/error/error.html :: error";
        }
    }

    //@ResponseBody
    @DeleteMapping("/acceptRequest/{requestId}")
    @PreAuthorize("hasAuthority('ACCESS_ADMIN_PANEL')")
    public String acceptRequest(@PathVariable("requestId") Long requestId, Model model) {
        try {
            RequestDTO requestDTO = requestService.getRequestById(requestId);
            //requestService.deleteRequestByRequestId(requestId);
            registrationService.sendAcceptanceEmail(requestDTO);
            model.addAttribute("success", accountsuccess.ACCEPT_REQUEST_SUCCESS.getAccountSuccessString());
            return "admin/fragments/success/success.html :: success";
        } catch (Exception e) {
            model.addAttribute("error", accounterror.ACCEPT_REQUEST_ERROR.getAccountErrorString());
            return "admin/fragments/error/error.html :: error";
        }
    }

    @PreAuthorize("hasAuthority('ACCESS_ADMIN_PANEL')")
    @GetMapping("/teacherTransactions")
    public String teacherTransactions(
            @RequestParam(required = false, name = "sortBy", defaultValue = "transactionAmount") String sortBy,
            @RequestParam(required = false, name = "perPage", defaultValue = "8") int perPage,
            @RequestParam(required = false, name = "pageIndex", defaultValue = "1") int pageIndex,
            @RequestParam(required = false, name = "direction", defaultValue = "ASC") Direction direction,
            @RequestParam(required = false, name = "role", defaultValue = "TEACHER") roles role,
            @RequestParam(required = false, name = "mode", defaultValue = "PREVIEW") String mode,
            @RequestParam(required = false, name = "fragment", defaultValue = "false") boolean fragment,
            @RequestParam(required = false, name = "startDate") LocalDate startDate,
            @RequestParam(required = false, name = "endDate") LocalDate endDate,
            HttpServletRequest request,
            Model model) {

        boolean spaRequest = isSpaRequest(fragment, request);

        fetchPaginatedTransactions(pageIndex, perPage, sortBy, direction, startDate, endDate, role, model);

        model.addAttribute(Map.of("teacherTransactions", new ArrayList<Teacher>()));

        return render(model, spa.TEACHER_TRANSACTIONS, spaRequest,
                "admin/fragments/main/teacher-transactions-main.html :: teacher-transactions-main");
    }

    @PreAuthorize("hasAuthority('ACCESS_ADMIN_PANEL')")
    @GetMapping("/teacherTransactions/getReport")
    public ResponseEntity<List<TransactionDTO>> getReportTeacherTransactions(
            @RequestParam(required = false, name = "sortBy", defaultValue = "transactionAmount") String sortBy,
            @RequestParam(required = false, name = "perPage", defaultValue = "8") int perPage,
            @RequestParam(required = false, name = "pageIndex", defaultValue = "1") int pageIndex,
            @RequestParam(required = false, name = "direction", defaultValue = "ASC") Direction direction,
            @RequestParam(required = false, name = "role", defaultValue = "TEACHER") roles role,
            @RequestParam(required = false, name = "mode", defaultValue = "PREVIEW") String mode,
            @RequestParam(required = false, name = "fragment", defaultValue = "false") boolean fragment,
            @RequestParam(required = false, name = "startDate") LocalDate startDate,
            @RequestParam(required = false, name = "endDate") LocalDate endDate,
            Model model) {

        Page<TransactionDTO> transactionsOnPage = fetchPaginatedTransactions(pageIndex, perPage, sortBy, direction,
                startDate, endDate, role, model);

        return ResponseEntity.ok(transactionsOnPage.get().toList());
    }

    @PreAuthorize("hasAuthority('ACCESS_ADMIN_PANEL')")
    @GetMapping("/studentTransactions")
    public String studentTransactions(
            @RequestParam(required = false, name = "sortBy", defaultValue = "transactionAmount") String sortBy,
            @RequestParam(required = false, name = "perPage", defaultValue = "8") int perPage,
            @RequestParam(required = false, name = "pageIndex", defaultValue = "1") int pageIndex,
            @RequestParam(required = false, name = "direction", defaultValue = "ASC") Direction direction,
            @RequestParam(required = false, name = "role", defaultValue = "STUDENT") roles role,
            @RequestParam(required = false, name = "mode", defaultValue = "PREVIEW") String mode,
            @RequestParam(required = false, name = "fragment", defaultValue = "false") boolean fragment,
            @RequestParam(required = false, name = "startDate") LocalDate startDate,
            @RequestParam(required = false, name = "endDate") LocalDate endDate,
            HttpServletRequest request,
            Model model) {

        boolean spaRequest = isSpaRequest(fragment, request);

        fetchPaginatedTransactions(pageIndex, perPage, sortBy, direction, startDate, endDate, role, model);

        model.addAttribute("studentTransactions", new ArrayList<Student>());

        return render(model, spa.STUDENT_TRANSACTIONS, spaRequest,
                "admin/fragments/main/student-transactions-main.html :: student-transactions-main");
    }

    @PreAuthorize("hasAuthority('ACCESS_ADMIN_PANEL')")
    @GetMapping("/studentTransactions/getReport")
    public ResponseEntity<List<TransactionDTO>> getReportStudentTransactions(
            @RequestParam(required = false, name = "sortBy", defaultValue = "transactionAmount") String sortBy,
            @RequestParam(required = false, name = "perPage", defaultValue = "8") int perPage,
            @RequestParam(required = false, name = "pageIndex", defaultValue = "1") int pageIndex,
            @RequestParam(required = false, name = "direction", defaultValue = "ASC") Direction direction,
            @RequestParam(required = false, name = "role", defaultValue = "STUDENT") roles role,
            @RequestParam(required = false, name = "mode", defaultValue = "PREVIEW") String mode,
            @RequestParam(required = false, name = "fragment", defaultValue = "false") boolean fragment,
            @RequestParam(required = false, name = "startDate") LocalDate startDate,
            @RequestParam(required = false, name = "endDate") LocalDate endDate,
            Model model) {

        Page<TransactionDTO> transactionsOnPage = fetchPaginatedTransactions(pageIndex, perPage, sortBy, direction,
                startDate, endDate, role, model);

        return ResponseEntity.ok(transactionsOnPage.get().toList());
    }

    @PreAuthorize("hasAuthority('ACCESS_ADMIN_PANEL')")
    @GetMapping("/requests")
    public String requests(
            @RequestParam(required = false, name = "sortBy", defaultValue = "transactionAmount") String sortBy,
            @RequestParam(required = false, name = "perPage", defaultValue = "8") int perPage,
            @RequestParam(required = false, name = "pageIndex", defaultValue = "1") int pageIndex,
            @RequestParam(required = false, name = "direction", defaultValue = "ASC") Direction direction,
            @RequestParam(required = false, name = "role", defaultValue = "STUDENT") roles role,
            @RequestParam(required = false, name = "mode", defaultValue = "PREVIEW") String mode,
            @RequestParam(required = false, name = "fragment", defaultValue = "false") boolean fragment,
            @RequestParam(required = false, name = "startDate") LocalDate startDate,
            @RequestParam(required = false, name = "endDate") LocalDate endDate,
            HttpServletRequest request,
            Model model) {

        boolean spaRequest = isSpaRequest(fragment, request);

        try {

            Set<String> allowedSort = Set.of("id", "expiresAt", "role");
            Pagination pagination = normalizePagination(pageIndex, perPage, sortBy, null, allowedSort, "id");

            pageIndex = pagination.pageIndex();
            perPage = pagination.perPage();
            sortBy = pagination.sortBy();

            Page<RequestDTO> transactionsOnPage = requestService.getPaginatedRequests(
                pageIndex - 1, 
                perPage, 
                sortBy, 
                direction
            );

            TransactionLinkRecord linkRecord;
            List<TransactionLinkRecord> pages = new ArrayList<>();
            for (int i = 0; i < transactionsOnPage.getTotalPages(); i++) {
                String isActive = "";
                if (i == transactionsOnPage.getNumber()) {
                    isActive = "current";
                }
                linkRecord = new TransactionLinkRecord(isActive, perPage, i + 1, direction, role);
                pages.add(linkRecord);
            }

            model.addAllAttributes(
                    Map.of(
                            "requests", transactionsOnPage.getContent(),
                            "transactionsOnPage", transactionsOnPage,
                            "pages", pages,
                            "sortBy", sortBy,
                            "mode", "VIEW_ALL",
                            "direction", direction.name()));
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getLocalizedMessage());
            return render(model, spa.REQUESTS, spaRequest, "admin/fragments/main/requests-main.html :: requests-main");
        }

        return render(model, spa.REQUESTS, spaRequest, "admin/fragments/main/requests-main.html :: requests-main");
    }

    @PreAuthorize("hasAuthority('ACCESS_ADMIN_PANEL')")
    @GetMapping("/requests/getReport")
    public ResponseEntity<List<RequestDTO>> getReportRequests(
            @RequestParam(required = false, name = "role", defaultValue = "STUDENT") roles role,
            @RequestParam(required = false, name = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(required = false, name = "perPage", defaultValue = "8") int perPage,
            @RequestParam(required = false, name = "pageIndex", defaultValue = "1") int pageIndex,
            @RequestParam(required = false, name = "direction", defaultValue = "ASC") Direction direction,
            @RequestParam(required = false, name = "fragment", defaultValue = "false") boolean fragment,
            HttpServletRequest request,
            Model model) {

        Page<RequestDTO> report = requestService.getPaginatedRequests(
            pageIndex - 1, 
            perPage, 
            sortBy, 
            direction
        );

        return ResponseEntity.ok(report.toList());
    }

    @PreAuthorize("hasAuthority('ACCESS_ADMIN_PANEL')")
    @GetMapping("/students")
    public String students(
            @RequestParam(required = false, name = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(required = false, name = "payableSortBy", defaultValue = "nextDate") String payableSortBy,
            @RequestParam(required = false, name = "perPage", defaultValue = "8") int perPage,
            @RequestParam(required = false, name = "role", defaultValue = "STUDENT") roles role,
            @RequestParam(required = false, name = "pageIndex", defaultValue = "1") int pageIndex,
            @RequestParam(required = false, name = "payablePageIndex", defaultValue = "1") int payablePageIndex,
            @RequestParam(required = false, name = "direction", defaultValue = "ASC") Direction direction,
            @RequestParam(required = false, name = "fragment", defaultValue = "false") boolean fragment,
            HttpServletRequest request,
            Model model) {

        boolean spaRequest = isSpaRequest(fragment, request);

        fetchPaginatedUsers(pageIndex, perPage, sortBy, direction, payablePageIndex, payableSortBy, role, model);

        return render(model, spa.STUDENTS, spaRequest, "admin/fragments/main/students-main.html :: students-main");
    }

    @PreAuthorize("hasAuthority('ACCESS_ADMIN_PANEL')")
    @GetMapping("/students/getReport")
    public ResponseEntity<List<UserCombineRecord>> getReportStudents(
            @RequestParam(required = false, name = "role", defaultValue = "STUDENT") roles role,
            @RequestParam(required = false, name = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(required = false, name = "payableSortBy", defaultValue = "nextDate") String payableSortBy,
            @RequestParam(required = false, name = "perPage", defaultValue = "8") int perPage,
            @RequestParam(required = false, name = "pageIndex", defaultValue = "1") int pageIndex,
            @RequestParam(required = false, name = "payablePageIndex", defaultValue = "1") int payablePageIndex,
            @RequestParam(required = false, name = "direction", defaultValue = "ASC") Direction direction,
            @RequestParam(required = false, name = "fragment", defaultValue = "false") boolean fragment,
            HttpServletRequest request,
            Model model) {

        WriteLog.main("The role must be STUDENT, the current role is : " + role.getRoleNameString(),
                AdminController.class);

        PageImpl<UserCombineRecord> report = fetchPaginatedUsers(pageIndex, perPage, sortBy, direction,
                payablePageIndex, payableSortBy, role, model);

        return ResponseEntity.ok(report.toList());
    }

    @PreAuthorize("hasAuthority('ACCESS_ADMIN_PANEL')")
    @GetMapping("/teachers")
    public String teachers(
            @RequestParam(required = false, name = "role", defaultValue = "TEACHER") roles role,
            @RequestParam(required = false, name = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(required = false, name = "payableSortBy", defaultValue = "nextDate") String payableSortBy,
            @RequestParam(required = false, name = "perPage", defaultValue = "8") int perPage,
            @RequestParam(required = false, name = "pageIndex", defaultValue = "1") int pageIndex,
            @RequestParam(required = false, name = "payablePageIndex", defaultValue = "1") int payablePageIndex,
            @RequestParam(required = false, name = "direction", defaultValue = "ASC") Direction direction,
            @RequestParam(required = false, name = "paymentDueStatus", defaultValue = "DUE") paymentDueStatus paymentDueStatus,
            @RequestParam(required = false, name = "fragment", defaultValue = "false") boolean fragment,
            HttpServletRequest request,
            Model model) {

        boolean spaRequest = isSpaRequest(fragment, request);

        WriteLog.main("This has to be an SPA request, is it? : " + spaRequest, AdminController.class);

        fetchPaginatedUsers(pageIndex, perPage, sortBy, direction, payablePageIndex, payableSortBy, role, model);
        listPaymentOverDues(paymentDueStatus, model);

        return render(model, spa.TEACHERS, spaRequest, "admin/fragments/main/teachers-main.html :: teachers-main");
    }

    @ResponseBody
    @PostMapping("/catchup/run")
    public String runCatchUpNow() {
        paymentOverDueService.synchAllTeacherPayDues();
        return "Catch-up executed.";
    }

    @PreAuthorize("hasAuthority('ACCESS_ADMIN_PANEL')")
    @GetMapping("/teachers/getReport")
    public ResponseEntity<List<UserCombineRecord>> getReportTeachers(
            @RequestParam(required = false, name = "role", defaultValue = "TEACHER") roles role,
            @RequestParam(required = false, name = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(required = false, name = "payableSortBy", defaultValue = "nextDate") String payableSortBy,
            @RequestParam(required = false, name = "perPage", defaultValue = "8") int perPage,
            @RequestParam(required = false, name = "pageIndex", defaultValue = "1") int pageIndex,
            @RequestParam(required = false, name = "payablePageIndex", defaultValue = "1") int payablePageIndex,
            @RequestParam(required = false, name = "direction", defaultValue = "ASC") Direction direction,
            @RequestParam(required = false, name = "fragment", defaultValue = "false") boolean fragment,
            HttpServletRequest request,
            Model model) {

        PageImpl<UserCombineRecord> report = fetchPaginatedUsers(pageIndex, perPage, sortBy, direction,
                payablePageIndex, payableSortBy, role, model);

        return ResponseEntity.ok(report.toList());
    }

    @PreAuthorize("hasAuthority('ACCESS_ADMIN_PANEL')")
    @GetMapping("/subjects")
    public String subjects(
            @RequestParam(required = false, name = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(required = false, name = "perPage", defaultValue = "8") int perPage,
            @RequestParam(required = false, name = "pageIndex", defaultValue = "1") int pageIndex,
            @RequestParam(required = false, name = "direction", defaultValue = "ASC") Direction direction,
            @RequestParam(required = false, name = "fragment", defaultValue = "false") boolean fragment,
            HttpServletRequest request,
            Model model) {

        boolean spaRequest = isSpaRequest(fragment, request);

        fetchPaginatedSubjects(pageIndex, perPage, sortBy, direction, model);

        return render(model, spa.SUBJECTS, spaRequest, "admin/fragments/main/subjects-main.html :: subjects-main");
    }

    @PreAuthorize("hasAuthority('ACCESS_ADMIN_PANEL')")
    @GetMapping("/subjects/getReport")
    public ResponseEntity<List<SubjectDTO>> getReportSubjects(
            @RequestParam(required = false, name = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(required = false, name = "perPage", defaultValue = "8") int perPage,
            @RequestParam(required = false, name = "pageIndex", defaultValue = "1") int pageIndex,
            @RequestParam(required = false, name = "direction", defaultValue = "ASC") Direction direction,
            @RequestParam(required = false, name = "fragment", defaultValue = "false") boolean fragment,
            HttpServletRequest request,
            Model model) {

        Page<SubjectDTO> report = fetchPaginatedSubjects(pageIndex, perPage, sortBy, direction, model);

        return ResponseEntity.ok(report.toList());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/fetchUser")
    public String fetchUser(
            @RequestParam(required = false, name = "fragment", defaultValue = "false") boolean fragment,
            HttpServletRequest request,
            Model model) {

        boolean spaRequest = isSpaRequest(fragment, request);

        return render(model, spa.USERS, spaRequest, "admin/fragments/main/users-main.html :: users-main");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/fetchUser")
    public String fetchUser(
            @RequestParam(required = false, name = "userId", defaultValue = "1000") Long userId,
            @RequestParam(required = false, name = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(required = false, name = "perPage", defaultValue = "8") int perPage,
            @RequestParam(required = false, name = "role", defaultValue = "STUDENT") roles role,
            @RequestParam(required = false, name = "pageIndex", defaultValue = "1") int pageIndex,
            @RequestParam(required = false, name = "direction", defaultValue = "ASC") Direction direction,
            @RequestParam(required = false, name = "fragment", defaultValue = "false") boolean fragment,
            HttpServletRequest request,
            Model model) {

        boolean spaRequest = isSpaRequest(fragment, request);

        Set<String> allowedSort = Set.of("id", "firstName", "lastName", "email", "affiliatedSince");
        Pagination pagination = normalizePagination(pageIndex, perPage, sortBy, null, allowedSort, "id");

        pageIndex = pagination.pageIndex();
        perPage = pagination.perPage();
        sortBy = pagination.sortBy();

        if (normalizeUserId(userId, fragment, model))
            return render(model, spa.USERS, spaRequest, "admin/fragments/main/users-main.html :: users-main");

        try {
            UserAdminProjection userAdminProjection = userService.getUserProjectionById(userId,
                    UserAdminProjection.class);

            if (userAdminProjection
                    .getRoles()
                    .stream()
                    .anyMatch(r -> roles.TEACHER.getRoleNameString().equals(r.getRoleNameString()))) {
                populateTeacherCourseOfferings(userId, model, pageIndex, perPage, sortBy, direction);
            }

            model.addAllAttributes(
                    Map.of(
                            "userProjection", userAdminProjection,
                            "userId", userId));

        } catch (RuntimeException e) {
            model.addAttribute("error", e.getLocalizedMessage());
        }

        return render(model, spa.USERS, spaRequest, "admin/fragments/main/users-main.html :: users-main");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/createUser")
    public String createUser(
            @RequestParam(required = false, name = "fragment", defaultValue = "false") boolean fragment,
            HttpServletRequest request,
            Model model) {

        boolean spaRequest = isSpaRequest(fragment, request);

        return render(model, spa.CREATE_USER, spaRequest, "admin/fragments/main/create-user-main.html :: create-user-main");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/createUser")
    public String createUser(
            @ModelAttribute @Valid UserCreateDTO userCreateDTO,
            @RequestParam(required = false, name = "fragment", defaultValue = "false") boolean fragment,
            HttpServletRequest request,
            Model model) throws PasswordsMustBePresentException {

        try {
            userService.createNewUserAdmin(userCreateDTO);
            model.addAttribute("success", accountsuccess.ACCOUNT_ADDED.getAccountSuccessString());
        } catch(DataIntegrityViolationException e) {
            model.addAttribute("error", e.getLocalizedMessage());
        } catch(PasswordsMustBePresentException e) {
            model.addAttribute("error", e.getExceptionMessage());
        } catch(PasswordsMustMatchException e) {
            model.addAttribute("error", e.getExceptionMessage());
        }

        boolean spaRequest = isSpaRequest(fragment, request);

        return render(model, spa.CREATE_USER, spaRequest, "admin/fragments/main/create-user-main.html :: create-user-main");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/updateUser")
    public String updateUser(
            @ModelAttribute("userAdminUpdateDTO") @Valid UserAdminUpdateDTO userAdminUpdateDTO,
            @RequestParam(required = false, name = "password2") String password2,
            @RequestParam(required = false, name = "userId", defaultValue = "1000") Long userId,
            @RequestParam(required = false, name = "fragment", defaultValue = "false") boolean fragment,
            @RequestParam(required = false, name = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(required = false, name = "perPage", defaultValue = "8") int perPage,
            @RequestParam(required = false, name = "role", defaultValue = "STUDENT") roles role,
            @RequestParam(required = false, name = "pageIndex", defaultValue = "1") int pageIndex,
            @RequestParam(required = false, name = "direction", defaultValue = "ASC") Direction direction,
            Model model,
            HttpServletRequest request) {

        boolean spaRequest = isSpaRequest(fragment, request);

        Set<String> allowedSort = Set.of("id", "firstName", "lastName", "email", "affiliatedSince");
        Pagination pagination = normalizePagination(pageIndex, perPage, sortBy, null, allowedSort, "id");

        pageIndex = pagination.pageIndex();
        perPage = pagination.perPage();
        sortBy = pagination.sortBy();

        if (normalizeUserId(userId, fragment, model))
            return render(model, spa.USERS, spaRequest, "admin/fragments/main/users-main.html :: users-main");

        try {
            if (!userAdminUpdateDTO.getPassword().equals(password2)) {
                throw new Exception("Passwords must match!");
            }

            UserAdminProjection userAdminProjection2 = userService.updateUserAdmin(userAdminUpdateDTO, userId);
            String actor = request.getUserPrincipal().getName();
            String subject = userAdminProjection2.getEmail();

            activityService.recordActivity(
                    eventtype.USER_UPDATED,
                    "User is Updated By Admin",
                    "Admin" + actor + " set Updated the User : " + userAdminProjection2.getEmail(),
                    actor,
                    subject);

            model.addAllAttributes(
                    Map.of(
                            "userProjection", userAdminProjection2,
                            "userId", userId));
        } catch (Exception e) {
            model.addAttribute("error", e.getLocalizedMessage());
        }

        try {
            UserAdminProjection userAdminProjection = userService.getUserProjectionById(userId,
                    UserAdminProjection.class);

            if (userAdminProjection
                    .getRoles()
                    .stream()
                    .anyMatch(r -> roles.TEACHER.getRoleNameString().equals(r.getRoleNameString()))) {

                populateTeacherCourseOfferings(userId, model, pageIndex, perPage, sortBy, direction);
            }

        } catch (RuntimeException e) {
            model.addAttribute("error", e.getLocalizedMessage());
        }

        return render(model, spa.USERS, spaRequest, "admin/fragments/main/users-main.html :: users-main");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/banUnbanUser/{id}")
    public String banUnbanUser(
            @PathVariable Long id,
            HttpServletResponse response,
            HttpServletRequest request,
            @RequestParam(required = false, name = "fragment", defaultValue = "false") boolean fragment,
            @RequestParam(required = false, name = "userId", defaultValue = "1000") Long userId,
            @RequestParam(required = false, name = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(required = false, name = "perPage", defaultValue = "8") int perPage,
            @RequestParam(required = false, name = "role", defaultValue = "STUDENT") roles role,
            @RequestParam(required = false, name = "pageIndex", defaultValue = "1") int pageIndex,
            @RequestParam(required = false, name = "direction", defaultValue = "ASC") Direction direction,
            @RequestParam(required = false, name = "payableSortBy", defaultValue = "nextDate") String payableSortBy,
            @RequestParam(required = false, name = "payablePageIndex", defaultValue = "1") int payablePageIndex,
            @RequestParam(required = false, name = "spa") spa spa,
            Model model) {

        boolean spaRequest = isSpaRequest(fragment, request);

        Set<String> allowedSort = Set.of("id", "firstName", "lastName", "email", "affiliatedSince");
        Pagination pagination = normalizePagination(pageIndex, perPage, sortBy, payableSortBy, allowedSort, "id");

        pageIndex = pagination.pageIndex();
        perPage = pagination.perPage();
        sortBy = pagination.sortBy();

        if (normalizeUserId(userId, fragment, model)) {
            switch (spa) {
                case TEACHERS -> {
                    fetchPaginatedUsers(pageIndex, perPage, sortBy, direction, payablePageIndex, payableSortBy, role,
                            model);
                }
                case STUDENTS -> {
                    fetchPaginatedUsers(pageIndex, perPage, sortBy, direction, payablePageIndex, payableSortBy, role,
                            model);
                }
                default -> {
                }
            }
            return render(model, spa, spaRequest, mainFragment(spa));
        }

        userService.banUnbanUser(id);
        UserLogoutProjection userLogoutProjection = userService.getUserProjectionById(id, UserLogoutProjection.class);
        sessionKiller.invalidateUserSessions(userLogoutProjection.getUserName());

        UserAdminProjection userAdminProjection = userService.getUserProjectionById(id, UserAdminProjection.class);

        String actor = request.getUserPrincipal().getName();
        String subject = userAdminProjection.getEmail();

        activityService.recordActivity(
                userAdminProjection.isBanned() ? eventtype.USER_BANNED : eventtype.USER_UNBANNED,
                userAdminProjection.isBanned() ? "User is Banned" : "User is UnBanned",
                "Admin" + actor + " set status of " + userAdminProjection.getEmail() + " to "
                        + (userAdminProjection.isBanned() ? "Banned" : "UnBanned"),
                actor,
                subject);

        model.addAllAttributes(
                Map.of(
                        "userProjection", userAdminProjection,
                        "nextDate", LocalDate.ofInstant(userAdminProjection.getNextDate(),
                                ZoneId.of(applicationProperties.getTime().getZone())),
                        "userId", id));

        try {
            if (userAdminProjection
                    .getRoles()
                    .stream()
                    .anyMatch(r -> roles.TEACHER.getRoleNameString().equals(r.getRoleNameString()))) {

                populateTeacherCourseOfferings(id, model, pageIndex, perPage, sortBy, direction);
            }

        } catch (RuntimeException e) {
            model.addAttribute("error", e.getLocalizedMessage());
        }

        switch (spa) {
            case TEACHERS -> {
                fetchPaginatedUsers(pageIndex, perPage, sortBy, direction, payablePageIndex, payableSortBy, role,
                        model);
            }
            case STUDENTS -> {
                fetchPaginatedUsers(pageIndex, perPage, sortBy, direction, payablePageIndex, payableSortBy, role,
                        model);
            }
            default -> {
            }
        }

        return render(model, spa, spaRequest, mainFragment(spa));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/deleteUser/{id}")
    public String deleteUser(
            @PathVariable Long id,
            HttpServletResponse response,
            HttpServletRequest request,
            @RequestParam(required = false, name = "userId", defaultValue = "1000") Long userId,
            @RequestParam(required = false, name = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(required = false, name = "perPage", defaultValue = "8") int perPage,
            @RequestParam(required = false, name = "role", defaultValue = "STUDENT") roles role,
            @RequestParam(required = false, name = "pageIndex", defaultValue = "1") int pageIndex,
            @RequestParam(required = false, name = "direction", defaultValue = "ASC") Direction direction,
            @RequestParam(required = false, name = "fragment", defaultValue = "false") boolean fragment,
            @RequestParam(required = false, name = "payableSortBy", defaultValue = "nextDate") String payableSortBy,
            @RequestParam(required = false, name = "payablePageIndex", defaultValue = "1") int payablePageIndex,
            @RequestParam(required = false, name = "startDate") LocalDate startDate,
            @RequestParam(required = false, name = "endDate") LocalDate endDate,
            @RequestParam(required = false, name = "spa") spa spa,
            Model model) {

        boolean spaRequest = isSpaRequest(fragment, request);

        if (id == null || id < 1) {
            model.addAttribute("error", "Please enter a valid user ID.");
            fetchPaginatedUsers(pageIndex, perPage, sortBy, direction, payablePageIndex, payableSortBy, role, model);
            return render(model, spa, spaRequest, mainFragment(spa));
        }

        UserLogoutProjection userLogoutProjection = userService.getUserProjectionById(id, UserLogoutProjection.class);
        sessionKiller.invalidateUserSessions(userLogoutProjection.getUserName());
        userService.deleteUserById(id);

        String actor = request.getUserPrincipal().getName();
        String subject = "User with ID : " + userLogoutProjection.getId();

        activityService.recordActivity(
                eventtype.USER_DELETED,
                "User is Deleted By Admin",
                "Admin" + actor + " deleted user with ID : " + userLogoutProjection.getId(),
                actor,
                subject);

        fetchPaginatedUsers(pageIndex, perPage, sortBy, direction, payablePageIndex, payableSortBy, role, model);

        return render(model, spa, spaRequest, mainFragment(spa));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/courseOfferings/{id}")
    public String courseOfferings(
            @RequestParam(required = false, name = "fragment", defaultValue = "false") boolean fragment,
            @RequestParam(required = false, name = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(required = false, name = "perPage", defaultValue = "8") int perPage,
            @RequestParam(required = false, name = "pageIndex", defaultValue = "1") int pageIndex,
            @RequestParam(required = false, name = "direction", defaultValue = "ASC") Direction direction,
            @PathVariable Long id,
            HttpServletRequest request,
            Model model) {

        boolean spaRequest = isSpaRequest(fragment, request);

        if (id == null || id < 1) {
            model.addAttribute("error", "Please enter a valid course offering ID.");
            return render(model, spa.COURSE_OFFERINGS, spaRequest,
                    "admin/fragments/main/users-main.html :: users-main");
        }

        try {

            Set<String> allowedSort = Set.of("student.id", "student.affiliationDate");
            Pagination pagination = normalizePagination(pageIndex, perPage, sortBy, null, allowedSort, "id");

            pageIndex = pagination.pageIndex();
            perPage = pagination.perPage();
            sortBy = pagination.sortBy();

            CourseOfferingDTO courseOfferingDTO = courseOfferingService.getCourseOfferingById(id);
            Page<CourseEnrollmentDTO> courseEnrollmentsOnPage = courseOfferingService.getPaginatedCourseEnrollments(
                    id,
                    pageIndex - 1,
                    perPage,
                    sortBy,
                    direction);

            GenericLinkRecord genericLinkRecord;
            List<GenericLinkRecord> pagesForCourseEnrollments = new ArrayList<>();

            for (int i = 0; i < courseEnrollmentsOnPage.getTotalPages(); i++) {
                String isActive = "";
                if (i == courseEnrollmentsOnPage.getNumber()) {
                    isActive = "current";
                }
                genericLinkRecord = new GenericLinkRecord(isActive, perPage, i + 1, direction);
                pagesForCourseEnrollments.add(genericLinkRecord);
            }

            model.addAllAttributes(
                    Map.of(
                            "courseOffering", courseOfferingDTO,
                            "courseEnrollmentsOnPage", courseEnrollmentsOnPage,
                            "pagesForCourseEnrollments", pagesForCourseEnrollments,
                            "sortBy", sortBy,
                            "direction", direction.name()));

        } catch (RuntimeException e) {
            model.addAttribute("error", e.getLocalizedMessage());
            return render(model, spa.USERS, spaRequest, "admin/fragments/main/users-main.html :: users-main");
        }

        return render(model, spa.COURSE_OFFERINGS, spaRequest,
                "admin/fragments/main/course-offerings-main.html :: course-offerings-main");
    }

    @PreAuthorize("hasAuthority('ACCESS_ADMIN_PANEL')")
    @GetMapping("/courseOfferings/{id}/getReport")
    public ResponseEntity<List<CourseEnrollmentDTO>> getReportCourseOfferings(
            @RequestParam(required = false, name = "fragment", defaultValue = "false") boolean fragment,
            @RequestParam(required = false, name = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(required = false, name = "perPage", defaultValue = "8") int perPage,
            @RequestParam(required = false, name = "pageIndex", defaultValue = "1") int pageIndex,
            @RequestParam(required = false, name = "direction", defaultValue = "ASC") Direction direction,
            @PathVariable Long id,
            HttpServletRequest request,
            Model model) {

        Page<CourseEnrollmentDTO> report = courseOfferingService.getPaginatedCourseEnrollments(
                id,
                pageIndex - 1,
                perPage,
                sortBy,
                direction);

        return ResponseEntity.ok(report.toList());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/createCourseOffering")
    public String createCourseOffering(
            // @RequestParam(required = false, name = "userId", defaultValue = "1000") Long
            // userId,
            @RequestParam(required = false, name = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(required = false, name = "perPage", defaultValue = "8") int perPage,
            @RequestParam(required = false, name = "role", defaultValue = "STUDENT") roles role,
            @RequestParam(required = false, name = "pageIndex", defaultValue = "1") int pageIndex,
            @RequestParam(required = false, name = "direction", defaultValue = "ASC") Direction direction,
            @RequestParam(required = false, name = "fragment", defaultValue = "false") boolean fragment,
            @RequestParam Long teacherId,
            @RequestParam Long subjectId,
            @RequestParam(required = false) List<Long> studentIds,
            HttpServletRequest request,
            Model model) {

        boolean spaRequest = isSpaRequest(fragment, request);

        if (normalizeUserId(teacherId, fragment, model))
            return render(model, spa.USERS, spaRequest, "admin/fragments/main/users-main.html :: users-main");

        try {
            teacherService.addCourseOffering(teacherId, subjectId, studentIds);

            Set<String> allowedSort = Set.of("id", "firstName", "lastName", "email", "affiliatedSince");
            Pagination pagination = normalizePagination(pageIndex, perPage, sortBy, null, allowedSort, "id");

            pageIndex = pagination.pageIndex();
            perPage = pagination.perPage();
            sortBy = pagination.sortBy();

            UserAdminProjection userAdminProjection = userService.getUserProjectionById(teacherId,
                    UserAdminProjection.class);

            if (userAdminProjection
                    .getRoles()
                    .stream()
                    .anyMatch(r -> roles.TEACHER.getRoleNameString().equals(r.getRoleNameString()))) {
                populateTeacherCourseOfferings(teacherId, model, pageIndex, perPage, sortBy, direction);
            }

            model.addAllAttributes(
                    Map.of(
                            "userProjection", userAdminProjection,
                            "userId", teacherId));

        } catch (CourseOfferingAlreadyExistsException e) {
            model.addAttribute("error", e.getExceptionMessage());
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getLocalizedMessage());
        }

        return render(model, spa.USERS, spaRequest, "admin/fragments/main/users-main.html :: users-main");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/deleteCourseOffering/{id}")
    public String deleteCourseOffering(
            @PathVariable Long id,
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = false, name = "userId", defaultValue = "1000") Long userId,
            @RequestParam(required = false, name = "fragment", defaultValue = "false") boolean fragment,
            @RequestParam(required = false, name = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(required = false, name = "perPage", defaultValue = "8") int perPage,
            @RequestParam(required = false, name = "role", defaultValue = "STUDENT") roles role,
            @RequestParam(required = false, name = "pageIndex", defaultValue = "1") int pageIndex,
            @RequestParam(required = false, name = "direction", defaultValue = "ASC") Direction direction,
            Model model) {

        boolean spaRequest = isSpaRequest(fragment, request);

        if (id == null || id < 1) {
            model.addAttribute("error", "Please enter a valid Course Offering ID.");
            return render(model, spa.USERS, spaRequest, "admin/fragments/main/users-main.html :: users-main");
        }

        courseOfferingRepository.deleteById(id);

        String actor = request.getUserPrincipal().getName();
        String subject = "Subject with ID : " + id;

        activityService.recordActivity(
                eventtype.COURSE_OFFERING_DELETED,
                "Course Offering is Deleted By Admin",
                "Admin" + actor + " deleted Course Offering with ID : " + id,
                actor,
                subject);

        if (normalizeUserId(userId, fragment, model))
            return render(model, spa.USERS, spaRequest, "admin/fragments/main/users-main.html :: users-main");

        try {
            UserAdminProjection userAdminProjection = userService.getUserProjectionById(userId,
                    UserAdminProjection.class);

            if (userAdminProjection
                    .getRoles()
                    .stream()
                    .anyMatch(r -> roles.TEACHER.getRoleNameString().equals(r.getRoleNameString()))) {

                populateTeacherCourseOfferings(userId, model, pageIndex, perPage, sortBy, direction);
            }

            model.addAllAttributes(
                    Map.of(
                            "userProjection", userAdminProjection,
                            "userId", userId));

        } catch (RuntimeException e) {
            model.addAttribute("error", e.getLocalizedMessage());
        }

        model.addAttribute("success", "CourseOffering is deleted Successfully");

        return render(model, spa.USERS, spaRequest, "admin/fragments/main/users-main.html :: users-main");
    }

    // @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/createNewSubject")
    public String createNewSubject(
            @RequestParam(required = false, name = "userId", defaultValue = "1000") Long userId,
            @RequestParam(required = false, name = "fragment", defaultValue = "false") boolean fragment,
            @RequestParam(required = false, name = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(required = false, name = "perPage", defaultValue = "8") int perPage,
            @RequestParam(required = false, name = "role", defaultValue = "STUDENT") roles role,
            @RequestParam(required = false, name = "pageIndex", defaultValue = "1") int pageIndex,
            @RequestParam(required = false, name = "direction", defaultValue = "ASC") Direction direction,
            @ModelAttribute @Valid SubjectCreateDTO subjectCreateDTO,
            HttpServletRequest request,
            Model model) {

        boolean spaRequest = isSpaRequest(fragment, request);

        try {
            subjectService.createNewSubject(subjectCreateDTO);
            model.addAttribute("success", "Subject created");
        } catch (SubjectAlreadyExistsException e) {
            model.addAttribute("error", e.getExceptionMessage());
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getLocalizedMessage());
        }

        fetchPaginatedSubjects(pageIndex, perPage, sortBy, direction, model);

        return render(model, spa.SUBJECTS, spaRequest, "admin/fragments/main/subjects-main.html :: subjects-main");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/deleteSubject/{id}")
    public String deleteSubject(
            @PathVariable Long id,
            @RequestParam(required = false, name = "userId", defaultValue = "1000") Long userId,
            @RequestParam(required = false, name = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(required = false, name = "perPage", defaultValue = "8") int perPage,
            @RequestParam(required = false, name = "role", defaultValue = "STUDENT") roles role,
            @RequestParam(required = false, name = "pageIndex", defaultValue = "1") int pageIndex,
            @RequestParam(required = false, name = "direction", defaultValue = "ASC") Direction direction,
            @RequestParam(required = false, name = "fragment", defaultValue = "false") boolean fragment,
            HttpServletResponse response,
            HttpServletRequest request,
            Model model) {

        boolean spaRequest = isSpaRequest(fragment, request);

        if (id == null || id < 1) {
            model.addAttribute("error", "Please enter a valid subject ID.");
            fetchPaginatedSubjects(pageIndex, perPage, sortBy, direction, model);
            return render(model, spa.SUBJECTS, spaRequest, "admin/fragments/main/subjects-main.html :: subjects-main");
        }

        try {

            subjectService.deleteSubjectById(id);
            model.addAttribute("success", "Subject Removed");

            String actor = request.getUserPrincipal().getName();
            String subject = "Subject with ID : " + id;

            activityService.recordActivity(
                    eventtype.SUBJECT_DELETED,
                    "Subject is Deleted By Admin",
                    "Admin" + actor + " deleted subject with ID : " + id,
                    actor,
                    subject);

        } catch (DataIntegrityViolationException e) {
            model.addAttribute("error", "Cannot delete subject: it is used by course offerings.");
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getLocalizedMessage());
        }

        fetchPaginatedSubjects(pageIndex, perPage, sortBy, direction, model);

        return render(model, spa.SUBJECTS, spaRequest, "admin/fragments/main/subjects-main.html :: subjects-main");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/notifyStudent/{studentId}")
    public String notifyStudent(
            @PathVariable Long studentId,
            Model model) {
        try {
            contactService.sendNotifyStudentEmail(studentId);
            String succ = "Email sent!";
            model.addAttribute("success", succ);
            return "admin/fragments/success/success.html :: success";
        } catch (Exception e) {
            String err = "Failed to send the email";
            model.addAttribute("error", err);
            return "admin/fragments/error/error.html :: error";
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/recordTransaction")
    public String recordTransaction(
            @RequestParam(required = false, name = "userId", defaultValue = "1000") Long userId,
            @RequestParam(required = false, name = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(required = false, name = "perPage", defaultValue = "8") int perPage,
            @RequestParam(required = false, name = "role", defaultValue = "STUDENT") roles role,
            @RequestParam(required = false, name = "pageIndex", defaultValue = "1") int pageIndex,
            @RequestParam(required = false, name = "direction", defaultValue = "ASC") Direction direction,
            @RequestParam(required = false, name = "fragment", defaultValue = "false") boolean fragment,
            @RequestParam(required = false, name = "payableSortBy", defaultValue = "nextDate") String payableSortBy,
            @RequestParam(required = false, name = "payablePageIndex", defaultValue = "1") int payablePageIndex,
            @RequestParam(required = false, name = "startDate") LocalDate startDate,
            @RequestParam(required = false, name = "endDate") LocalDate endDate,
            HttpServletRequest request,
            HttpServletResponse response,
            @ModelAttribute @Valid TransactionCreateDTO transactionCreateDTO,
            @RequestParam Long teacherId,
            Model model) {

        boolean spaRequest = isSpaRequest(fragment, request);

        WriteLog.main("Recording the transactions... is this request an SPA? : " + spaRequest, AdminController.class);

        try {
            transactionService.recordTransaction(teacherId, transactionCreateDTO);
            model.addAttribute("success", "Transaction Recorded");

            WriteLog.main("Successfully recorded the transaction", AdminController.class);
        } catch (RuntimeException e) {
            WriteLog.main("An Error happened during the recording : " + e.getLocalizedMessage(), AdminController.class);
            model.addAttribute("error", e.getLocalizedMessage());
        }

        WriteLog.main("Make Sure you are passing the correct role (TEACHER), the current role is : "
                + role.getRoleNameString(), AdminController.class);

        fetchPaginatedUsers(pageIndex, perPage, sortBy, direction, payablePageIndex, payableSortBy, role, model);

        return render(model, spa.TEACHERS, spaRequest, "admin/fragments/main/teachers-main.html :: teachers-main");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/downloadPhoto")
    public ResponseEntity<Resource> downloadPhoto(
            @RequestParam("albumId") Long profileId,
            @RequestParam("photoId") Long photoId, Authentication authentication) throws IOException {

        String email = authentication.getName();

        UserDTO userDTO = (UserDTO) userService.getUserByEmail(email, dtotype.FULL);
        ProfilePicture profilePicture = profileService.getProfileByProfileId(profileId);

        if (profilePicture.getUser() == null || !userDTO.getUserName().equals(profilePicture.getUser().getUserName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        String filename = profilePicture.getProfilePhoto();
        if (filename == null || filename.isBlank()) {
            return ResponseEntity.notFound().build();
        }

        if (filename.startsWith("/")) {
            filename = filename.substring(1);
        }

        Path path = Paths.get(System.getProperty("user.dir")).resolve(filename).normalize();
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
    // byte[] data = generateCsvReport();
    // Resource resource = new ByteArrayResource(data);
    // return ResponseEntity.ok()
    // .contentType(MediaType.parseMediaType("text/csv"))
    // .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;
    // filename=\"report.csv\"")
    // .body(resource);
    // }

    private String mainFragment(spa s) {
        return switch (s) {
            case STUDENTS -> "admin/fragments/main/students-main.html :: students-main";
            case TEACHERS -> "admin/fragments/main/teachers-main.html :: teachers-main";
            case USERS -> "admin/fragments/main/users-main.html :: users-main";
            default -> "admin/admin.html";
        };
    }

    private Pagination normalizePagination(
            int pageIndex,
            int perPage,
            String sortBy,
            String payableSortBy,
            Set<String> allowedSort,
            String defaultSort) {
        pageIndex = Math.max(pageIndex, 1);
        perPage = Math.min(Math.max(perPage, 1), 50);

        if (sortBy == null || !allowedSort.contains(sortBy))
            sortBy = defaultSort;
        if (payableSortBy == null || !allowedSort.contains(payableSortBy))
            payableSortBy = defaultSort;

        return new Pagination(pageIndex, perPage, sortBy, payableSortBy);
    }

    private boolean normalizeUserId(Long userId, boolean fragment, Model model) {
        if (userId == null || userId < 1) {
            model.addAttribute("error", "Please enter a valid user ID.");
            return true;
        }
        return false;
    }

    private boolean isSpaRequest(
            boolean fragment,
            HttpServletRequest request) {
        if (fragment)
            return true;
        String requestHeader = request.getHeader("X-Requested-With");
        if ("XMLHttpRequest".equalsIgnoreCase(requestHeader))
            return true;

        String spaHeader = request.getHeader("X-SPA");
        return "true".equalsIgnoreCase(spaHeader);
    }

    private String render(Model model, spa spa, boolean spaRequest, String fragmentView) {
        model.addAttribute("spa", spa.getSpaString());
        return spaRequest ? fragmentView : "admin/admin.html";
    }

    private void listPaymentOverDues(
        paymentDueStatus paymentDueStatus,
        Model model
    ) {
        List<PaymentOverDueRecord> paymentOverDueRecords = paymentOverDueService.listPaymentOverDues(paymentDueStatus);
        model.addAttribute("paymentOverDues", paymentOverDueRecords);
    }

    private Page<SubjectDTO> fetchPaginatedSubjects(
            int pageIndex,
            int perPage,
            String sortBy,
            Direction direction,
            Model model) {

        Set<String> allowedSort = Set.of("id", "subjectTitle");
        Pagination pagination = normalizePagination(pageIndex, perPage, sortBy, null, allowedSort, "id");

        pageIndex = pagination.pageIndex();
        perPage = pagination.perPage();
        sortBy = pagination.sortBy();

        Page<SubjectDTO> subjectsOnPage = subjectService.getPaginatedSubjects(
                pageIndex - 1,
                perPage,
                sortBy,
                direction);

        GenericLinkRecord genericLinkRecord;

        List<GenericLinkRecord> pagesForSubjects = new ArrayList<>();

        for (int i = 0; i < subjectsOnPage.getTotalPages(); i++) {
            String isActive = "";
            if (i == subjectsOnPage.getNumber()) {
                isActive = "current";
            }
            genericLinkRecord = new GenericLinkRecord(isActive, perPage, i + 1, direction);
            pagesForSubjects.add(genericLinkRecord);
        }

        model.addAllAttributes(
                Map.of(
                        "subjectsOnPage", subjectsOnPage,
                        "pagesForSubjects", pagesForSubjects,
                        "sortBy", sortBy,
                        "direction", direction.name()));

        return subjectsOnPage;
    }

    private PageImpl<UserCombineRecord> fetchPaginatedUsers(
            int pageIndex,
            int perPage,
            String sortBy,
            Direction direction,
            int payablePageIndex,
            String payableSortBy,
            roles role,
            Model model) {
        
        WriteLog.main("Made it to fetchPaginatedUsers(), current role must be teacher, the current role is : " + role.getRoleNameString(), AdminController.class);

        Set<String> allowedSort = Set.of("affiliationDate", "id", "payment", "nextDate");
        Pagination pagination = normalizePagination(pageIndex, perPage, sortBy, payableSortBy, allowedSort, "id");

        pageIndex = pagination.pageIndex();
        perPage = pagination.perPage();
        sortBy = pagination.sortBy();
        payableSortBy = pagination.payableSortBy();

        GenericLinkRecord genericLinkRecord;
        TransactionLinkRecord transactionLinkRecord;

        Page<?> usersOnPage = Page.empty();

        switch (role) {
            case TEACHER -> {

                WriteLog.main("This is a teacher and the switch case made it here as it should", AdminController.class);

                usersOnPage = teacherService.getPaginatedTeachers(
                        pageIndex - 1,
                        perPage,
                        sortBy,
                        direction);

                usersOnPage.forEach(u -> {
                    WriteLog.main("Results : " + u.toString(), AdminController.class);
                });
            }
            case STUDENT -> {
                usersOnPage = studentService.getPaginatedStudents(
                        pageIndex - 1,
                        perPage,
                        sortBy,
                        direction);
            }
            default -> throw new IllegalArgumentException("Unsupported role");
        }

        Page<UserPayableDTO> payableUsersOnPage = userService.getPaginatedPayableUsers(
                role,
                payablePageIndex - 1,
                perPage,
                payableSortBy,
                direction);

        List<GenericLinkRecord> pagesForUsers = new ArrayList<>();
        List<TransactionLinkRecord> pagesForPayableUsers = new ArrayList<>();

        for (int i = 0; i < usersOnPage.getTotalPages(); i++) {
            String isActive = "";
            if (i == usersOnPage.getNumber()) {
                isActive = "current";
            }
            genericLinkRecord = new GenericLinkRecord(isActive, perPage, i + 1, direction);
            pagesForUsers.add(genericLinkRecord);
        }

        for (int i = 0; i < payableUsersOnPage.getTotalPages(); i++) {
            String isActive = "";
            if (i == payableUsersOnPage.getNumber()) {
                isActive = "current";
            }
            transactionLinkRecord = new TransactionLinkRecord(isActive, perPage, i + 1, direction, role);
            pagesForPayableUsers.add(transactionLinkRecord);
        }

        switch (role) {
            case TEACHER -> {
                model.addAllAttributes(
                        Map.of(
                                "teachersOnPage", usersOnPage,
                                "payableTeachersOnPage", payableUsersOnPage,
                                "pagesForTeachers", pagesForUsers,
                                "pagesForPayableTeachers", pagesForPayableUsers,
                                "transaction", new TransactionCreateDTO(),
                                "sortBy", sortBy,
                                "payableSortBy", payableSortBy,
                                "direction", direction.name()));
            }
            case STUDENT -> {
                model.addAllAttributes(
                        Map.of(
                                "studentsOnPage", usersOnPage,
                                "payableStudentsOnPage", payableUsersOnPage,
                                "pagesForStudents", pagesForUsers,
                                "pagesForPayableStudents", pagesForPayableUsers,
                                "sortBy", sortBy,
                                "payableSortBy", payableSortBy,
                                "direction", direction.name()));
            }
            default -> throw new IllegalArgumentException("Unsupported role");
        }

        return getUsersReport(role, usersOnPage, payableUsersOnPage);

    }

    private PageImpl<UserCombineRecord> getUsersReport(
            roles role,
            Page<?> usersOnPage,
            Page<UserPayableDTO> payableUsersOnPage) {
        List<UserCombineRecord> merged = new ArrayList<>();

        switch (role) {
            case TEACHER -> {
                merged.addAll(usersOnPage.getContent()
                        .stream()
                        .map(u -> {
                            TeacherDTO t = (TeacherDTO) u;
                            return new UserCombineRecord(
                                    t.getId(),
                                    t.getFirstName(),
                                    t.getLastName(),
                                    null,
                                    null,
                                    t.getEmail(),
                                    t.getRole(),
                                    t.getAffiliatedSince(),
                                    t.isBanned());
                        })
                        .toList());
            }
            case STUDENT -> {
                merged.addAll(usersOnPage.getContent()
                        .stream()
                        .map(u -> {
                            StudentDTO s = (StudentDTO) u;
                            return new UserCombineRecord(
                                    s.getId(),
                                    s.getFirstName(),
                                    s.getLastName(),
                                    null,
                                    null,
                                    s.getEmail(),
                                    s.getRole(),
                                    s.getAffiliatedSince(),
                                    s.isBanned());
                        })
                        .toList());
            }
            default -> throw new IllegalArgumentException("Unsupported role");
        }

        merged.addAll(payableUsersOnPage.getContent()
                .stream()
                .map(u -> new UserCombineRecord(
                        u.getId(),
                        u.getFirstName(),
                        u.getLastName(),
                        String.valueOf(u.getPayment().getAmount()) + String.valueOf(u.getPayment().getCurrency()),
                        u.getNextDate(),
                        null,
                        role,
                        null,
                        null))
                .toList());

        long total = usersOnPage.getTotalElements() + payableUsersOnPage.getTotalElements();

        return new PageImpl<>(merged, usersOnPage.getPageable(), total);
    }

    private Page<TransactionDTO> fetchPaginatedTransactions(
            int pageIndex,
            int perPage,
            String sortBy,
            Direction direction,
            LocalDate startDate,
            LocalDate endDate,
            roles role,
            Model model) {

        Set<String> allowedSort = Set.of("transactionTime", "transactionAmount");
        Pagination pagination = normalizePagination(pageIndex, perPage, sortBy, null, allowedSort, "transactionAmount");

        pageIndex = pagination.pageIndex();
        perPage = pagination.perPage();
        sortBy = pagination.sortBy();

        Page<TransactionDTO> transactionsOnPage = transactionService
                .getPaginatedTransactions(
                        Optional.ofNullable(startDate).orElseGet(() -> LocalDate.ofEpochDay(0))
                                .atStartOfDay(ZoneId.of(applicationProperties.getTime().getZone())).toInstant(),
                        Optional.ofNullable(endDate).orElseGet(() -> LocalDate.now().plusDays(1))
                                .atStartOfDay(ZoneId.of(applicationProperties.getTime().getZone())).toInstant(),
                        role,
                        pageIndex - 1,
                        perPage,
                        sortBy,
                        direction);

        TransactionLinkRecord linkRecord;
        List<TransactionLinkRecord> pages = new ArrayList<>();
        for (int i = 0; i < transactionsOnPage.getTotalPages(); i++) {
            String isActive = "";
            if (i == transactionsOnPage.getNumber()) {
                isActive = "current";
            }
            linkRecord = new TransactionLinkRecord(isActive, perPage, i + 1, direction, role);
            pages.add(linkRecord);
        }

        model.addAllAttributes(
                Map.of(
                        "transactions", transactionsOnPage.getContent(),
                        "transactionsOnPage", transactionsOnPage,
                        "mode", "VIEW_ALL",
                        "pages", pages,
                        "sortBy", sortBy,
                        "direction", direction.name()));

        return transactionsOnPage;
    }

    private void populateTeacherCourseOfferings(
            Long userId,
            Model model,
            int pageIndex,
            int perPage,
            String sortBy,
            Direction direction) {
        Page<StudentDTO> studentsOnPage = studentService.getPaginatedStudents(
                pageIndex - 1,
                perPage,
                sortBy,
                direction);

        List<SubjectDTO> allSubjects = subjectService.getAllSubjects();

        GenericLinkRecord genericLinkRecord;

        List<GenericLinkRecord> pagesForStudents = new ArrayList<>();

        for (int i = 0; i < studentsOnPage.getTotalPages(); i++) {
            String isActive = "";
            if (i == studentsOnPage.getNumber()) {
                isActive = "current";
            }
            genericLinkRecord = new GenericLinkRecord(isActive, perPage, i + 1, direction);
            pagesForStudents.add(genericLinkRecord);
        }

        List<CourseOffering> courseOfferings = courseOfferingRepository.findByTeacher_Id(userId);

        model.addAllAttributes(
                Map.of(
                        "courseOfferings", courseOfferings,
                        "studentsOnPage", studentsOnPage,
                        "allSubjects", allSubjects,
                        "pagesForStudents", pagesForStudents));
    }

}
