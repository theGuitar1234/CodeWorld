package az.codeworld.springboot.web.entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import az.codeworld.springboot.admin.entities.User;
import az.codeworld.springboot.security.entities.Role;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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
@Table(name = "TEACHERS")
@PrimaryKeyJoinColumn(name = "id")
// @DiscriminatorValue("TEACHER")
public class Teacher extends User{

    @Column
    private String department;

    @Column(name = "job_title")
    private String title;

    @Column
    private LocalDate hiredAt;

    @Column
    private String officeRoom;

    @Column
    private double wage;

    @Column
    private double salary;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "TEACHER_SUBJECTS", 
        joinColumns = @JoinColumn(name = "teacher_id"), 
        inverseJoinColumns = @JoinColumn(name = "subject_id")
    )
    private List<Subject> subjects = new ArrayList<>();
}
