package edu.java.scrapper.exceptions;

import java.net.URI;
import org.springframework.http.HttpStatus;

public final class UnsupportedLinkException extends ScrapperException {
    public UnsupportedLinkException(URI url) {
        super(
            "Link %s is unsupported".formatted(url.toString()),
            "Received unsupported link",
            HttpStatus.BAD_REQUEST
        );
    }
}
