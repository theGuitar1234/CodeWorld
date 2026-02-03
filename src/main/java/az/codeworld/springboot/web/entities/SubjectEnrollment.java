package az.codeworld.springboot.web.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import az.codeworld.springboot.admin.entities.Student;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SUBJECT_ENROLLMENTS", uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "subject_id"}))
public class SubjectEnrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long enrollmentId;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;
}
