package az.codeworld.springboot.aop.validators;

import az.codeworld.springboot.aop.validations.PasswordValidation;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<PasswordValidation, String> {

    //^(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_\-+=\[\]{};:'",.<>/?\\|`~]).{8,}$
    //Disallows spaces : ^(?=.*[a-z])(?=.*[A-Z])(?=.*[\W_])\S{8,}$
    //^(?=.*[a-z])(?=.*[A-Z])(?=.*[\W_]).{8,}$

    private static final String PASSWORD_PATTERN_STRING = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_]).{8,}$";

    @Override
    public boolean isValid(String passwordString, ConstraintValidatorContext context) {
        return passwordString != null && passwordString.matches(PASSWORD_PATTERN_STRING);
    }
    
}

