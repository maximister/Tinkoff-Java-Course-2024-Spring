package edu.java.scrapper.exceptions;

import org.springframework.http.HttpStatus;

public final class ChatNotFoundException extends ScrapperException {
    public ChatNotFoundException(long chatId) {
        super(
            "Chat %s is not found".formatted(chatId),
            "Invalid chat id",
            HttpStatus.NOT_FOUND
        );
    }
}
