package az.codeworld.springboot.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import az.codeworld.springboot.web.entities.SubjectEnrollment;

@Repository
public interface SubjectEntrollmentRepository extends JpaRepository<SubjectEnrollment, Long> {
    boolean existsByStudent_idAndSubject_id(Long studentId, Long subjectId);
}
