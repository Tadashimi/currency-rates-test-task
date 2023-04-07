package com.ukolpakova.soap.model;

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
     * Currency name in Lithuanian.
     */
    private String nameLT;

    /**
     * Currency name in English.
     */
    private String nameEN;

    public Currency() {
    }

    public Currency(String currencyCode, String nameLT, String nameEN) {
        this.currencyCode = currencyCode;
        this.nameLT = nameLT;
        this.nameEN = nameEN;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Currency currency = (Currency) o;
        return Objects.equals(currencyCode, currency.currencyCode) && Objects.equals(nameLT, currency.nameLT) && Objects.equals(nameEN, currency.nameEN);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currencyCode, nameLT, nameEN);
    }
}
