package az.codeworld.springboot.admin.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;

import az.codeworld.springboot.admin.dtos.StudentDTO;
import az.codeworld.springboot.admin.dtos.transactions.TransactionDTO;
import az.codeworld.springboot.utilities.constants.roles;

public interface StudentService {
    Page<StudentDTO> getPaginatedStudents(int pageIndex, int pageSize, String sortBy, Direction direction);
    long countAllStudents();
}
