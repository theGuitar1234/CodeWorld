package az.codeworld.springboot.web.entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
// @Builder
@ToString
@NoArgsConstructor
@Table(name="CLASS_SECTIONS")
public class ClassSection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long classId;

    @Column(name = "CLASS_TITLE")
    private String classTitle;

    @Column
    private LocalDate classDate;

    @ManyToOne
    @JoinColumn(name = "id")
    private Subject subject;

    @JsonIgnore
    @OneToMany(mappedBy = "classSection", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeachingAssignment> teachingAssignments = new ArrayList<>();

    public void addAssignment(TeachingAssignment teachingAssignment) {
        this.teachingAssignments.add(teachingAssignment);
        teachingAssignment.setClassSection(this);
    }

    @JsonIgnore
    @OneToMany(mappedBy = "classSection", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Enrollment> enrollments = new ArrayList<>();

    public void addEnrollment(Enrollment enrollment) {
        this.enrollments.add(enrollment);
        enrollment.setClassSection(this);
    }

}
