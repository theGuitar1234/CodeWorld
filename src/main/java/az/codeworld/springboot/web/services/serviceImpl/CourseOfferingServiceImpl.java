package az.codeworld.springboot.web.services.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

import az.codeworld.springboot.web.dtos.CourseEnrollmentDTO;
import az.codeworld.springboot.web.dtos.CourseOfferingDTO;
import az.codeworld.springboot.web.dtos.SubjectDTO;
import az.codeworld.springboot.web.entities.CourseOffering;
import az.codeworld.springboot.web.mappers.CourseEnrollmentMapper;
import az.codeworld.springboot.web.mappers.CourseOfferingMapper;
import az.codeworld.springboot.web.mappers.SubjectMapper;
import az.codeworld.springboot.web.repositories.CourseEnrollmentRepository;
import az.codeworld.springboot.web.repositories.CourseOfferingRepository;
import az.codeworld.springboot.web.services.CourseOfferingService;
import jakarta.transaction.Transactional;

@Service
public class CourseOfferingServiceImpl implements CourseOfferingService {

    private final CourseOfferingRepository courseOfferingRepository;
    private final CourseEnrollmentRepository courseEnrollmentRepository;

    public CourseOfferingServiceImpl(
        CourseOfferingRepository courseOfferingRepository,
        CourseEnrollmentRepository courseEnrollmentRepository
    ) {
        this.courseOfferingRepository = courseOfferingRepository;
        this.courseEnrollmentRepository = courseEnrollmentRepository;
    }

    @Override
    public CourseOfferingDTO getCourseOfferingById(Long courseOfferingId) {
        Optional<CourseOffering> courseOfferingOptional = courseOfferingRepository.findById(courseOfferingId);
        CourseOffering courseOffering = courseOfferingOptional.orElseThrow(() -> new RuntimeException("Course Offering not Found by ID"));
        return CourseOfferingMapper.toCourseOfferingDTO(courseOffering);
    }

    @Override
    public Page<CourseEnrollmentDTO> getPaginatedCourseEnrollments(Long courseOfferingId, int pageIndex, int pageSize, String sortBy,
            Direction direction) {
        return courseEnrollmentRepository
                .findByCourseOffering_Id(courseOfferingId, PageRequest.of(pageIndex, pageSize).withSort(direction, sortBy))
                .map(ce -> CourseEnrollmentMapper.toCourseEnrollmentDTO(ce));
    }

    @Override
    @Transactional
    @Modifying
    public void deleteCourseOfferingById(Long courseOfferingId) {
        courseOfferingRepository.deleteById(courseOfferingId);
    }
    
}
