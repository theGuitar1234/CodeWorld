package az.codeworld.springboot.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import az.codeworld.springboot.web.entities.Subject;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long>  {
    boolean existsBySubjectTitle(String subjectTitle);
}
