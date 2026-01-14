package az.codeworld.springboot.aop.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import az.codeworld.springboot.aop.validators.EmailValidator;

@Documented
@Constraint(validatedBy = EmailValidator.class) 
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)                
public @interface EmailValidation {

    String message() default "Invalid email!, proper email validation is as follows : ^[A-Za-z0-9._%+-]+@[A-Za-z0-9._%+-]+\\.[A-Za-z]{2,6}$";     

    Class<?>[] groups() default {};               

    Class<? extends Payload>[] payload() default {};
}
