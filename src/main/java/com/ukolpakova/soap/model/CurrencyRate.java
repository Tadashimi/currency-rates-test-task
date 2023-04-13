package com.ukolpakova.soap.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Represents a currency rate. Currency code can be found in ISO 4217.
 * Currency amount provides the rate against EUR.
 */
public class CurrencyRate {
    /**
     * ISO 4217 currency code for this currency.
     */
    private String currencyCode;

    /**
     * Currency amount in 1 euro.
     */
    private BigDecimal currencyAmount;

    public CurrencyRate() {
    }

    public CurrencyRate(String currencyCode, BigDecimal currencyAmount) {
        this.currencyCode = currencyCode;
        this.currencyAmount = currencyAmount;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public BigDecimal getCurrencyAmount() {
        return currencyAmount;
    }

    public void setCurrencyAmount(BigDecimal currencyAmount) {
        this.currencyAmount = currencyAmount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CurrencyRate that = (CurrencyRate) o;
        return Objects.equals(currencyCode, that.currencyCode) && Objects.equals(currencyAmount, that.currencyAmount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currencyCode, currencyAmount);
    }
}
