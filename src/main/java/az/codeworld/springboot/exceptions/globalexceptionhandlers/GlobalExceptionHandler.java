package az.codeworld.springboot.exceptions.globalexceptionhandlers;

import java.io.IOException;

import org.springframework.ui.Model;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import az.codeworld.springboot.exceptions.InvalidRequestTokenException;
import jakarta.servlet.http.HttpServletResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MissingServletRequestParameterException.class) 
    public String handleGlobalMissingServletRequestParameterException(MissingServletRequestParameterException e, Model model, HttpServletResponse response) throws IOException {
        model.addAttribute("error", e.getStackTrace());
        return "error/InvalidRequestToken";
    }

    @ExceptionHandler(InvalidRequestTokenException.class)
    public String handleInvalidRequestTokenException(InvalidRequestTokenException e, Model model) {
        model.addAttribute("error", e.getStackTrace());
        return "error/InvalidRequestToken";
    }
}
