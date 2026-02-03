package az.codeworld.springboot.web.entities;

import az.codeworld.springboot.admin.entities.Teacher;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "TEACHING_ASSIGNMENTS")
public class TeachingAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long teachingAssignmentId;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @ManyToOne
    @JoinColumn(name = "class_id")
    private ClassSection classSection;

}
