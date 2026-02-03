package az.codeworld.springboot.admin.mappers;

import java.time.LocalDate;
import java.util.Locale;

import org.springframework.stereotype.Component;

import az.codeworld.springboot.admin.dtos.StudentDTO;
import az.codeworld.springboot.admin.dtos.TeacherDTO;
import az.codeworld.springboot.admin.dtos.UserDTO;
import az.codeworld.springboot.admin.dtos.create.UserCreateDTO;
import az.codeworld.springboot.admin.dtos.dashboard.UserDashboardDTO;
import az.codeworld.springboot.admin.dtos.transactions.UserTransactionDTO;
import az.codeworld.springboot.admin.entities.Student;
import az.codeworld.springboot.admin.entities.Teacher;
import az.codeworld.springboot.admin.entities.User;
import az.codeworld.springboot.security.dtos.LoginAuditDTO;
import az.codeworld.springboot.utilities.constants.dtotype;
import az.codeworld.springboot.utilities.constants.presence;
import az.codeworld.springboot.utilities.constants.roles;
import az.codeworld.springboot.web.mappers.SubjectMapper;

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
