package az.codeworld.springboot.exceptions.globalexceptionhandlers;

import java.io.IOException;

import org.springframework.ui.Model;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MissingServletRequestParameterException.class) 
    public void handleGlobalMissingServletRequestParameterException(MissingServletRequestParameterException e, Model model, HttpServletResponse response) throws IOException {
        response.getWriter().write("Token Invalid or missing");
    }
}
