package edu.java.scrapper.httpClients;

import java.net.URI;
import java.time.OffsetDateTime;

public record LinkInfo(
    URI url,
    long id,
    String title,
    String description,
    OffsetDateTime lastModified) {
}
