package com.ukolpakova.soap.exception;

public class CurrencyParseException extends RuntimeException {

    public CurrencyParseException(String message) {
        super(message);
    }

    public CurrencyParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
