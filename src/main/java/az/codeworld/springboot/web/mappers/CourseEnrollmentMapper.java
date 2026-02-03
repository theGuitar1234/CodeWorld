package az.codeworld.springboot.web.mappers;

import org.springframework.stereotype.Component;

import az.codeworld.springboot.web.dtos.CourseEnrollmentDTO;
import az.codeworld.springboot.web.dtos.EnrollmentDTO;
import az.codeworld.springboot.web.entities.CourseEnrollment;
import az.codeworld.springboot.web.entities.Enrollment;

@Component
public class CourseEnrollmentMapper {

    public static CourseEnrollmentDTO toCourseEnrollmentDTO (
        CourseEnrollment courseEnrollment
    ) {
        return CourseEnrollmentDTO
            .builder()
            .email(courseEnrollment.getStudent().getEmail())
            .affiliatedSince(courseEnrollment.getStudent().getAffiliationDate().toString())
            .firstName(courseEnrollment.getStudent().getFirstName())
            .lastName(courseEnrollment.getStudent().getLastName())
            .id(courseEnrollment.getStudent().getId())
            .build();
    }
}
