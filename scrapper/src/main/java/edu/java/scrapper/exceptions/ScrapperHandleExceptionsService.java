package edu.java.scrapper.exceptions;

import edu.java.scrapper.model.ControllerDto.ApiErrorResponse;
import java.util.Arrays;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.method.MethodValidationException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ScrapperHandleExceptionsService extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ScrapperException.class)
    public ResponseEntity<ApiErrorResponse> handleScrapperExceptions(ScrapperException ex) {
        return new ResponseEntity<>(
            new ApiErrorResponse(
                ex.getDescription(),
                Integer.toString(ex.getHttpStatusCode().value()),
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                Arrays.stream(ex.getStackTrace()).map(StackTraceElement::toString).toList()
            ),
            ex.getHttpStatusCode()
        );
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleServerExceptions(Exception ex) {
        return new ResponseEntity<>(
            new ApiErrorResponse(
                "Sorry, unexpected server error",
                "500",
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                Arrays.stream(ex.getStackTrace()).map(StackTraceElement::toString).toList()
            ),
            HttpStatusCode.valueOf(500)
        );
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
        HttpRequestMethodNotSupportedException ex,
        HttpHeaders headers,
        HttpStatusCode status,
        WebRequest request
    ) {
        return toApiErrorResponse(ex, status);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
        HttpMediaTypeNotSupportedException ex,
        HttpHeaders headers,
        HttpStatusCode status,
        WebRequest request
    ) {
        return toApiErrorResponse(ex, status);
    }

    @Override
    protected ResponseEntity<Object> handleMissingPathVariable(
        MissingPathVariableException ex,
        HttpHeaders headers,
        HttpStatusCode status,
        WebRequest request
    ) {
        return toApiErrorResponse(ex, status);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex,
        HttpHeaders headers,
        HttpStatusCode status,
        WebRequest request
    ) {
        return toApiErrorResponse(ex, status);
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
    protected ResponseEntity<Object> handleTypeMismatch(
        TypeMismatchException ex,
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
        return toApiErrorResponse(ex, status);
    }

    @Override
    protected ResponseEntity<Object> handleMethodValidationException(
        MethodValidationException ex,
        HttpHeaders headers,
        HttpStatus status,
        WebRequest request
    ) {
        return toApiErrorResponse(ex, status);
    }

    private ResponseEntity<Object> toApiErrorResponse(Exception ex, HttpStatusCode status) {
        return new ResponseEntity<>(
            new ApiErrorResponse(
                "Invalid request parameters",
                String.valueOf(status.value()),
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                Arrays.stream(ex.getStackTrace()).map(StackTraceElement::toString).toList()
            ),
            status
        );
    }
}
