package az.codeworld.springboot.web.controllers;

import java.util.ArrayList;
import java.util.List;
import java.security.Principal;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import az.codeworld.springboot.admin.dtos.UserDTO;
import az.codeworld.springboot.admin.dtos.dashboard.UserDashboardDTO;
import az.codeworld.springboot.admin.dtos.transactions.UserTransactionDTO;
import az.codeworld.springboot.admin.services.StudentService;
import az.codeworld.springboot.admin.services.TeacherService;
import az.codeworld.springboot.admin.services.TransactionService;
import az.codeworld.springboot.admin.services.UserService;
import az.codeworld.springboot.utilities.constants.dtotype;
import az.codeworld.springboot.utilities.constants.roles;
import az.codeworld.springboot.web.records.NotificationRecord;
import az.codeworld.springboot.web.services.NotificationService;

import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class HomeController {

    private UserService userService;
    private TeacherService teacherService;
    private StudentService studentService;
    private TransactionService transactionService;

    public HomeController(
        UserService userService,
        TeacherService teacherService,
        StudentService studentService,
        TransactionService transactionService
    ) {
        this.userService = userService;
        this.transactionService = transactionService;
        this.teacherService = teacherService;
        this.studentService = studentService;    
    }

    @GetMapping("/")
    public String home(Principal principal, Model model) {

        // UserDashboardDTO userDashboardDTO = new UserDashboardDTO();
     
        // if (principal != null) {
        //     userDashboardDTO = (UserDashboardDTO) userService.getUserByUserName(principal.getName(), dtotype.DASHBOARD);
        // }

        // model.addAllAttributes(
        //     Map.of(
        //         "user", userDashboardDTO
        //     )
        // );

        long totalTeachers = teacherService.countAllTeachers();
        long totalStudents = studentService.countAllStudents();

        model.addAllAttributes(
            Map.of(
                "totalTeachers", totalTeachers,
                "totalStudents", totalStudents
            )
        );

        return "index";
    }
    
    @GetMapping("/404")
    public String notFound() {
        return "error/404";
    }
    
}
