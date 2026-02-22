package az.codeworld.springboot.exceptions.exceptionhandlers;

import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class GlobalExceptionHandler {

    // @ExceptionHandler(MissingServletRequestParameterException.class) 
    // public String handleGlobalMissingServletRequestParameterException(MissingServletRequestParameterException e, Model model, HttpServletResponse response) throws IOException {
    //     model.addAttribute("error", e.getStackTrace());
    //     return "error/InvalidRequestToken";
    // }

    // @ExceptionHandler(InvalidRequestTokenException.class)
    // public String handleInvalidRequestTokenException(InvalidRequestTokenException e, Model model) {
    //     model.addAttribute("error", e.getStackTrace());
    //     return "error/InvalidRequestToken";
    // }
}
