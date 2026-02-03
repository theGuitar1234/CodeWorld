package az.codeworld.springboot.web.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import az.codeworld.springboot.admin.entities.Teacher;
import az.codeworld.springboot.web.entities.CourseOffering;
import az.codeworld.springboot.web.entities.Subject;

@Repository
public interface CourseOfferingRepository extends JpaRepository<CourseOffering, Long> {
    CourseOffering findBySubject_Id(Long subjectId);
    CourseOffering findByTeacher_IdAndSubject_Id(Long teacherId, Long subjectId);

    boolean existsByTeacherAndSubject(Teacher teacher, Subject subject);

    List<CourseOffering> findByTeacher_Id(Long teacherId);
}
