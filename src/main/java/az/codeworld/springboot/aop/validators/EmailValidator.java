package az.codeworld.springboot.aop.validators;

import az.codeworld.springboot.aop.validations.EmailValidation;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EmailValidator implements ConstraintValidator<EmailValidation, String> {

    private static final String EMAIL_PATTERN_STRING = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9._%+-]+\\.[A-Za-z]{2,6}$";

    @Override
    public boolean isValid(String emailString, ConstraintValidatorContext context) {
        return emailString == null || emailString.matches(EMAIL_PATTERN_STRING);
    }
    
}
