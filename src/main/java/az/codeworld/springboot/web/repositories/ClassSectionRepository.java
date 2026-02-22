package az.codeworld.springboot.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import az.codeworld.springboot.web.entities.ClassSection;

import java.util.List;
import java.time.LocalDate;

@Repository
public interface ClassSectionRepository extends JpaRepository<ClassSection, Long> {
    
    @Query(""" 
        SELECT DISTINCT cs FROM ClassSection cs
        JOIN FETCH cs.teachingAssignments ta
        JOIN FETCH ta.teacher t 
        WHERE t.userName = :userName
        AND cs.classDate = :classDate
    """)
    List<ClassSection> findByTeacherAndClassDate(
        @Param("userName") String userName,
        @Param("classDate") LocalDate classDate
    ); 

    boolean existsBySubject_IdAndTeachingAssignments_Teacher_IdAndClassDate(Long subjectId, Long teacherId, LocalDate classDate);
}
