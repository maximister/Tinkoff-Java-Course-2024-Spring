package edu.java.scrapper.exceptions;

import org.springframework.http.HttpStatus;

public final class UnauthorizedChatException extends ScrapperException {
    public UnauthorizedChatException(long chatId) {
        super(
            "Chat %s has not registered".formatted(chatId),
            "Impossible to process chat before it's registration",
            HttpStatus.UNAUTHORIZED
        );
    }
}
