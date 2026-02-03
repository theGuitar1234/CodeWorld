package az.codeworld.springboot.admin.events;

import java.time.Instant;

import az.codeworld.springboot.utilities.constants.eventtype;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "ACTIVITY_EVENTS", indexes = {
    @Index(name = "idx_activity_at", columnList = "ocurredAt")
})
public class ActivityEvent {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Instant ocurredAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 60)
    private eventtype eventtype;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(nullable = false, length = 400)
    private String description;

    @Column(length = 120)
    private String actor;

    @Column(length = 120)
    private String subject;
}
