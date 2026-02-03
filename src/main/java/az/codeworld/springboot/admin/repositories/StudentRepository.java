package az.codeworld.springboot.admin.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import az.codeworld.springboot.admin.entities.Student;
import java.time.Instant;


public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findBySubjectEnrollments_Subject_id(Long subjectId);

    List<Student> findByNextDateBefore(Instant cutoff);
    List<Student> findByNextDateAfter(Instant cutoff);
}
