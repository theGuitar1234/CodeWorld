package az.codeworld.springboot.web.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentDTO {
    private Long studentId;
    private String studentFirstName;
    private String studentLastName;
    private boolean isPresent;
}
