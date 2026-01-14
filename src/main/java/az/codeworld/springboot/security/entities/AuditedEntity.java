package az.codeworld.springboot.security.entities;

import java.time.Instant;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditedEntity {
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAtAudit;

    @CreatedBy
    @Column(updatable = false, length = 100)
    private String createdByAudit;

    @LastModifiedDate
    @Column(nullable = false)
    private Instant lastModifiedAtAudit;

    @LastModifiedBy
    @Column(length = 100)
    private String lastModifiedByAudit;
}
