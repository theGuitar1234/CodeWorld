package az.codeworld.springboot.admin.controllers;

import java.security.Principal;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import az.codeworld.springboot.admin.dtos.UserDTO;
import az.codeworld.springboot.admin.services.UserService;
import az.codeworld.springboot.utilities.constants.dtotype;

import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequestMapping("/teachers")
public class TeacherController {

    private final UserService userService;

    public TeacherController(
        UserService userService
    ) {
        this.userService = userService;
    }

    // @GetMapping({"", "/"})
    // public String teachers(Principal principal, Model model) {
    //     UserDTO userDTO = (UserDTO) userService.getUserByUsername(principal.getName(), dtotype.FULL);
    //     model.addAllAttributes(
    //         Map.of(
    //             "user", userDTO
    //         )
    //     );
    //     return "user/teachers";
    // }
    
}
