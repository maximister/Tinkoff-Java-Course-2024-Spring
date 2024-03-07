package edu.java.scrapper.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ScrapperException extends RuntimeException {
    protected final String description;
    protected final HttpStatus httpStatusCode;

    public ScrapperException(String message, String description, HttpStatus httpStatusCode) {
        super(message);
        this.httpStatusCode = httpStatusCode;
        this.description = description;
    }
}
