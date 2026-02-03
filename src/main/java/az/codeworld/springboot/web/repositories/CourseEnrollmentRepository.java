package az.codeworld.springboot.web.repositories;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import az.codeworld.springboot.admin.entities.Student;
import az.codeworld.springboot.admin.entities.Transaction;
import az.codeworld.springboot.web.entities.CourseEnrollment;

@Repository
public interface CourseEnrollmentRepository extends JpaRepository<CourseEnrollment, Long> {
    List<CourseEnrollment> findByCourseOffering_Teacher_IdAndCourseOffering_Subject_Id(Long teacherId, Long subjectId);
    boolean existsByStudent_idAndCourseOffering_id(Long studentId, Long courseOfferingId);
    List<CourseEnrollment> findByCourseOffering_Id(Long offeringId);

    @Query("""
        SELECT ce FROM CourseEnrollment ce 
        JOIN ce.courseOffering c
        WHERE c.id = :id
    """)
    Page<CourseEnrollment> findByCourseOffering_Id(Long id, Pageable pageable);
}
