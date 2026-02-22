package az.codeworld.springboot.admin.services.serviceImpl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import az.codeworld.springboot.admin.dtos.StudentDTO;
import az.codeworld.springboot.admin.mappers.StudentMapper;
import az.codeworld.springboot.admin.repositories.StudentRepository;
import az.codeworld.springboot.admin.services.StudentService;

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
