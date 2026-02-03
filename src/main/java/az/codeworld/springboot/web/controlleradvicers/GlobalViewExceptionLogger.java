package az.codeworld.springboot.web.controlleradvicers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.thymeleaf.exceptions.TemplateProcessingException;

@ControllerAdvice
public class GlobalViewExceptionLogger {
    private static final Logger log = LoggerFactory.getLogger(GlobalViewExceptionLogger.class);

    @ExceptionHandler(TemplateProcessingException.class)
    public String thymeleafTemplateCrash(TemplateProcessingException ex, Model model) {
        log.error("Thymeleaf template crashed: {}", ex.getMessage(), ex);

        model.addAttribute("errorMessage", ex.getMessage());
        return "error/template-error";
    }

    @ExceptionHandler(Exception.class)
    public String anyCrash(Exception ex, Model model) {
        log.error("Unhandled crash during request: {}", ex.getMessage(), ex);
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/general-error";
    }
}
