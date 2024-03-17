package edu.java.scrapper.exceptions;

import org.springframework.http.HttpStatus;

public final class ChatNotFoundException extends ScrapperException {
    public ChatNotFoundException(long chatId) {
        super(
            "Чат %s не найден".formatted(chatId),
            "Неверно указан chat id",
            HttpStatus.NOT_FOUND
        );
    }
}
