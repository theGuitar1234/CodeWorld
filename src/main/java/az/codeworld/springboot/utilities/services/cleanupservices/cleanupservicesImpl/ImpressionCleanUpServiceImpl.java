package az.codeworld.springboot.utilities.services.cleanupservices.cleanupservicesImpl;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import az.codeworld.springboot.admin.repositories.ActivityRepository;
import az.codeworld.springboot.utilities.configurations.ApplicationProperties;
import az.codeworld.springboot.utilities.services.cleanupservices.ImpressionCleanUpService;
import az.codeworld.springboot.web.repositories.ImpressionRepository;

@Service
@Profile("prod")
public class ImpressionCleanUpServiceImpl implements ImpressionCleanUpService {
    private Logger log = LoggerFactory.getLogger(ImpressionCleanUpServiceImpl.class);

    private final ImpressionRepository impressionRepository;
    private final ApplicationProperties applicationProperties;

    public ImpressionCleanUpServiceImpl(
        ImpressionRepository impressionRepository,
        ApplicationProperties applicationProperties
    ) {
        this.impressionRepository = impressionRepository;
        this.applicationProperties = applicationProperties;
    }

    @Override
    @Scheduled(cron = "0 0 3 * * *")
    public void deleteOldImpressions() {
        ZoneId zone = ZoneId.of(applicationProperties.getTime().getZone());
        Instant cutOff = LocalDate.now(zone).withDayOfMonth(1).minusMonths(1).atStartOfDay(zone).toInstant();
        int deleted = impressionRepository.deleteImpressionsByOcurredAtAtBefore(cutOff);
        log.info("Deleted {} activit events", deleted);
    }
    
}
