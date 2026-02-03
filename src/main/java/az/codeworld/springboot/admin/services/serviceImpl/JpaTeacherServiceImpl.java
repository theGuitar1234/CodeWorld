package az.codeworld.springboot.admin.services.serviceImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import az.codeworld.springboot.admin.dtos.StudentDTO;
import az.codeworld.springboot.admin.dtos.TeacherDTO;
import az.codeworld.springboot.admin.entities.Student;
import az.codeworld.springboot.admin.entities.Teacher;
import az.codeworld.springboot.admin.mappers.StudentMapper;
import az.codeworld.springboot.admin.mappers.TeacherMapper;
import az.codeworld.springboot.admin.repositories.StudentRepository;
import az.codeworld.springboot.admin.repositories.TeacherRepository;
import az.codeworld.springboot.admin.services.TeacherService;
import az.codeworld.springboot.exceptions.ClassSectionAlreadyExistsException;
import az.codeworld.springboot.exceptions.CourseOfferingAlreadyExistsException;
import az.codeworld.springboot.web.entities.ClassSection;
import az.codeworld.springboot.web.entities.CourseEnrollment;
import az.codeworld.springboot.web.entities.CourseOffering;
import az.codeworld.springboot.web.entities.Enrollment;
import az.codeworld.springboot.web.entities.Subject;
import az.codeworld.springboot.web.entities.SubjectEnrollment;
import az.codeworld.springboot.web.entities.TeachingAssignment;
import az.codeworld.springboot.web.repositories.*;
import az.codeworld.springboot.web.services.GenericWebService;
import az.codeworld.springboot.web.services.SubjectService;
import jakarta.transaction.Transactional;

@Service
public class JpaTeacherServiceImpl implements TeacherService {

    private final SubjectEntrollmentRepository subjectEntrollmentRepository;

    private final CourseOfferingRepository courseOfferingRepository;

    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final GenericWebService genericWebService;
    private final CourseEnrollmentRepository courseEnrollmentRepository;
    private final ClassSectionRepository classSectionRepository;
    private final SubjectRepository subjectRepository;

    public JpaTeacherServiceImpl(
        TeacherRepository teacherRepository,
        StudentRepository studentRepository,
        GenericWebService genericWebService,
        CourseEnrollmentRepository courseEnrollmentRepository,
        ClassSectionRepository classSectionRepository,
        SubjectRepository subjectRepository, 
        CourseOfferingRepository courseOfferingRepository, 
        SubjectEntrollmentRepository subjectEntrollmentRepository
    ) {
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
        this.genericWebService = genericWebService;
        this.courseEnrollmentRepository = courseEnrollmentRepository;
        this.classSectionRepository = classSectionRepository;
        this.subjectRepository = subjectRepository;
        this.courseOfferingRepository = courseOfferingRepository;
        this.subjectEntrollmentRepository = subjectEntrollmentRepository;
    }

    @Override
    public TeacherDTO getTeacherByUserName(String userName) {
        Optional<Teacher> teacherOptional = teacherRepository.findByUserName(userName);
        Teacher teacher = teacherOptional.orElseThrow(() -> new RuntimeException("Teacher not Found by username"));

        return TeacherMapper.toTeacherDTO(teacher);
    }

    @Override
    public TeacherDTO getTeacherById(Long teacherId) {
        Optional<Teacher> teacherOptional = teacherRepository.findById(teacherId);
        Teacher teacher = teacherOptional.orElseThrow(() -> new RuntimeException("Teacher not Found by ID"));

        return TeacherMapper.toTeacherDTO(teacher);
    }

    @Override
    public List<TeachingAssignment> getTeacherTeachingAssignments(String userName) { return null; }

    @Override
    public List<TeacherDTO> getAllTeachers() {
        return teacherRepository.findAll()
            .stream()
            .map(t -> TeacherMapper.toTeacherDTO(t))
            .toList();
    }

    @Override
    public Page<TeacherDTO> getPaginatedTeachers(int pageIndex, int pageSize, String sortBy,
            Direction direction) {
        return teacherRepository
                .findAll(PageRequest.of(pageIndex, pageSize).withSort(direction, sortBy))
                .map(s -> TeacherMapper.toTeacherDTO(s));
    }

