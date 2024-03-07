package edu.java.scrapper.exceptions;

import org.springframework.http.HttpStatus;

public final class UnauthorizedChatException extends ScrapperException {
    public UnauthorizedChatException(long chatId) {
        super(
            "Чат %s не зарегистрирован".formatted(chatId),
            "Невозможно взаимодействовать с чатом до регистрации",
            HttpStatus.UNAUTHORIZED
        );
    }
}
