package az.codeworld.springboot.admin.services.serviceImpl;

import java.time.Instant;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import az.codeworld.springboot.admin.repositories.RequestRepository;
import az.codeworld.springboot.admin.services.RequestCleanUpService;
import jakarta.transaction.Transactional;

@Service
@Profile("prod")
public class RequestCleanUpServiceImplProd implements RequestCleanUpService {

    private Logger log = LoggerFactory.getLogger(RequestCleanUpServiceImplProd.class);

    private final RequestRepository requestRepository;

    public RequestCleanUpServiceImplProd(
        RequestRepository requestRepository
    ) {
        this.requestRepository = requestRepository;
    }

    @Override
    @Transactional
    @Scheduled(cron = "0 0 3 * * *")
    public void deleteExpiredRequests() {
        long cutoff = Instant.now().toEpochMilli();
        int deleted = requestRepository.deleteExpiredRequests(cutoff);
        log.info("Deleted {} expired requests", deleted);
    }
}
