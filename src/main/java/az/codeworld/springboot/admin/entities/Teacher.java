package az.codeworld.springboot.admin.entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.envers.NotAudited;

import com.fasterxml.jackson.annotation.JsonIgnore;

import az.codeworld.springboot.security.entities.Role;
import az.codeworld.springboot.web.entities.CourseEnrollment;
import az.codeworld.springboot.web.entities.CourseOffering;
import az.codeworld.springboot.web.entities.Subject;
import az.codeworld.springboot.web.entities.TeachingAssignment;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
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
@Table(name = "TEACHERS")
@PrimaryKeyJoinColumn(name = "id")
public class Teacher extends User {

    @Column
    private String department;

    @Column(name = "job_title")
    private String title;

    @Column
    private String officeRoom;

    @JsonIgnore
    @OneToMany(mappedBy = "teacher", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<TeachingAssignment> teachingAssignments = new ArrayList<>();

    public void addTeachingAssignments(List<TeachingAssignment> teachingAssignments) {
        teachingAssignments.forEach(ta -> {
            ta.setTeacher(this);
            this.teachingAssignments.add(ta);
        });
    }

    @JsonIgnore
    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseOffering> courseOfferings = new ArrayList<>();

    public void addCourseOfferings(List<CourseOffering> courseOfferings) {
        courseOfferings.forEach(co -> {
            co.setTeacher(this);
            this.courseOfferings.add(co);
        });
    }
}
