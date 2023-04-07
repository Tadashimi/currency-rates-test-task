package com.ukolpakova.soap.response;

import com.ukolpakova.soap.model.Currency;

/**
 * Represents the available currency rates.
 */
public class CurrencyRatesResponse {
    /**
     * Currency name in Lithuanian.
     */
    private String nameLT;

    /**
     * Currency name in English.
     */
    private String nameEN;

    /**
     * ISO 4217 currency code for this currency.
     */
    private String currencyCode;

    /**
     * Currency amount in 1 euro.
     */
    private double currencyAmount;

    public CurrencyRatesResponse() {
    }

    public CurrencyRatesResponse(Currency currency, double currencyAmount) {
        this.nameLT = currency.getNameLT();
        this.nameEN = currency.getNameEN();
        this.currencyCode = currency.getCurrencyCode();
        this.currencyAmount = currencyAmount;
    }

    public String getNameLT() {
        return nameLT;
    }

    public void setNameLT(String nameLT) {
        this.nameLT = nameLT;
    }

    public String getNameEN() {
        return nameEN;
    }

    public void setNameEN(String nameEN) {
        this.nameEN = nameEN;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public double getCurrencyAmount() {
        return currencyAmount;
    }

    public void setCurrencyAmount(double currencyAmount) {
        this.currencyAmount = currencyAmount;
    }
}
