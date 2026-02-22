package az.codeworld.springboot.admin.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import az.codeworld.springboot.admin.entities.Student;
import java.time.Instant;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    @Query(
    """
        SELECT DISTINCT s FROM Student s
        JOIN FETCH s.subjectEnrollments se
        JOIN FETCH se.subject sub
        WHERE sub.id = :subjectId        
    """
    )
    List<Student> findBySubjectEnrollments_Subject_id(@Param("subjectId") Long subjectId);
    List<Student> findByNextDateBefore(Instant cutoff);
    List<Student> findByNextDateAfter(Instant cutoff);
}
