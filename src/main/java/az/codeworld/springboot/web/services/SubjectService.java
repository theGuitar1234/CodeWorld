package az.codeworld.springboot.web.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import az.codeworld.springboot.admin.dtos.StudentDTO;
import az.codeworld.springboot.exceptions.SubjectAlreadyExistsException;
import az.codeworld.springboot.web.dtos.SubjectDTO;
import az.codeworld.springboot.web.dtos.create.SubjectCreateDTO;
import az.codeworld.springboot.web.entities.Subject;

public interface SubjectService {
    
    void saveSubject(Subject subject);

    List<SubjectDTO> getAllSubjects();   

    Page<SubjectDTO> getPaginatedSubjects(int pageIndex, int pageSize, String sortBy, Direction direction);

    void deleteSubjectById(Long subjectId);

    void createNewSubject(SubjectCreateDTO subjectCreateDTO) throws SubjectAlreadyExistsException;
}
