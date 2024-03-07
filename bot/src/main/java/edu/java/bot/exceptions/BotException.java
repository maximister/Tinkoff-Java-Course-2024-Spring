package edu.java.bot.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BotException extends Exception {
    protected final String description;
    protected final HttpStatus httpStatusCode;

    public BotException(String message, String description, HttpStatus httpStatusCode) {
        super(message);
        this.httpStatusCode = httpStatusCode;
        this.description = description;
    }
}
