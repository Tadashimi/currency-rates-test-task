package com.ukolpakova.soap.handler;

import com.ukolpakova.soap.exception.CurrencyParseException;
import com.ukolpakova.soap.exception.EntityNotFoundCurrencyException;
import jakarta.xml.ws.WebServiceException;
import jakarta.xml.ws.soap.SOAPFaultException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Global exception handler for handle custom exceptions.
 */
@ControllerAdvice
public class CurrencyParseExceptionHandler extends ResponseEntityExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(CurrencyParseExceptionHandler.class);

    @ExceptionHandler(value = {CurrencyParseException.class, SOAPFaultException.class, WebServiceException.class})
    private ProblemDetail handleParseException(RuntimeException exception) {
        logger.error(exception.getMessage(), exception);
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, exception.getLocalizedMessage());
    }

    @ExceptionHandler(value = EntityNotFoundCurrencyException.class)
    private ProblemDetail handleEntityNotFoundException(RuntimeException exception) {
        logger.error(exception.getMessage(), exception);
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getLocalizedMessage());
    }
}
