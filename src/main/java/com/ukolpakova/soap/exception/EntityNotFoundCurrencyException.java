package com.ukolpakova.soap.exception;

import java.util.Locale;

/**
 * Custom exception for missing entities.
 */
public class EntityNotFoundCurrencyException extends AbstractCurrencyException {
    public EntityNotFoundCurrencyException(String messageKey) {
        super(messageKey);
    }

    public EntityNotFoundCurrencyException(String messageKey, Locale locale) {
        super(messageKey, locale);
    }
}
