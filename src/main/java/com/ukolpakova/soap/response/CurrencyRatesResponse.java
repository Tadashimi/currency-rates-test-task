package com.ukolpakova.soap.response;

import com.ukolpakova.soap.constant.CurrencyNameLanguage;
import com.ukolpakova.soap.model.Currency;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Represents the available currency rates.
 */
public class CurrencyRatesResponse {
    /**
     * ISO 4217 currency code for this currency.
     */
    private String currencyCode;

    /**
     * Currency names.
     */
    private Map<CurrencyNameLanguage, String> currencyNames;

    /**
     * Currency amount in 1 euro.
     */
    private BigDecimal currencyAmount;

    public CurrencyRatesResponse(Currency currency, BigDecimal currencyAmount) {
        this.currencyCode = currency.getCurrencyCode();
        this.currencyNames = currency.getCurrencyNames();
        this.currencyAmount = currencyAmount;
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

    public BigDecimal getCurrencyAmount() {
        return currencyAmount;
    }

    public void setCurrencyAmount(BigDecimal currencyAmount) {
        this.currencyAmount = currencyAmount;
    }
}
