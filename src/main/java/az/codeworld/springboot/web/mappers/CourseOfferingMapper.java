package az.codeworld.springboot.web.mappers;

import org.springframework.stereotype.Component;

import az.codeworld.springboot.web.dtos.CourseOfferingDTO;
import az.codeworld.springboot.web.dtos.SubjectDTO;
import az.codeworld.springboot.web.entities.CourseOffering;
import az.codeworld.springboot.web.entities.Subject;

@Component
public class CourseOfferingMapper {

    public static CourseOfferingDTO toCourseOfferingDTO(
        CourseOffering courseOffering
    ) {
        return CourseOfferingDTO
            .builder()
            .id(courseOffering.getId())
            .subjectTitle(courseOffering.getSubject().getSubjectTitle())
            .totalStudents(courseOffering.getCourseEnrollments().size())
            .build();
    }
}
