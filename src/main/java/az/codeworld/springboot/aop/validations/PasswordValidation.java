package az.codeworld.springboot.aop.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import az.codeworld.springboot.aop.validators.PasswordValidator;

@Documented
@Constraint(validatedBy = PasswordValidator.class) 
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)                
public @interface PasswordValidation {

    String message() default "Invalid password! According to proper password validation, a password must include at least: 1 lowercase letter, 1 uppercase letter and 1 special character";     

    Class<?>[] groups() default {};               

    Class<? extends Payload>[] payload() default {};
}
