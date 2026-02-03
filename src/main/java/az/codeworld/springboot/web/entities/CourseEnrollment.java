package az.codeworld.springboot.web.entities;

import az.codeworld.springboot.admin.entities.Student;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "COURSE_ENROLLMENTS")
public class CourseEnrollment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "course_offering_id")
    private CourseOffering courseOffering;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;
}