    @Override
    public boolean confirmTeachesSubject(String teacherUserName, Long subjectId) {
        return teacherRepository.existsByIdAndCourseOfferingsSubjectId(getTeacherByUserName(teacherUserName).getId(), subjectId);
    }

    @Override
    @Transactional
    public void assignClassSection(LocalDate classDate, String classTitle, Long subjectId, String teacherUserName,
            Map<String, String> attendance) throws Exception {

        Optional<Teacher> teacherOptional = teacherRepository.findByUserName(teacherUserName);
        Teacher teacher = teacherOptional.orElseThrow(() -> new RuntimeException("Teacher Not Found By UserName"));
        
        if (classSectionRepository.existsBySubject_IdAndTeachingAssignments_Teacher_IdAndClassDate(subjectId, teacher.getId(), classDate)) throw new ClassSectionAlreadyExistsException();
        
        ClassSection classSection = new ClassSection();
        classSection.setClassDate(classDate);
        classSection.setClassTitle(classTitle);
        classSection.setSubject(subjectRepository.findById(subjectId).orElseThrow(() -> new RuntimeException("Subject Not Found By ID")));

        classSectionRepository.save(classSection);
        classSectionRepository.flush();

        TeachingAssignment teachingAssignment = new TeachingAssignment();

        teacher.addTeachingAssignments(List.of(teachingAssignment));
        classSection.addAssignment(teachingAssignment);

        List<Student> students = courseEnrollmentRepository
                .findByCourseOffering_Teacher_IdAndCourseOffering_Subject_Id(teacher.getId(), subjectId)
                .stream()
                .map(c -> c.getStudent())
                .toList();

        students.forEach(s -> classSection.addEnrollment(
            Enrollment
                .builder()
                .student(s)
                .isPresent("true".equalsIgnoreCase(attendance.get("attendance[" + s.getId() + "]")))
                .classSection(classSection)
                .build()
            )
        );
        
        genericWebService.saveType(TeachingAssignment.class, teachingAssignment);
    }

    

    @Override
    public List<StudentDTO> getStudentsInSubject(Long teacherId, Long subjectId) {
        return courseEnrollmentRepository.findByCourseOffering_Teacher_IdAndCourseOffering_Subject_Id(teacherId, subjectId)
            .stream()
            .map(e -> StudentDTO.builder().firstName(e.getStudent().getFirstName()).lastName(e.getStudent().getLastName()).id(e.getStudent().getId()).build())
            .toList();
    }

    @Override
    public void addCourseOffering(Long teacherId, Long subjectId, List<Long> studentIds) throws CourseOfferingAlreadyExistsException {
        CourseOffering courseOffering = new CourseOffering();
        Teacher teacher = teacherRepository.findById(teacherId).orElseThrow(() -> new RuntimeException("Teacher Not Found By ID"));
        Subject subject = subjectRepository.findById(subjectId).orElseThrow(() -> new RuntimeException("Subject Not Found By ID"));

        if (courseOfferingRepository.existsByTeacherAndSubject(teacher, subject)) throw new CourseOfferingAlreadyExistsException();

        courseOffering.setSubject(subject);
        teacher.addCourseOfferings(List.of(courseOffering));
        genericWebService.saveType(CourseOffering.class, courseOffering);
        
        studentIds.forEach(studentId -> {
            if (!courseEnrollmentRepository.existsByStudent_idAndCourseOffering_id(studentId, subjectId)) {
               CourseEnrollment courseEnrollment = new CourseEnrollment();
               Student student = studentRepository.findById(studentId).orElseThrow(() -> new RuntimeException("Student Not Found By ID"));
               
               if (!subjectEntrollmentRepository.existsByStudent_idAndSubject_id(studentId, subjectId)) {
                SubjectEnrollment subjectEnrollment = new SubjectEnrollment();
                subjectEnrollment.setStudent(student);
                subjectEnrollment.setSubject(subject);
                genericWebService.saveType(SubjectEnrollment.class, subjectEnrollment);
               }

               courseEnrollment.setStudent(student);
               courseOffering.addCourseEnrollments(List.of(courseEnrollment));
               genericWebService.saveType(CourseEnrollment.class, courseEnrollment);
            }
        });
    }

    @Override
    public long countAllTeachers() {
        return teacherRepository.count();
    }
}
    