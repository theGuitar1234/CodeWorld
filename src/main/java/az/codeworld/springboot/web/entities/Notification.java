package az.codeworld.springboot.web.entities;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import az.codeworld.springboot.utilities.constants.notificationtype;
import az.codeworld.springboot.web.entities.TeachingAssignment;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
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
@Table(name = "NOTIFICATIONS", indexes = {
    @Index(name = "idx_notification_created", columnList = "createdAt"),
    @Index(name = "idx_notification_type", columnList = "notificationType")
})
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false, length = 500, columnDefinition = "TEXT")
    private String notificationContent;

    @Column(nullable = false, length = 120)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private notificationtype notificationType;

    @Column(nullable = false, length = 200)
    private String link;

    @JsonIgnore
    @OneToMany(mappedBy = "notification", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NotificationRecipient> notificationRecipients = new ArrayList<>();

    public void addNotificationRecipient(NotificationRecipient notificationRecipient) {
        this.notificationRecipients.add(notificationRecipient);
        notificationRecipient.setNotification(this);
    }
}
