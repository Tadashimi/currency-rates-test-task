package com.ukolpakova.soap.parser.handler;

import com.ukolpakova.soap.model.CurrencyRate;
import org.springframework.stereotype.Component;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class CurrencyRatesParserHandler extends DefaultHandler {
    private static final String fxRatesTag = "FxRates";
    private static final String fxRateTag = "FxRate";
    private static final String currencyCodeEUR = "EUR";
    private static final String currencyCodeTag = "Ccy";
    private static final String amountTag = "Amt";

    private List<CurrencyRate> currencyRatesList;
    private StringBuilder elementValue;

    CurrencyRate currencyRate;

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (elementValue == null) {
            elementValue = new StringBuilder();
        } else {
            elementValue.append(ch, start, length);
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        switch (qName) {
            case fxRatesTag -> currencyRatesList = new ArrayList<>();
            case fxRateTag -> currencyRate = new CurrencyRate();
            case currencyCodeTag, amountTag -> elementValue = new StringBuilder();
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        switch (qName) {
            case currencyCodeTag -> {
                String currencyCode = elementValue.toString();
                if (!Objects.equals(currencyCode, currencyCodeEUR)) {
                    currencyRate.setCurrencyCode(currencyCode);
                }
            }
            case amountTag -> {
                String currencyCodeFromCurrencyRate = currencyRate.getCurrencyCode();
                boolean isCurrencyCodeIsAlreadyAddedToRate = Objects.nonNull(currencyCodeFromCurrencyRate)
                        && !currencyCodeFromCurrencyRate.isEmpty();
                if (isCurrencyCodeIsAlreadyAddedToRate) {
                    currencyRate.setCurrencyAmount(new BigDecimal(elementValue.toString()));
                }
            }
            case fxRateTag -> currencyRatesList.add(currencyRate);
        }
    }

    public List<CurrencyRate> getCurrencyRatesList() {
        return currencyRatesList;
    }
}
