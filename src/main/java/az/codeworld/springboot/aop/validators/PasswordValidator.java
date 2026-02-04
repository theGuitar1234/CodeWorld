package az.codeworld.springboot.aop.validators;

import az.codeworld.springboot.aop.validations.PasswordValidation;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<PasswordValidation, String> {

    private static final String PASSWORD_PATTERN_STRING = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

    @Override
    public boolean isValid(String passwordString, ConstraintValidatorContext context) {
        return passwordString != null && passwordString.matches(PASSWORD_PATTERN_STRING);
    }
    
}

