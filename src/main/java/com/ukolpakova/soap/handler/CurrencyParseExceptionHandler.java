package com.ukolpakova.soap.handler;

import com.ukolpakova.soap.exception.CurrencyParseException;
import com.ukolpakova.soap.exception.EntityNotFoundCurrencyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Locale;

/**
 * Global exception handler for handle custom exceptions.
 */
@ControllerAdvice
public class CurrencyParseExceptionHandler extends ResponseEntityExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(CurrencyParseExceptionHandler.class);

    @ExceptionHandler(value = CurrencyParseException.class)
    private ResponseEntity<Object> handleParseException(RuntimeException exception, WebRequest request, Locale locale) {
        logger.error(exception.getMessage());
        return handleExceptionInternal(exception, exception.getLocalizedMessage(),
                new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(value = EntityNotFoundCurrencyException.class)
    private ResponseEntity<Object> handleEntityNotFoundException(RuntimeException exception, WebRequest request) {
        logger.error(exception.getMessage());
        return handleExceptionInternal(exception, exception.getLocalizedMessage(),
                new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }
}
