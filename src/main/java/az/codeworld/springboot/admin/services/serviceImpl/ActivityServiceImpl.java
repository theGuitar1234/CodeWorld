package az.codeworld.springboot.admin.services.serviceImpl;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import az.codeworld.springboot.admin.events.ActivityEvent;
import az.codeworld.springboot.admin.records.ActivityRecord;
import az.codeworld.springboot.admin.repositories.ActivityRepository;
import az.codeworld.springboot.admin.services.ActivityService;
import az.codeworld.springboot.utilities.constants.eventtype;

@Service
public class ActivityServiceImpl implements ActivityService {

    private final ActivityRepository activityRepository;

    public ActivityServiceImpl(
        ActivityRepository activityRepository
    ) {
        this.activityRepository = activityRepository;
    }

    @Override
    public void recordActivity(
        eventtype eventtype,
        String title,
        String description,
        String actor,
        String subject
    ) {
        ActivityEvent activityEvent = new ActivityEvent();
        activityEvent.setOcurredAt(Instant.now());
        activityEvent.setTitle(title);
        activityEvent.setEventtype(eventtype);
        activityEvent.setDescription(description);
        activityEvent.setActor(actor);
        activityEvent.setSubject(subject);

        activityRepository.save(activityEvent);
        activityRepository.flush();
    }

    @Override
    public List<ActivityRecord> getLatestActivities() {
        return activityRepository.findTop10ByOrderByOcurredAtDesc()
            .stream()
            .map(a -> new ActivityRecord(a.getTitle(), a.getDescription(), a.getEventtype().getEventColor(), a.getOcurredAt()))
            .toList();
    }
    
}
