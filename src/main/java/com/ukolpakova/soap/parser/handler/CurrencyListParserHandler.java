package com.ukolpakova.soap.parser.handler;

import com.ukolpakova.soap.constant.CurrencyNameLanguage;
import com.ukolpakova.soap.model.Currency;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.HashMap;
import java.util.Map;

@Component
public class CurrencyListParserHandler extends DefaultHandler {
    private static final String currencyEntryTag = "CcyNtry";
    private static final String currencyTag = "Ccy";
    private static final String currencyNameTag = "CcyNm";
    private static final String currencyTableTag = "CcyTbl";
    private static final String fxRatesTag = "FxRates";
    private static final String langAttributeTag = "lang";

    private Map<String, Currency> currenciesMap;
    private StringBuilder elementValue;
    private Pair<String, StringBuilder> nameValue;

    Currency currentCurrency;

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
            case fxRatesTag, currencyTableTag -> currenciesMap = new HashMap<>();
            case currencyEntryTag -> currentCurrency = new Currency();
            case currencyTag -> elementValue = new StringBuilder();
            case currencyNameTag -> {
                elementValue = new StringBuilder();
                nameValue = new ImmutablePair<>(attributes.getValue(langAttributeTag), elementValue);
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        switch (qName) {
            case currencyTag -> {
                String currencyCode = elementValue.toString();
                currentCurrency.setCurrencyCode(currencyCode);
            }
            case currencyNameTag -> {
                CurrencyNameLanguage currencyNameLanguage = CurrencyNameLanguage.valueOf(nameValue.getLeft());
                currentCurrency.addCurrencyName(currencyNameLanguage, nameValue.getRight().toString());
            }
            case currencyEntryTag -> currenciesMap.put(currentCurrency.getCurrencyCode(), currentCurrency);
        }
    }

    public Map<String, Currency> getCurrenciesMap() {
        return currenciesMap;
    }
}
