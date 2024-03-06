package edu.java.httpClients;

import java.net.URL;
import java.time.OffsetDateTime;

public record LinkInfo(
    URL url,
    long id,
    String title,
    String description,
    OffsetDateTime lastModified) {
}
