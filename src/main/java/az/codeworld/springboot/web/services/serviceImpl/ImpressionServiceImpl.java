package az.codeworld.springboot.web.services.serviceImpl;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import az.codeworld.springboot.admin.projections.UserIdProjection;
import az.codeworld.springboot.admin.services.UserService;
import az.codeworld.springboot.utilities.configurations.ApplicationProperties;
import az.codeworld.springboot.utilities.constants.source;
import az.codeworld.springboot.web.entities.Impression;
import az.codeworld.springboot.web.repositories.ImpressionRepository;
import az.codeworld.springboot.web.services.ImpressionService;
import jakarta.mail.Session;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Service
public class ImpressionServiceImpl implements ImpressionService {

    private final ConcurrentHashMap<String, Long> recentImpressions = new ConcurrentHashMap<>();
    private static final long TTL_MS = 10_000;

    private final ImpressionRepository impressionRepository;

    private final UserService userService;

    private final ApplicationProperties applicationProperties;

    public ImpressionServiceImpl(
        ImpressionRepository impressionRepository,
        UserService userService,
        ApplicationProperties applicationProperties
    ) {
        this.impressionRepository = impressionRepository;
        this.userService = userService;
        this.applicationProperties = applicationProperties;
    }

    @Override
    public void recordImpression(
        HttpServletRequest request,
        String path,
        source source
    ) {
        String sessionId = null;
        HttpSession session = request.getSession(false);
        if (session != null)
            sessionId = session.getId();

        String key = (sessionId == null ? "no-session" : sessionId) + "|" + source + "|" + path;

        long now = System.currentTimeMillis();
        Long last = recentImpressions.put(key, now);   
        
        if (last != null && (now - last) < TTL_MS) return;

        if (recentImpressions.size() > 50_000) recentImpressions.clear();

        Impression impression = new Impression();
        impression.setOcurredAt(Instant.now());
        impression.setPath(path);
        impression.setSource(source);
        impression.setSessionId(sessionId);
        impression.setIp(request.getRemoteAddr());
        impression.setUserAgent(request.getHeader("User-Agent"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            impression.setUserId((userService.getUserProjectionByUserName(authentication.getName(), UserIdProjection.class).getId()));
        }

        impressionRepository.save(impression);
        impressionRepository.flush();
    }

    @Override
    public long countTotalImpressionsThisMonth() {
        ZoneId zone = ZoneId.of(applicationProperties.getTime().getZone());
        Instant start = LocalDate.now(zone).withDayOfMonth(1)
            .atStartOfDay(zone).toInstant();
        Instant end = LocalDate.now(zone).withDayOfMonth(1).plusMonths(1)
            .atStartOfDay(zone).toInstant();
        
        return impressionRepository.countByOcurredAtBetween(start, end);
    }
    
}
