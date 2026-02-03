package az.codeworld.springboot.web.entities;

import java.time.Instant;

import jakarta.annotation.Generated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "NOTIFICATION_RECIPIENTS", indexes = {
    @Index(name = "idx_notification_recipient", columnList = "recipientId, isRead, deliveredAt")
})
public class NotificationRecipient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="notification_id")
    private Notification notification;

    @Column(nullable = false)
    private Long recipientId;

    @Column(nullable = false)
    private boolean isRead = false;

    @Column(nullable = false)
    private Instant deliveredAt;

    @Column
    private Instant readAt;
}
