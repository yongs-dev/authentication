package com.mark.authentication.config.handler;

import com.mark.authentication.exception.ErrorDetails;
import com.mark.authentication.exception.InputNotFoundException;
import com.mark.authentication.exception.UserNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ErrorDetails> handleAllException(final Exception ex, final WebRequest request) {
        final ErrorDetails errorDetails = ErrorDetails.builder()
                .details(request.getDescription(false))
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({UserNotFoundException.class, InputNotFoundException.class})
    public final ResponseEntity<ErrorDetails> handleUserNotFoundException(final Exception ex, final WebRequest request) {
        final ErrorDetails errorDetails = ErrorDetails.builder()
                .details(request.getDescription(false))
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex, final HttpHeaders headers, final HttpStatusCode status, final WebRequest request) {
        final ErrorDetails errorDetails = ErrorDetails.builder()
                .details(request.getDescription(false))
                .message("Total Errors: " + ex.getErrorCount() + " First Error: " + ex.getFieldError().getDefaultMessage())
                .timestamp(LocalDateTime.now())
                .build();


        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
}
