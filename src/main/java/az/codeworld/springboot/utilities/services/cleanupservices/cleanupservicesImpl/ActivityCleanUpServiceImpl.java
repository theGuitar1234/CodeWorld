package az.codeworld.springboot.utilities.services.cleanupservices.cleanupservicesImpl;

import java.time.Duration;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import az.codeworld.springboot.admin.repositories.ActivityRepository;
import az.codeworld.springboot.utilities.services.cleanupservices.ActivityCleanUpService;

@Service
@Profile("prod")
public class ActivityCleanUpServiceImpl implements ActivityCleanUpService {

    private Logger log = LoggerFactory.getLogger(ActivityCleanUpServiceImpl.class);

    private final ActivityRepository activityRepository;

    public ActivityCleanUpServiceImpl(
        ActivityRepository activityRepository
    ) {
        this.activityRepository = activityRepository;
    }

    @Override
    @Scheduled(cron = "0 0 3 * * *")
    public void deleteOldActivities() {
        Instant cutOff = Instant.now().minus(Duration.ofDays(7));
        int deleted = activityRepository.deleteActivityEventsByOcurredAtAtBefore(cutOff);
        log.info("Deleted {} activit events", deleted);
    }
    
}
