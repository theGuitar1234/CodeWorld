package az.codeworld.springboot.admin.controllers;

import java.awt.print.PrinterIOException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import az.codeworld.springboot.admin.dtos.StudentDTO;
import az.codeworld.springboot.admin.dtos.TeacherDTO;
import az.codeworld.springboot.admin.dtos.UserDTO;
import az.codeworld.springboot.admin.dtos.create.TransactionCreateDTO;
import az.codeworld.springboot.admin.dtos.dashboard.UserDashboardDTO;
import az.codeworld.springboot.admin.projections.UserIdProjection;
import az.codeworld.springboot.admin.records.GenericLinkRecord;
import az.codeworld.springboot.admin.records.TransactionLinkRecord;
import az.codeworld.springboot.admin.services.TeacherService;
import az.codeworld.springboot.admin.services.TransactionService;
import az.codeworld.springboot.admin.services.UserService;
import az.codeworld.springboot.exceptions.ClassSectionAlreadyExistsException;
import az.codeworld.springboot.utilities.WriteLog;
import az.codeworld.springboot.utilities.constants.dtotype;
import az.codeworld.springboot.utilities.constants.roles;
import az.codeworld.springboot.web.dtos.ClassSectionDTO;
import az.codeworld.springboot.web.dtos.SubjectDTO;
import az.codeworld.springboot.web.entities.ClassSection;
import az.codeworld.springboot.web.entities.Subject;
import az.codeworld.springboot.web.mappers.SubjectMapper;
import az.codeworld.springboot.web.repositories.CourseOfferingRepository;
import az.codeworld.springboot.web.services.ClassSectionService;
import io.swagger.v3.oas.models.Paths;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequestMapping("/students")
public class StudentController {

    private final ClassSectionAlreadyExistsException classSectionAlreadyExistsException;
    private final TransactionService transactionService;
    private final UserService userService;
    private final TeacherService teacherService;
    private final ClassSectionService classSectionService;
    private final CourseOfferingRepository courseOfferingRepository;

    public StudentController(
        UserService userService,
        TeacherService teacherService,
        ClassSectionService classSectionService,
        ClassSectionAlreadyExistsException classSectionAlreadyExistsException,
        CourseOfferingRepository courseOfferingRepository,
        TransactionService transactionService
    ) {
        this.userService = userService;
        this.teacherService = teacherService;
        this.classSectionService = classSectionService;
        this.classSectionAlreadyExistsException = classSectionAlreadyExistsException;
        this.courseOfferingRepository = courseOfferingRepository;
        this.transactionService = transactionService;
    }

    @GetMapping({"/", ""})
    public String students(
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(name = "classDate", required = false) LocalDate classdate,
            @RequestParam(name = "subject", required = false, defaultValue = "0") int subject,
            Principal principal,
            Model model) {

        WriteLog.main("After ClassSection is added, it is supposed to be received with a new date, the current date is : " + classdate, StudentController.class);

        String userName = principal.getName();
        UserDTO userDTO = (UserDTO) userService.getUserByUserName(userName, dtotype.FULL);
        TeacherDTO teacherDTO = teacherService.getTeacherByUserName(userName);

        List<ClassSectionDTO> classSections = classSectionService
                .getClassSectionsByTeacherAndDate(
                        userName,
                        Optional.ofNullable(classdate).orElseGet(() -> LocalDate.now()));

        List<SubjectDTO> teachingSubjects = new ArrayList<>();

        courseOfferingRepository.findByTeacher_Id(teacherDTO.getId())
                .forEach(c -> teachingSubjects.add(SubjectMapper.toSubjectDTO(c.getSubject())));

        List<List<StudentDTO>> studentsInSubjects = new ArrayList<>();

        for (SubjectDTO subjectDTO : teachingSubjects) {
            studentsInSubjects.add(teacherService.getStudentsInSubject(teacherDTO.getId(), subjectDTO.getId()));
        }

        model.addAllAttributes(
                Map.of(
                        "user", userDTO,
                        "teacher", teacherDTO,
                        "classDate", Optional.ofNullable(classdate).orElseGet(LocalDate::now),
                        "subject", subject,
                        "classSections", Page.empty().getContent(),
                        "classSectionsOnPage", classSections,
                        "studentsInSubjects", studentsInSubjects,
                        "teachingSubjects", teachingSubjects,
                        "pages", Page.empty()));

        return "user/students.html";
    }

    @PostMapping("/addClassSection")
    public String addClassSection(
            @RequestParam LocalDate classDate,
            @RequestParam String classTitle,
            @RequestParam Long subjectId,
            @RequestParam Map<String, String> attendance,
            Principal principal,
            Model model) throws Exception {

        String teacherUserName = principal.getName();

        if (!teacherService.confirmTeachesSubject(teacherUserName, subjectId))
            throw new Exception(
                    "Teacher claimed that he/she teaches this subject but I wanted to be sure and looked it up, the Teacher doesn't teach this thing, idk what you are planning to do, probably some nasty hacker or something, f*ck off! don't you have any better things to do, leave my endpoints alone!");

        try {
            teacherService.assignClassSection(
                    classDate,
                    classTitle,
                    subjectId,
                    teacherUserName,
                    attendance);
            return "redirect:/students/?classDate=" + classDate + "&success=Class%20Section%20Successfully%20Added";
        } catch (ClassSectionAlreadyExistsException e) {
            model.addAttribute("error", e.getExceptionMessage());
            return "redirect:/students/?error=" + e.getExceptionMessage();
        }
    }

    @GetMapping("/getReport")
    public ResponseEntity<?> getReport(
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(name = "classDate", required = false) LocalDate classdate,
            @RequestParam(name = "subject", required = false, defaultValue = "0") int subject,
            Principal principal) {

        String userName = principal.getName();
        List<ClassSectionDTO> classSections = classSectionService
                .getClassSectionsByTeacherAndDate(
                        userName,
                        Optional.ofNullable(classdate).orElseGet(() -> LocalDate.now()));

        return ResponseEntity.ok(classSections.get(subject).getEnrollments());
    }

    @PreAuthorize("hasRole('STUDENT') and !hasRole('TEACHER')")
    @PostMapping("/recordTransaction")
    public String recordTransaction(
            @ModelAttribute @Valid TransactionCreateDTO transactionCreateDTO,
            Principal principal,
            Model model) {

        UserIdProjection studentProjection = userService.getUserProjectionByUserName(principal.getName(), UserIdProjection.class);
        try {
            transactionService.recordTransaction(studentProjection.getId(), transactionCreateDTO);
            //model.addAttribute("success", "Transaction Recorded");
            return "redirect:/user/dashboard?success=Transaction Recorded";
        } catch (RuntimeException e) {
            //model.addAttribute("error", e.getLocalizedMessage());
            return "redirect:/user/dashboard?error=" + e.getLocalizedMessage();
        }
    }

}
