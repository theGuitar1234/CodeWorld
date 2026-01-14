package az.codeworld.springboot.aop.validators;

import az.codeworld.springboot.aop.validations.UsernameValidation;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UsernameValidator implements ConstraintValidator<UsernameValidation, String> {

    private static final String USER_PATTERN_STRING = "^[STA]-[A-Z0-9]{4}-[A-Z0-9]{4}-\\\\d{1,}$";

    @Override
    public boolean isValid(String usernameString, ConstraintValidatorContext context) {
        return usernameString == null || usernameString.matches(USER_PATTERN_STRING);
    }
    
}
