package az.codeworld.springboot.admin.repositories;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import az.codeworld.springboot.admin.events.ActivityEvent;
import jakarta.transaction.Transactional;

@Repository
public interface ActivityRepository extends JpaRepository<ActivityEvent, Long>  {
    List<ActivityEvent> findTop10ByOrderByOcurredAtDesc();

    @Modifying
    @Transactional
    @Query("DELETE FROM ActivityEvent a WHERE a.ocurredAt < :cutoff")
    int deleteActivityEventsByOcurredAtAtBefore(@Param("cutoff") Instant cutoff);
}
