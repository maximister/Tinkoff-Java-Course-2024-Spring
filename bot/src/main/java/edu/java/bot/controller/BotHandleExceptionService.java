package edu.java.bot.controller;

import edu.java.bot.exceptions.BotException;
import edu.java.bot.model.controllerDto.ApiErrorResponse;
import java.util.Arrays;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class BotHandleExceptionService extends ResponseEntityExceptionHandler {
    @ExceptionHandler(BotException.class)
    public ResponseEntity<ApiErrorResponse> handleScrapperExceptions(BotException ex) {
        return new ResponseEntity<>(
            new ApiErrorResponse(
                ex.getDescription(),
                ex.getHttpStatusCode().toString(),
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                Arrays.stream(ex.getStackTrace()).map(StackTraceElement::toString).toList()
            ),
            ex.getHttpStatusCode()
        );
    }

    @Override
    protected ResponseEntity<Object> handleHandlerMethodValidationException(
        HandlerMethodValidationException ex,
        HttpHeaders headers,
        HttpStatusCode status,
        WebRequest request
    ) {
        return toApiErrorResponse(ex, status);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
        HttpMessageNotReadableException ex,
        HttpHeaders headers,
        HttpStatusCode status,
        WebRequest request
    ) {
        return super.handleHttpMessageNotReadable(ex, headers, status, request);
    }

    private ResponseEntity<Object> toApiErrorResponse(Exception ex, HttpStatusCode status) {
        return new ResponseEntity<>(
            new ApiErrorResponse(
                "Некорректные параметры запроса",
                String.valueOf(status.value()),
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                Arrays.stream(ex.getStackTrace()).map(StackTraceElement::toString).toList()
            ),
            status
        );
    }
}
