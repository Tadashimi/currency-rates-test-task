package com.ukolpakova.soap.localization;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Allows read error message for selected locale.
 */
public class LocalizedErrorMessageProvider {

    /**
     * Retrieves the value for the messageKey from the locale-specific messages.properties, or from
     * the base messages.properties for unsupported locales.
     *
     * @param messageKey The key for the message in the messages.properties ResourceBundle.
     * @param locale The locale to search the message key.
     * @return The value defined for the messageKey in the provided locale.
     */
    public static String getMessageForLocale(String messageKey, Locale locale) {
        return ResourceBundle.getBundle("error_messages", locale)
                .getString(messageKey);
    }
}
