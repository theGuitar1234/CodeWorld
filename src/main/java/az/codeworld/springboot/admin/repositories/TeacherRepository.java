package az.codeworld.springboot.admin.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import az.codeworld.springboot.admin.entities.Student;
import az.codeworld.springboot.admin.entities.Teacher;
import java.util.List;
import java.util.Optional;


@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Optional<Teacher> findByUserName(String userName);
    boolean existsByIdAndCourseOfferingsSubjectId(Long teacherId, Long subjectId);
}
