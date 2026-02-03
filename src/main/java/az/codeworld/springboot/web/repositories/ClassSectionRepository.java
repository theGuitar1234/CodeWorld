package az.codeworld.springboot.web.repositories;

import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import az.codeworld.springboot.web.entities.ClassSection;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;


@Repository
public interface ClassSectionRepository extends JpaRepository<ClassSection, Long> {
    
    @Query(""" 
        SELECT DISTINCT cs FROM ClassSection cs
        JOIN cs.teachingAssignments ta 
        WHERE ta.teacher.userName = :userName
        AND cs.classDate = :classDate
    """)
    List<ClassSection> findByTeacherAndClassDate(
        @Param("userName") String userName,
        @Param("classDate") LocalDate classDate
    ); 

    boolean existsBySubject_IdAndTeachingAssignments_Teacher_IdAndClassDate(Long subjectId, Long teacherId, LocalDate classDate);
}
