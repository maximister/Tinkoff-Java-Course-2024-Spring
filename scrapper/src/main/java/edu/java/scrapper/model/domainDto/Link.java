package edu.java.scrapper.model.domainDto;

import java.net.URI;
import java.time.OffsetDateTime;

public record Link(
    long linkId,
    OffsetDateTime lastUpdate,
    OffsetDateTime lastCheck,
    URI url
) {
}
