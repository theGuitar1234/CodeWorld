package az.codeworld.springboot.web.mappers;

import org.springframework.stereotype.Component;

import az.codeworld.springboot.web.dtos.EnrollmentDTO;
import az.codeworld.springboot.web.entities.Enrollment;

@Component
public class EnrollmentMapper {

    public static EnrollmentDTO toEnrollmentDTO (
        Enrollment enrollment
    ) {
        return EnrollmentDTO
            .builder()
            .studentId(enrollment.getStudent().getId())
            .studentFirstName(enrollment.getStudent().getFirstName())
            .studentLastName(enrollment.getStudent().getLastName())
            .isPresent(enrollment.isPresent())
            .build();
    }
}
