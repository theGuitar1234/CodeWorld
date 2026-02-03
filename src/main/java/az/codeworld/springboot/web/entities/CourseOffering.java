package az.codeworld.springboot.web.entities;

import java.util.ArrayList;
import java.util.List;

import az.codeworld.springboot.admin.entities.Teacher;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "COURSE_OFFERINGS")
public class CourseOffering {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @OneToMany(mappedBy = "courseOffering", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseEnrollment> courseEnrollments = new ArrayList<>();

    public void addCourseEnrollments(List<CourseEnrollment> courseEnrollments) {
        courseEnrollments.forEach(ce -> {
            ce.setCourseOffering(this);
            this.courseEnrollments.add(ce);
        });
    }
}
