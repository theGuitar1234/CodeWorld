package az.codeworld.springboot.web.mappers;

import org.springframework.stereotype.Component;

import az.codeworld.springboot.web.dtos.ClassSectionDTO;
import az.codeworld.springboot.web.entities.ClassSection;

@Component
public class ClassSectionMapper {

    public static ClassSectionDTO toClassSectionDTO(
        ClassSection classSection
    ) {
        return ClassSectionDTO
            .builder()
            .classId(classSection.getClassId())
            .subjectId(classSection.getSubject().getId())
            .classTitle(classSection.getSubject().getSubjectTitle())
            .classDate(classSection.getClassDate().toString())
            .enrollments(classSection.getEnrollments()
                .stream()
                .map(e -> EnrollmentMapper.toEnrollmentDTO(e))
                .toList()    
            )
            .build();
    }
}
