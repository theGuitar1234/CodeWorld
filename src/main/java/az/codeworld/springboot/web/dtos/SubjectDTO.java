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
public class SubjectDTO {
    private Long id;
    private String subjectTitle;
    private String subjectDescription;
}
