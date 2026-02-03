package az.codeworld.springboot.web.mappers;

import org.springframework.stereotype.Component;
import az.codeworld.springboot.web.dtos.SubjectDTO;
import az.codeworld.springboot.web.entities.Subject;

@Component
public class SubjectMapper {

    public static SubjectDTO toSubjectDTO(
        Subject subject
    ) {
        return SubjectDTO
            .builder()
            .id(subject.getId())
            .subjectTitle(subject.getSubjectTitle())
            .subjectDescription(subject.getSubjectBody())
            .build();
    }
}
