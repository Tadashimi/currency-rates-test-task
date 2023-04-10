package com.ukolpakova.soap.exception;

import com.ukolpakova.soap.localization.LocalizedErrorMessageProvider;

import java.util.Locale;

public abstract class AbstractCurrencyException extends RuntimeException {
    private final String messageKey;
    private final Locale locale;

    protected AbstractCurrencyException(String messageKey) {
        this(messageKey, Locale.getDefault());
    }

    protected AbstractCurrencyException(String messageKey, Locale locale) {
        this.messageKey = messageKey;
        this.locale = locale;
    }

    /**
     * @return a localized message based on the messageKey provided at instantiation.
     */
    public String getMessage() {
        return getLocalizedMessage();
    }

    /**
     * @return a localized message based on the messageKey provided at instantiation.
     */
    public String getLocalizedMessage() {
        return LocalizedErrorMessageProvider.getMessageForLocale(messageKey, locale);
    }
}
