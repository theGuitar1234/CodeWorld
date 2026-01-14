package az.codeworld.springboot.admin.entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import az.codeworld.springboot.web.entities.TeachingAssignment;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
    private LocalDate hiredAt;

    @Column
    private String officeRoom;

    @OneToMany(mappedBy = "teacher", orphanRemoval = true, cascade = CascadeType.MERGE)
    private List<TeachingAssignment> teachingAssignments = new ArrayList<>();
}
