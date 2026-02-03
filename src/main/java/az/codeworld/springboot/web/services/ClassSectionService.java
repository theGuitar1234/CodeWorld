package az.codeworld.springboot.web.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;

import az.codeworld.springboot.admin.dtos.transactions.TransactionDTO;
import az.codeworld.springboot.admin.entities.Transaction;
import az.codeworld.springboot.utilities.constants.roles;
import az.codeworld.springboot.web.dtos.ClassSectionDTO;
import az.codeworld.springboot.web.entities.ClassSection;

public interface ClassSectionService {

    void defaultMethod();

    Page<ClassSectionDTO> getPaginatedClassSections(int pageIndex, int pageSize, String sortBy, Direction direction);
    List<ClassSectionDTO> getClassSectionsByTeacherAndDate(String userName, LocalDate classDate);

    void updateAttendance(Long classSectionId, Map<Long, Boolean> attendance);

}
