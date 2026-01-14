package az.codeworld.springboot.aop.validators;

import az.codeworld.springboot.aop.validations.PasswordValidation;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<PasswordValidation, String> {

    private static final String PASSWORD_PATTERN_STRING = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[\\W_]).+$";

    @Override
    public boolean isValid(String passwordString, ConstraintValidatorContext context) {
        return passwordString != null && passwordString.matches(PASSWORD_PATTERN_STRING);
    }
    
}

