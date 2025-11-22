package az.codeworld.springboot.admin.entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import az.codeworld.springboot.web.entities.Subject;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "STUDENTS")
@DiscriminatorValue("STUDENT")
public class Student extends User {

    @Column
    private String groupName;

    @Column
    private int year;

    @Column
    private String major;

    @Column
    private LocalDate enrollmentDate;

    @Column
    private double gpa;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "STUDENT_SUBJECTS", joinColumns = @JoinColumn(name = "subject_id"), inverseJoinColumns = @JoinColumn(name = "student_id"))
    private List<Subject> subjects = new ArrayList<>();

}
