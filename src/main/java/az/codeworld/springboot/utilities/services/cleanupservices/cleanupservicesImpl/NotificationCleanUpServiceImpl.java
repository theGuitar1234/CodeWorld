package az.codeworld.springboot.utilities.services.cleanupservices.cleanupservicesImpl;

import java.time.Duration;
import java.time.Instant;

import org.postgresql.translation.messages_bg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import az.codeworld.springboot.admin.repositories.ActivityRepository;
import az.codeworld.springboot.utilities.services.cleanupservices.ActivityCleanUpService;
import az.codeworld.springboot.utilities.services.cleanupservices.NotificationCleanUpService;
import az.codeworld.springboot.web.repositories.NotificationRepository;

@Service
@Profile("prod")
public class NotificationCleanUpServiceImpl implements NotificationCleanUpService {

    private Logger log = LoggerFactory.getLogger(NotificationCleanUpServiceImpl.class);

    private final NotificationRepository notificationRepository;

    public NotificationCleanUpServiceImpl(
        NotificationRepository notificationRepository
    ) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    @Scheduled(cron = "0 0 3 * * *")
    public void deleteOldNotifications() {
        Instant cutOff = Instant.now().minus(Duration.ofDays(7));
        int deleted = notificationRepository.deleteNotificationsByCreatedAtBefore(cutOff);
        log.info("Deleted {} notifications", deleted);
    }
}
