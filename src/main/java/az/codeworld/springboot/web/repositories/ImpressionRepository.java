package az.codeworld.springboot.web.repositories;

import java.time.Instant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import az.codeworld.springboot.web.entities.Impression;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;

@Repository
public interface ImpressionRepository extends JpaRepository<Impression, Long> {

    long countByOcurredAtBetween(Instant start, Instant end);

    @Modifying
    @Transactional
    @Query("DELETE FROM Impression i WHERE i.ocurredAt < :cutoff")
    int deleteImpressionsByOcurredAtAtBefore(@Param("cutoff") Instant cutoff);
}
