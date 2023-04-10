package com.ukolpakova.soap.parser;

import com.ukolpakova.soap.constants.CurrencyNameLanguage;
import com.ukolpakova.soap.exception.CurrencyParseException;
import com.ukolpakova.soap.model.Currency;
import com.ukolpakova.soap.wsclient.generated.GetCurrencyListResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Parser for currency list.
 */
@Component
public class CurrencyParser {
    private static final String currencyEntryTag = "CcyNtry";
    private static final String currencyTag = "Ccy";
    private static final String currencyNameTag = "CcyNm";
    private static final String langAttributeTag = "lang";
    private static final String ERROR_PARSING_CURRENCY_LIST = "Error while parsing currency list: ";

    private final Logger logger = LoggerFactory.getLogger(CurrencyParser.class);

    /**
     * It assumed that XML for parsing is get from SOAP server.
     * It's recommended to use this method in try-catch block to avoid unexpected errors in case of any changes in SOAP response.
     *
     * @param currencyList SOAP response that contains XML data for parsing currency list
     * @return map of currency code and {@link Currency}
     */
    public Map<String, Currency> parseCurrencyList(GetCurrencyListResponse.GetCurrencyListResult currencyList) {
        if (Objects.isNull(currencyList)) {
            logger.error("currencyList is null");
            throw new CurrencyParseException(ERROR_PARSING_CURRENCY_LIST + "GetCurrencyListResult is null. Check the SOAP response");
        }
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
        Map<CurrencyNameLanguage, String> currencyNames = parseCurrencyNames(fxRateNode);
        return new Currency(currencyCode, currencyNames);
    }

    private String parseCurrencyCode(Element fxRateNode) {
        logger.debug("Starting parsing currency code from fxRateNode: {}", fxRateNode);
        NodeList currencyCodeNodeList = fxRateNode.getElementsByTagName(currencyTag);
        if (currencyCodeNodeList.getLength() == 0) {
            logger.error("{} is not found", currencyTag);
            throw new CurrencyParseException(ERROR_PARSING_CURRENCY_LIST + "Currency code node is not found");
        }
        String currencyCode = currencyCodeNodeList.item(0).getTextContent();
        if (Objects.isNull(currencyCode) || currencyCode.isEmpty()) {
            logger.error("currency code is not found");
            throw new CurrencyParseException(ERROR_PARSING_CURRENCY_LIST + "Currency code does not exist");
        }
        return currencyCode;
    }

    private Map<CurrencyNameLanguage, String> parseCurrencyNames(Element fxRateNode) {
        logger.debug("Starting parsing currency code from fxRateNode: {}", fxRateNode);
        NodeList currencyNameNodeList = fxRateNode.getElementsByTagName(currencyNameTag);
        Map<CurrencyNameLanguage, String> currencyNames = new HashMap<>();
        for (int j = 0; j < currencyNameNodeList.getLength(); j++) {
            Node item = currencyNameNodeList.item(j);
            Node langItem = item.getAttributes().getNamedItem(langAttributeTag);
            if (Objects.isNull(langItem)) {
                logger.error("{} attribute is not found", langAttributeTag);
                throw new CurrencyParseException(ERROR_PARSING_CURRENCY_LIST + "lang attribute is not found");
            }
            String nameLanguage = langItem.getTextContent();
            if (Objects.isNull(nameLanguage) || nameLanguage.isEmpty()) {
                logger.error("nameLanguage attribute is not found");
                throw new CurrencyParseException(ERROR_PARSING_CURRENCY_LIST + "currency name language is not set");
            }
            Optional<CurrencyNameLanguage> currencyNameLanguageOptional = CurrencyNameLanguage.getCurrencyNameLanguageIfExist(nameLanguage);
            if (currencyNameLanguageOptional.isEmpty()) {
                logger.error("unsupported language: {}", nameLanguage);
                throw new CurrencyParseException(ERROR_PARSING_CURRENCY_LIST + "currency name language is not defined in app");
            }
            String currencyName = item.getTextContent();
            if (Objects.isNull(currencyName) || currencyName.isEmpty()) {
                logger.error("currency name is not found for language {}", nameLanguage);
                throw new CurrencyParseException(ERROR_PARSING_CURRENCY_LIST + "currency name is not set");
            }
            currencyNames.put(currencyNameLanguageOptional.get(), currencyName);
        }
        return currencyNames;
    }
}
