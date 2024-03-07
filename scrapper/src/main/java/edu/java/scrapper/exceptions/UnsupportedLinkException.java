package edu.java.scrapper.exceptions;

import java.net.URI;
import org.springframework.http.HttpStatus;

public final class UnsupportedLinkException extends ScrapperException {
    public UnsupportedLinkException(URI url) {
        super(
            "Ссылка %s не поддерживается".formatted(url.toString()),
            "Получена неподдерживаемая ссылка",
            HttpStatus.BAD_REQUEST
        );
    }
}
