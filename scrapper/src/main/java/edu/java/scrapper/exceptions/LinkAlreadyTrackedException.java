package edu.java.scrapper.exceptions;

import java.net.URI;
import org.springframework.http.HttpStatus;

public final class LinkAlreadyTrackedException extends ScrapperException {
    public LinkAlreadyTrackedException(URI url) {
        super(
            "Ссылка %s уже отслеживается".formatted(url.toString()),
            "Невозможно повторно добавить ссылку",
            HttpStatus.CONFLICT
        );
    }
}
