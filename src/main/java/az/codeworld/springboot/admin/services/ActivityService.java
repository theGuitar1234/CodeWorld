package az.codeworld.springboot.admin.services;

import java.util.List;

import az.codeworld.springboot.admin.events.ActivityEvent;
import az.codeworld.springboot.admin.records.ActivityRecord;
import az.codeworld.springboot.utilities.constants.eventtype;

public interface ActivityService {
    void recordActivity(eventtype eventtype,
            String title,
            String description,
            String actor,
            String subject);

    List<ActivityRecord> getLatestActivities();
}
