package com.ukolpakova.soap.parser;

import com.ukolpakova.soap.constant.CurrencyNameLanguage;
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

import static com.ukolpakova.soap.localization.ParseCurrencyListErrorMessageConstant.ATTRIBUTE_LANG_NOT_FOUND;
import static com.ukolpakova.soap.localization.ParseCurrencyListErrorMessageConstant.CURRENCY_CODE_NOT_EXIST;
import static com.ukolpakova.soap.localization.ParseCurrencyListErrorMessageConstant.CURRENCY_CODE_NOT_FOUND;
import static com.ukolpakova.soap.localization.ParseCurrencyListErrorMessageConstant.CURRENCY_LIST_RESULT_IS_NULL;
import static com.ukolpakova.soap.localization.ParseCurrencyListErrorMessageConstant.CURRENCY_NAME_LANGUAGE_NOT_EXIST;
import static com.ukolpakova.soap.localization.ParseCurrencyListErrorMessageConstant.CURRENCY_NAME_LANGUAGE_NOT_SUPPORTED;
import static com.ukolpakova.soap.localization.ParseCurrencyListErrorMessageConstant.CURRENCY_NAME_NOT_EXIST;
import static com.ukolpakova.soap.localization.ParseCurrencyListErrorMessageConstant.DOCUMENT_IS_EMPTY;
import static com.ukolpakova.soap.localization.ParseCurrencyListErrorMessageConstant.FXRATES_IS_NULL;

/**
 * Parser for currency list.
 */
@Component
public class CurrencyParser {
    private static final String currencyEntryTag = "CcyNtry";
    private static final String currencyTag = "Ccy";
    private static final String currencyNameTag = "CcyNm";
    private static final String langAttributeTag = "lang";

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
            throw new CurrencyParseException(CURRENCY_LIST_RESULT_IS_NULL);
        }
        List<Object> content = currencyList.getContent();
        logger.debug("Starting parsing currency list content: {}", content);
        if (content.isEmpty()) {
            logger.error("Content is empty");
            throw new CurrencyParseException(DOCUMENT_IS_EMPTY);
        }
        Element fxRates = (Element) content.get(0);
        if (Objects.isNull(fxRates)) {
            logger.error("FxRates is null");
            throw new CurrencyParseException(FXRATES_IS_NULL);
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
            throw new CurrencyParseException(CURRENCY_CODE_NOT_FOUND);
        }
        String currencyCode = currencyCodeNodeList.item(0).getTextContent();
        if (Objects.isNull(currencyCode) || currencyCode.isEmpty()) {
            logger.error("currency code is not found");
            throw new CurrencyParseException(CURRENCY_CODE_NOT_EXIST);
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
                throw new CurrencyParseException(ATTRIBUTE_LANG_NOT_FOUND);
            }
            String nameLanguage = langItem.getTextContent();
            if (Objects.isNull(nameLanguage) || nameLanguage.isEmpty()) {
                logger.error("nameLanguage attribute is not found");
                throw new CurrencyParseException(CURRENCY_NAME_LANGUAGE_NOT_EXIST);
            }
            Optional<CurrencyNameLanguage> currencyNameLanguageOptional = CurrencyNameLanguage.getCurrencyNameLanguageIfExist(nameLanguage);
            if (currencyNameLanguageOptional.isEmpty()) {
                logger.error("unsupported language: {}", nameLanguage);
                throw new CurrencyParseException(CURRENCY_NAME_LANGUAGE_NOT_SUPPORTED);
            }
            String currencyName = item.getTextContent();
            if (Objects.isNull(currencyName) || currencyName.isEmpty()) {
                logger.error("currency name is not found for language {}", nameLanguage);
                throw new CurrencyParseException(CURRENCY_NAME_NOT_EXIST);
            }
            currencyNames.put(currencyNameLanguageOptional.get(), currencyName);
        }
        return currencyNames;
    }
}
