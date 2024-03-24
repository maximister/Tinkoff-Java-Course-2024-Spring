package edu.java.scrapper.exceptions;

import org.springframework.http.HttpStatus;

public final class ChatAlreadyRegisteredException extends ScrapperException {
    public ChatAlreadyRegisteredException(long chatId) {
        super(
            "This chat is already registered",
            "Impossible to register chat several times",
            HttpStatus.CONFLICT
        );
    }
}
