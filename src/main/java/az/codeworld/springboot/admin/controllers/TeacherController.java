package az.codeworld.springboot.admin.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequestMapping("/teachers/")
public class TeacherController {

    @GetMapping("/")
    public String teachers() {
        return "user/teachers";
    }
    
}
