package com.ukolpakova.soap.parser;

import com.ukolpakova.soap.exception.CurrencyParseException;
import com.ukolpakova.soap.model.Currency;
import com.ukolpakova.soap.wsclient.generated.GetCurrencyListResponse;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CurrencyParser {
    private static final String currencyEntryTag = "CcyNtry";
    private static final String currencyTag = "Ccy";
    private static final String currencyNameTag = "CcyNm";
    private static final String langAttributeTag = "lang";
    private static final String langAttributeLT = "LT";
    private static final String langAttributeEN = "EN";
    private static final String ERROR_PARSING_CURRENCY_LIST = "Error while parsing currency list: ";

    private final Logger logger = LoggerFactory.getLogger(CurrencyParser.class);

    private final GetCurrencyListResponse.GetCurrencyListResult currencyList;

    public CurrencyParser(GetCurrencyListResponse.GetCurrencyListResult currencyList) {
        this.currencyList = currencyList;
    }

    public Map<String, Currency> parseCurrencyList() {
        List<Object> content = currencyList.getContent();
        logger.debug("Starting parsing currency list content: {}", content);
        if (content.isEmpty()) {
            logger.error("Content is empty");
            throw new CurrencyParseException(ERROR_PARSING_CURRENCY_LIST + "Document is empty");
        }
        Element fxRates = (Element) content.get(0);
        if (Objects.isNull(fxRates)) {
            logger.error("FxRates is null");
            throw new CurrencyParseException(ERROR_PARSING_CURRENCY_LIST + "FxRates is null");
        }
        return parseFxRates(fxRates);
    }

    private Map<String, Currency> parseFxRates(Element fxRates) {
        logger.debug("Starting parsing currency fxRates: {}", fxRates);
        NodeList fxRateNodeList = fxRates.getElementsByTagName(currencyEntryTag);
        Map<String, Currency> currenciesMap = new HashMap<>();
        for (int i = 0; i < fxRateNodeList.getLength(); i++) {
            Currency parsedCurrency = parseCurrency((Element) fxRateNodeList.item(i));
            currenciesMap.put(parsedCurrency.getCurrencyCode(), parsedCurrency);
        }
        return currenciesMap;
    }

    private Currency parseCurrency(Element fxRateNode) {
        logger.debug("Starting parsing currency from fxRateNode: {}", fxRateNode);
        String currencyCode = parseCurrencyCode(fxRateNode);
        Pair<String, String> currencyNames = parseCurrencyNames(fxRateNode);
        return new Currency(currencyCode, currencyNames.getLeft(), currencyNames.getRight());
    }

    private String parseCurrencyCode(Element fxRateNode) {
        logger.debug("Starting parsing currency code from fxRateNode: {}", fxRateNode);
        NodeList ccy = fxRateNode.getElementsByTagName(currencyTag);
        return ccy.item(0).getFirstChild().getTextContent();
    }

    private Pair<String, String> parseCurrencyNames(Element fxRateNode) {
        logger.debug("Starting parsing currency code from fxRateNode: {}", fxRateNode);
        NodeList currencyNameNodeList = fxRateNode.getElementsByTagName(currencyNameTag);
        String currencyNameLT = "";
        String currencyNameEN = "";
        for (int j = 0; j < currencyNameNodeList.getLength(); j++) {
            Node item = currencyNameNodeList.item(j);
            String lang = item.getAttributes().getNamedItem(langAttributeTag).getTextContent();
            if (Objects.equals(lang, langAttributeLT)) {
                currencyNameLT = item.getTextContent();
            }
            if (Objects.equals(lang, langAttributeEN)) {
                currencyNameEN = item.getTextContent();
            }
        }
        return new ImmutablePair<>(currencyNameLT, currencyNameEN);
    }
}
