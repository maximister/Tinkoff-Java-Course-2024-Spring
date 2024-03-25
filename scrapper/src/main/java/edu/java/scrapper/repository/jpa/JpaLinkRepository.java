package edu.java.scrapper.repository.jpa;

import edu.java.scrapper.repository.jpa.entity.LinkEntity;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaLinkRepository extends JpaRepository<LinkEntity, Long> {
    Optional<LinkEntity> findByUrl(String url);

    List<LinkEntity> findByLastCheckBefore(OffsetDateTime timeForUpdate);
}
