package az.codeworld.springboot.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping("/")
    public String index() {
        return "index";
    }
    
    @GetMapping("/transactions")
    public String transactions() {
        return "buss/transactions";
    }

    @GetMapping("/404")
    public String notFound() {
        return "error/404";
    }
    
}
