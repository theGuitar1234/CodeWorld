package az.codeworld.springboot.admin.entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import az.codeworld.springboot.security.entities.Role;
import az.codeworld.springboot.web.entities.CourseEnrollment;
import az.codeworld.springboot.web.entities.Enrollment;
import az.codeworld.springboot.web.entities.SubjectEnrollment;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.PrimaryKeyJoinColumns;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "STUDENTS")
@PrimaryKeyJoinColumn(name = "id")
// @DiscriminatorValue("STUDENT")
public class Student extends User {

    @Column
    private String groupName;

    @Column(name = "study_year")
    private int year;

    @Column
    private String major;

    @Column
    private double gpa;

    @JsonIgnore
    @OneToMany(mappedBy = "student", orphanRemoval = true, cascade = CascadeType.MERGE)
    private List<Enrollment> enrollments = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "student", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<SubjectEnrollment> subjectEnrollments = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseEnrollment> courseEnrollments = new ArrayList<>();
}
