package edu.java.scrapper.exceptions;

import java.net.URI;
import org.springframework.http.HttpStatus;

public final class LinkNotFoundException extends ScrapperException {

    public LinkNotFoundException(URI url) {
        super(
            "Link %s is not found".formatted(url.toString()),
            "Received untracked link",
            HttpStatus.NOT_FOUND
        );
    }
}
