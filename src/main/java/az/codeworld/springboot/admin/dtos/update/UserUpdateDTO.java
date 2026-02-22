package az.codeworld.springboot.admin.dtos.update;

import org.springframework.stereotype.Component;

import az.codeworld.springboot.aop.validations.EmailValidation;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@Component
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO {

    @Pattern(regexp = "^[A-Z]{1}[a-z]{1,14}$")
    private String firstName;

    @Pattern(regexp = "^[A-Z]{1}[a-z]{1,20}$")
    private String lastName;

    @EmailValidation
    private String email;

    private String birthDate;
    
    private String street;
    
    private String city;
    
    private String region;
    
    private Integer postalCode;
    
    private String country;
    
    private String language;
    
    private String timeZone;

    @Pattern(regexp = "^\\+\\d{3}\\d{2}\\d{3}\\d{2}\\d{2}$")
    private String phoneNumber;

    @Min(0)
    @Max(150)
    private Integer age; 
}
