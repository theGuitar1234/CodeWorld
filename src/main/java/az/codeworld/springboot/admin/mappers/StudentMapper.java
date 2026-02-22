package az.codeworld.springboot.admin.mappers;

import org.springframework.stereotype.Component;

import az.codeworld.springboot.admin.dtos.StudentDTO;
import az.codeworld.springboot.admin.entities.Student;
import az.codeworld.springboot.utilities.constants.roles;

@Component
public class StudentMapper {

    public static StudentDTO toStudentDTO(
        Student student
    ) {
        return StudentDTO
            .builder()
            .firstName(student.getFirstName())
            .lastName(student.getLastName())
            .email(student.getEmail())
            .id(student.getId())
            .affiliatedSince(student.getAffiliationDate().toString())
            .isBanned(student.isBanned())
            .role(roles.STUDENT)
            .build();
    }
}
