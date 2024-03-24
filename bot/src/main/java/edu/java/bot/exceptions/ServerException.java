package edu.java.bot.exceptions;

import org.springframework.http.HttpStatus;

public class ServerException extends BotException {
    public ServerException(String message, String description, HttpStatus httpStatusCode) {
        super(message, description, httpStatusCode);
    }
}
