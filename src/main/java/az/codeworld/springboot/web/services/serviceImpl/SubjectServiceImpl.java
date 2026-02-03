package az.codeworld.springboot.web.services.serviceImpl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import az.codeworld.springboot.admin.dtos.StudentDTO;
import az.codeworld.springboot.admin.mappers.StudentMapper;
import az.codeworld.springboot.exceptions.SubjectAlreadyExistsException;
import az.codeworld.springboot.web.dtos.SubjectDTO;
import az.codeworld.springboot.web.dtos.create.SubjectCreateDTO;
import az.codeworld.springboot.web.entities.Subject;
import az.codeworld.springboot.web.mappers.SubjectMapper;
import az.codeworld.springboot.web.repositories.SubjectRepository;
import az.codeworld.springboot.web.services.SubjectService;
import jakarta.transaction.Transactional;

@Service
public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository subjectRepository;

    public SubjectServiceImpl(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    @Override
    public void saveSubject(Subject subject) {
        subjectRepository.save(subject);
        subjectRepository.flush();
    }

    @Override
    public List<SubjectDTO> getAllSubjects() {
        return subjectRepository.findAll()
            .stream()
            .map(s -> SubjectMapper.toSubjectDTO(s))
            .toList();
    }

    @Override
    public Page<SubjectDTO> getPaginatedSubjects(int pageIndex, int pageSize, String sortBy,
            Direction direction) {
        return subjectRepository
                .findAll(PageRequest.of(pageIndex, pageSize).withSort(direction, sortBy))
                .map(s -> SubjectMapper.toSubjectDTO(s));
    }

    @Override
    @Transactional
    public void deleteSubjectById(Long subjectId) {
        subjectRepository.deleteById(subjectId);
        //subjectRepository.flush();
    }

    @Override
    public void createNewSubject(SubjectCreateDTO subjectCreateDTO) throws SubjectAlreadyExistsException {

        if (subjectRepository.existsBySubjectTitle(subjectCreateDTO.getSubjectTitle())) throw new SubjectAlreadyExistsException();

        Subject subject = new Subject();
        subject.setSubjectTitle(subjectCreateDTO.getSubjectTitle());
        subject.setSubjectBody(subjectCreateDTO.getSubjectBody());
        saveSubject(subject);
    }
    
}
