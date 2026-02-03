package az.codeworld.springboot.web.entities;

import java.time.Instant;

import az.codeworld.springboot.utilities.constants.source;
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
@Table(name = "IMPRESSIONS", indexes = {
    @Index(name = "idx_impression_at", columnList = "occurredAt"),
    @Index(name = "idx_impressions_path", columnList = "path"),
    @Index(name = "idx_impressions_session", columnList = "sessionId")
})
public class Impression {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Instant ocurredAt;

    @Column(nullable = false, length = 300)
    private String path;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private source source;

    @Column(length = 80)
    private String sessionId;

    @Column(nullable = true)
    private Long userId;

    @Column(length = 60)
    private String ip;

    @Column(length = 200)
    private String userAgent;
}
