package az.codeworld.springboot.security.controllers;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import az.codeworld.springboot.admin.services.RequestService;
import az.codeworld.springboot.admin.records.RequestRecord;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/restricted")
public class SecurityController {

    private final RequestService requestService;

    public SecurityController(
        RequestService requestService
    ) {
        this.requestService = requestService;
    }

    @GetMapping("/")
    public String restricted(
        HttpServletRequest request,
        Model model
    ) {
        model.addAttribute("error", request.getParameter("error"));
        return "auth/restricted";
    }

    @GetMapping("/authenticate")
    public String authenciate() {
        return "auth/restricted";
    }

    @PostMapping("/request")
    public String request(
        @ModelAttribute RequestRecord requestRecord
    ) {
        try {
            requestService.createNewRequest(requestRecord);
            return "redirect:/restricted/?msg=Request is sent!";
        } catch (DataIntegrityViolationException e) {
            return "redirect:/restricted/?error=" + requestRecord.email() + " has already sent a request!";
        } catch (NullPointerException e) {
            return "redirect:/restricted/?error=Please make sure you filled all the fields";
        }
    }
    
}
