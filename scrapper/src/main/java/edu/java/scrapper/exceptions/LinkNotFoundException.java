package edu.java.scrapper.exceptions;

import java.net.URI;
import org.springframework.http.HttpStatus;

public final class LinkNotFoundException extends ScrapperException {

    public LinkNotFoundException(URI url) {
        super(
            "Ссылка %s не найдена".formatted(url.toString()),
            "Получена неотслеживаемая ссылка",
            HttpStatus.NOT_FOUND
        );
    }
}
