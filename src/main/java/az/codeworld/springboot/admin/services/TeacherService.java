package az.codeworld.springboot.admin.services;

import az.codeworld.springboot.admin.dtos.StudentDTO;
import az.codeworld.springboot.admin.dtos.TeacherDTO;
import az.codeworld.springboot.admin.entities.Student;
import az.codeworld.springboot.exceptions.CourseOfferingAlreadyExistsException;
import az.codeworld.springboot.web.entities.TeachingAssignment;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;

public interface TeacherService {

    TeacherDTO getTeacherByUserName(String userName);
    TeacherDTO getTeacherById(Long teacherId);
;
    List<TeachingAssignment> getTeacherTeachingAssignments(String userName);
    List<TeacherDTO> getAllTeachers();

    Page<TeacherDTO> getPaginatedTeachers(int pageIndex, int pageSize, String sortBy, Direction direction);

    boolean confirmTeachesSubject(String teacherUserName, Long subjectId);

    void assignClassSection(LocalDate classDate, String classTitle, Long subjectId, String teacherUserName, Map<String, String> attendance) throws Exception;

    List<StudentDTO> getStudentsInSubject(Long teacherId, Long subjectId);

    void addCourseOffering(Long teacherId, Long subjectId, List<Long> studentIds) throws CourseOfferingAlreadyExistsException;

    long countAllTeachers();
}
