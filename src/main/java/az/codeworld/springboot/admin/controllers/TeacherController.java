package az.codeworld.springboot.admin.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.Principal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import az.codeworld.springboot.admin.dtos.UserDTO;
import az.codeworld.springboot.admin.services.TeacherService;
import az.codeworld.springboot.admin.services.UserService;
import az.codeworld.springboot.exceptions.ClassSectionAlreadyExistsException;
import az.codeworld.springboot.utilities.constants.dtotype;
import az.codeworld.springboot.utilities.services.contactservices.ContactService;
import az.codeworld.springboot.web.services.ClassSectionService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequestMapping("/teachers")
public class TeacherController {

    private final UserService userService;
    private final TeacherService teacherService;
    private final ContactService contactService;
    private final ClassSectionService classSectionService;

    public TeacherController(
        UserService userService,
        TeacherService teacherService,
        ContactService contactService,
        ClassSectionService classSectionService
    ) {
        this.userService = userService;
        this.teacherService = teacherService;
        this.contactService = contactService;
        this.classSectionService = classSectionService;
    }

    @PostMapping("/notifyAdmin")
    @PreAuthorize("hasRole('TEACHER')")
    public String notifyAdmin(Principal principal, Model model) {
        try {
            contactService.sendNotifyAdminEmail(principal.getName());
            String succ = "Email sent!";
            model.addAttribute("success", succ);
            return "admin/fragments/success/success.html :: success";
        } catch (Exception e) {
            String err = "Failed to send the email";
            model.addAttribute("error", err);
            return "admin/fragments/error/error.html :: error";
        }
    }

    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/editClassSection")
    public String editClassSection(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate classDate,
            @RequestParam Long classSectionId,
            @RequestParam Long subjectId,
            @RequestParam Map<String, String> attendance,
            Principal principal,
            Model model) throws Exception {

        String teacherUserName = principal.getName();

        if (!teacherService.confirmTeachesSubject(teacherUserName, subjectId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed.");
        }

        Map<Long, Boolean> cleanAttendance = new HashMap<>();

        for (var entry : attendance.entrySet()) {
            String key = entry.getKey();
            String val = entry.getValue();

            if (key != null && key.startsWith("attendance[") && key.endsWith("]")) {
                String idPart = key.substring("attendance[".length(), key.length() - 1);
                try {
                    Long studentId = Long.valueOf(idPart);
                    cleanAttendance.put(studentId, Boolean.parseBoolean(val));
                } catch (NumberFormatException ignored) {}
            }
        }

        try {
            classSectionService.updateAttendance(
                    classSectionId,
                    cleanAttendance);

            return "redirect:/students/?success=Attendance%20updated&classDate=" + classDate;
        } catch (RuntimeException e) {
            return "redirect:/students/?error=" + e.getMessage();
        }
    }

}
