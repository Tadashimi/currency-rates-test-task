package com.ukolpakova.soap.exception;

/**
 * Custom exception for any error during the XML parsing.
 */
public class CurrencyParseException extends RuntimeException {

    public CurrencyParseException(String message) {
        super(message);
    }

    public CurrencyParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
