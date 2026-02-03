package az.codeworld.springboot.web.dtos;

import java.util.List;

import az.codeworld.springboot.web.entities.Enrollment;
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
public class ClassSectionDTO {
    private Long classId;
    private String classDate;
    private String classTitle;
    private Long subjectId;
    private List<EnrollmentDTO> enrollments;
}
