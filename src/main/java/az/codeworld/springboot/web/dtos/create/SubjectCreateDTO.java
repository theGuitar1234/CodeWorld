package az.codeworld.springboot.web.dtos.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubjectCreateDTO {

    @NotBlank
    @NotEmpty
    @NotNull
    private String subjectTitle;
    
    @NotBlank
    @NotEmpty
    @NotNull
    private String subjectBody;  
}
