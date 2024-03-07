package edu.java.scrapper.exceptions;

import org.springframework.http.HttpStatus;

public final class ChatAlreadyRegisteredException extends ScrapperException {
    public ChatAlreadyRegisteredException(long chatId) {
        super(
            "Чат %s уже зарегистрирован".formatted(chatId),
            "Невозможно зарегистрировать чат несколько раз",
            HttpStatus.CONFLICT
        );
    }
}
