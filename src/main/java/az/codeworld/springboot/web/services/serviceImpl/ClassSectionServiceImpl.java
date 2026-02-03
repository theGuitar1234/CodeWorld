package az.codeworld.springboot.web.services.serviceImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import az.codeworld.springboot.admin.dtos.transactions.TransactionDTO;
import az.codeworld.springboot.admin.mappers.TransactionMapper;
import az.codeworld.springboot.utilities.constants.roles;
import az.codeworld.springboot.web.dtos.ClassSectionDTO;
import az.codeworld.springboot.web.entities.ClassSection;
import az.codeworld.springboot.web.entities.Enrollment;
import az.codeworld.springboot.web.mappers.ClassSectionMapper;
import az.codeworld.springboot.web.repositories.ClassSectionRepository;
import az.codeworld.springboot.web.services.ClassSectionService;
import jakarta.transaction.Transactional;

@Service
public class ClassSectionServiceImpl implements ClassSectionService {

    private final ClassSectionRepository classSectionRepository;

    public ClassSectionServiceImpl(
            ClassSectionRepository classSectionRepository) {
        this.classSectionRepository = classSectionRepository;
    }

    @Override
    public void defaultMethod() {
    }

    @Override
    public Page<ClassSectionDTO> getPaginatedClassSections(
            int pageIndex,
            int pageSize,
            String sortBy,
            Direction direction) {
        return classSectionRepository
                .findAll(
                        PageRequest.of(pageIndex, pageSize).withSort(direction, sortBy))
                .map(c -> {
                    return ClassSectionMapper.toClassSectionDTO(c);
                });
    }

    @Override
    public List<ClassSectionDTO> getClassSectionsByTeacherAndDate(String userName, LocalDate classDate) {
        return classSectionRepository.findByTeacherAndClassDate(userName, classDate)
                .stream()
                .map(s -> ClassSectionMapper.toClassSectionDTO(s))
                .toList();
    }

    @Override
    @Transactional
    public void updateAttendance(
            Long classSectionId,
            Map<Long, Boolean> attendance) {
        ClassSection section = classSectionRepository
                .findById(classSectionId)
                .orElseThrow(() -> new RuntimeException("Class section not found."));

        for (Enrollment enrollment : section.getEnrollments()) {
            Long studentId = enrollment.getStudent().getId();
            Boolean isPresent = attendance.get(studentId);
            if (isPresent != null) {
                enrollment.setPresent(isPresent); 
            }
        }

        classSectionRepository.save(section);
    }

}
