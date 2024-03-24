package edu.java.bot.exceptions;

import org.springframework.http.HttpStatus;

public class ClientException extends BotException {
    public ClientException(String message, String description, HttpStatus httpStatusCode) {
        super(message, description, httpStatusCode);
    }
}
