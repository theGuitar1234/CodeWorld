package az.codeworld.springboot.web.services.serviceImpl;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import az.codeworld.springboot.web.records.TimeZoneOption;
import az.codeworld.springboot.web.services.TimeZoneService;

@Service
public class TimeZoneServiceImpl implements TimeZoneService {

    @Override
    public List<TimeZoneOption> getTimeZones() {
        return ZoneId.getAvailableZoneIds()
            .stream()
            .map(z -> {
                ZoneId zoneId = ZoneId.of(z);
                ZoneOffset offset = zoneId.getRules().getOffset(Instant.now());

                boolean isUtc = offset.equals(ZoneOffset.UTC);

                String prefix = isUtc ? "UTC" : "GMT";
                String offsetText = isUtc ? "+00:00" : offset.getId();

                String label = "(" + prefix + offsetText + ") " + zoneId.getId();
                return new TimeZoneOption(zoneId.getId(), label);
            })
            .sorted(Comparator
                .comparing((TimeZoneOption to) -> ZoneId.of(to.id()).getRules().getOffset(Instant.now()))
                .thenComparing(TimeZoneOption::id))
            .toList();
    }
}

