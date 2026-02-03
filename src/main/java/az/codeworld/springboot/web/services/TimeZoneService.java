package az.codeworld.springboot.web.services;

import java.util.List;

import az.codeworld.springboot.web.records.TimeZoneOption;

public interface TimeZoneService {
    List<TimeZoneOption> getTimeZones();
}
