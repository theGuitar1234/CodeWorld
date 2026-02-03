package az.codeworld.springboot.admin.mappers;

import org.springframework.stereotype.Component;

import az.codeworld.springboot.admin.dtos.TeacherDTO;
import az.codeworld.springboot.admin.entities.Teacher;
import az.codeworld.springboot.utilities.constants.roles;

@Component
public class TeacherMapper {

    public static TeacherDTO toTeacherDTO(
        Teacher teacher
    ) {
        return TeacherDTO
            .builder()
            .id(teacher.getId())
            .firstName(teacher.getFirstName())
            .lastName(teacher.getLastName())
            .email(teacher.getEmail())
            .isBanned(teacher.isBanned())
            .affiliatedSince(teacher.getAffiliationDate().toString())
            .role(roles.TEACHER)
            .amount(teacher.getPayment().getAmount())
            .build();
    }
}
