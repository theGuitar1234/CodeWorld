package az.codeworld.springboot.admin.controllers;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import az.codeworld.springboot.admin.services.UserService;

import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequestMapping("/teachers")
public class TeacherController {

    @GetMapping("/")
    public String teachers() {
        return "user/teachers";
    }
    
}
