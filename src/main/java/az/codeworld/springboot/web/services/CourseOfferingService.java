package az.codeworld.springboot.web.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;

import az.codeworld.springboot.web.dtos.CourseEnrollmentDTO;
import az.codeworld.springboot.web.dtos.CourseOfferingDTO;
import az.codeworld.springboot.web.dtos.SubjectDTO;

public interface CourseOfferingService {
    CourseOfferingDTO getCourseOfferingById(Long courseOfferingId);
    void deleteCourseOfferingById(Long courseOfferingId);
    Page<CourseEnrollmentDTO> getPaginatedCourseEnrollments(Long courseOfferingId, int pageIndex, int pageSize,
            String sortBy, Direction direction);
}
