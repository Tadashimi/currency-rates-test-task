package com.ukolpakova.soap.model;

import com.ukolpakova.soap.constant.CurrencyNameLanguage;

import java.util.Map;
import java.util.Objects;

/**
 * Represents a currency. Currencies are identified by their ISO 4217 currency
 * codes. Visit the <a href="http://www.iso.org/iso/home/standards/currency_codes.htm">
 * ISO web site</a> for more information.
 */
public class Currency {
    /**
     * ISO 4217 currency code for this currency.
     */
    private String currencyCode;

    /**
     * Currency names in different languages.
     */
    private Map<CurrencyNameLanguage, String> currencyNames;

    public Currency(String currencyCode, Map<CurrencyNameLanguage, String> currencyNames) {
        this.currencyCode = currencyCode;
        this.currencyNames = currencyNames;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public Map<CurrencyNameLanguage, String> getCurrencyNames() {
        return currencyNames;
    }

    public void setCurrencyNames(Map<CurrencyNameLanguage, String> currencyNames) {
        this.currencyNames = currencyNames;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Currency currency = (Currency) o;
        return Objects.equals(currencyCode, currency.currencyCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currencyCode);
    }
}
