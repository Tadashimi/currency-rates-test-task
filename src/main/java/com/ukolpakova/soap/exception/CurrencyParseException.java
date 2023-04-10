package com.ukolpakova.soap.exception;

import java.util.Locale;

/**
 * Custom exception for any error during the XML parsing.
 */
public class CurrencyParseException extends AbstractCurrencyException {
    public CurrencyParseException(String messageKey) {
        super(messageKey);
    }

    public CurrencyParseException(String messageKey, Locale locale) {
        super(messageKey, locale);
    }
}
