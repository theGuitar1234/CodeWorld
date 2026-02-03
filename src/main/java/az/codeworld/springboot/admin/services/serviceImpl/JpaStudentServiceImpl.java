package az.codeworld.springboot.admin.services.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import az.codeworld.springboot.admin.dtos.StudentDTO;
import az.codeworld.springboot.admin.dtos.TeacherDTO;
import az.codeworld.springboot.admin.dtos.transactions.TransactionDTO;
import az.codeworld.springboot.admin.entities.Teacher;
import az.codeworld.springboot.admin.mappers.StudentMapper;
import az.codeworld.springboot.admin.mappers.TeacherMapper;
import az.codeworld.springboot.admin.mappers.TransactionMapper;
import az.codeworld.springboot.admin.repositories.StudentRepository;
import az.codeworld.springboot.admin.repositories.TeacherRepository;
import az.codeworld.springboot.admin.services.StudentService;
import az.codeworld.springboot.admin.services.TeacherService;
import az.codeworld.springboot.utilities.constants.roles;
import az.codeworld.springboot.web.entities.TeachingAssignment;

@Service
public class JpaStudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    public JpaStudentServiceImpl(
        StudentRepository studentRepository
    ) {
        this.studentRepository = studentRepository;
    }

    @Override
    public Page<StudentDTO> getPaginatedStudents(int pageIndex, int pageSize, String sortBy,
            Direction direction) {
        return studentRepository
                .findAll(PageRequest.of(pageIndex, pageSize))
                .map(s -> StudentMapper.toStudentDTO(s));
    }

    @Override
    public long countAllStudents() {
        return studentRepository.count();
    }
    
}
