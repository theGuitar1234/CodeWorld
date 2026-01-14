package az.codeworld.springboot.security.controllers;

import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/invoices")
public class JWTController {
    @GetMapping("/{id}")
    public Map<String, Object> getInvoice(@PathVariable String id, Authentication auth) {
        return Map.of("id", id, "principal", auth.getName());
    }

    @GetMapping("/getToken")
    public Map<String, Object> getToken(Authentication authentication) {
        return Map.of("class", authentication.getClass().getSimpleName(), "name", authentication.getName(), "authorities", authentication.getAuthorities());
    }
}