package com.ukolpakova.soap.constants;

import java.util.Optional;

/**
 * Represents the possible language in which currency can have name.
 */
public enum CurrencyNameLanguage {
    LT, EN;

    public static Optional<CurrencyNameLanguage> getCurrencyNameLanguageIfExist(String name) {
        try {
            return Optional.of(CurrencyNameLanguage.valueOf(name));
        } catch (Exception exception) {
            return Optional.empty();
        }
    }
}
