package edu.java.scrapper.exceptions;

import java.net.URI;
import org.springframework.http.HttpStatus;

public final class LinkAlreadyTrackedException extends ScrapperException {
    public LinkAlreadyTrackedException(URI url) {
        super(
            "Link %s is already tracked".formatted(url.toString()),
            "Impossible to track link again",
            HttpStatus.CONFLICT
        );
    }
}
