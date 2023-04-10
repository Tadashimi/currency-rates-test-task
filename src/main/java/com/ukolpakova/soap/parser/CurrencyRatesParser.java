package com.ukolpakova.soap.parser;

import com.ukolpakova.soap.exception.CurrencyParseException;
import com.ukolpakova.soap.model.CurrencyRate;
import com.ukolpakova.soap.wsclient.generated.GetCurrentFxRatesResponse;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Parser for currency rates.
 */
@Component
public class CurrencyRatesParser {
    private static final String currencyCodeEUR = "EUR";
    private static final String fxRateTag = "FxRate";
    private static final String currencyAmountTag = "CcyAmt";
    private static final String ERROR_PARSING_CURRENCY_RATES = "Error while parsing currency rates: ";
    public static final String currencyCodeTag = "Ccy";
    public static final String amountTag = "Amt";

    private final Logger logger = LoggerFactory.getLogger(CurrencyRatesParser.class);

    /**
     * It assumed that XML for parsing is get from SOAP server.
     * It's recommended to use this method in try-catch block to avoid unexpected errors in case of any changes in SOAP response.
     *
     * @param currentEUFxRates SOAP response that contains XML data for parsing currency rates
     * @return list of {@link CurrencyRate}
     */
    public List<CurrencyRate> parseCurrencyRates(GetCurrentFxRatesResponse.GetCurrentFxRatesResult currentEUFxRates) {
        if (Objects.isNull(currentEUFxRates)) {
            logger.error("currentEUFxRates is null");
            throw new CurrencyParseException(ERROR_PARSING_CURRENCY_RATES + "GetCurrentFxRatesResult is null. Check the SOAP response");
        }
        List<Object> content = currentEUFxRates.getContent();
        logger.debug("Starting parsing currency list content: {}", content);
        if (content.isEmpty()) {
            logger.error("Content is empty");
            throw new CurrencyParseException(ERROR_PARSING_CURRENCY_RATES + "Document is empty");
        }
        Element fxRates = (Element) content.get(0);
        if (Objects.isNull(fxRates)) {
            logger.error("FxRates is null");
            throw new CurrencyParseException(ERROR_PARSING_CURRENCY_RATES + "FxRates is null");
        }
        return parseFxRatesContent(fxRates);
    }

    private List<CurrencyRate> parseFxRatesContent(Element fxRates) {
        logger.debug("Starting parsing currency fxRates: {}", fxRates);
        NodeList fxRateNodeList = fxRates.getElementsByTagName(fxRateTag);
        return parseFxRateNodeList(fxRateNodeList);
    }

    private List<CurrencyRate> parseFxRateNodeList(NodeList fxRateNodeList) {
        logger.debug("Starting parsing currency fxRateNodeList: {}", fxRateNodeList);
        List<CurrencyRate> currencyRates = new ArrayList<>();
        for (int i = 0; i < fxRateNodeList.getLength(); i++) {
            Pair<String, Double> currencyRateData = parseFxRateNode(fxRateNodeList.item(i));
            currencyRates.add(new CurrencyRate(currencyRateData.getLeft(), currencyRateData.getRight()));
        }
        return currencyRates;
    }

    private Pair<String, Double> parseFxRateNode(Node fxRateNode) {
        logger.debug("Starting parsing amount from fxRateNode: {}", fxRateNode);
        NodeList currencyAmountsNodeList = ((Element) fxRateNode).getElementsByTagName(currencyAmountTag);
        return getCurrencyAmountsInEURFromCurrencyAmountNodeList(currencyAmountsNodeList);
    }

    private Pair<String, Double> getCurrencyAmountsInEURFromCurrencyAmountNodeList(NodeList currencyAmountsNodeList) {
        for (int i = 0; i < currencyAmountsNodeList.getLength(); i++) {
            Pair<String, Double> currencyAmount = parseAmount((Element) currencyAmountsNodeList.item(i));
            if (!Objects.equals(currencyCodeEUR, currencyAmount.getLeft())) {
                return currencyAmount;
            }
        }
        logger.error("Amount is not found");
        throw new CurrencyParseException(ERROR_PARSING_CURRENCY_RATES + "Amount is not found");
    }

    private Pair<String, Double> parseAmount(Element currencyAmount) {
        String currencyCode = parseCurrencyCode(currencyAmount);
        double amount = parseCurrencyAmount(currencyAmount);
        return new ImmutablePair<>(currencyCode, amount);
    }

    private String parseCurrencyCode(Element currencyAmount) {
        NodeList currencyCodesNodeList = currencyAmount.getElementsByTagName(currencyCodeTag);
        if (currencyCodesNodeList.getLength() == 0) {
            logger.error("currency code is not found");
            throw new CurrencyParseException(ERROR_PARSING_CURRENCY_RATES + "Currency code is not found");
        }
        String currencyCode = currencyCodesNodeList.item(0).getTextContent();
        if (Objects.isNull(currencyCode) || currencyCode.isEmpty()) {
            logger.error("currency code value is not found");
            throw new CurrencyParseException(ERROR_PARSING_CURRENCY_RATES + "Currency code value is not found");
        }
        return currencyCode;
    }

    private double parseCurrencyAmount(Element currencyAmount) {
        String amountString = currencyAmount.getElementsByTagName(amountTag).item(0).getTextContent();
        if (Objects.isNull(amountString) || amountString.isEmpty()) {
            logger.error("amount value is not found");
            throw new CurrencyParseException(ERROR_PARSING_CURRENCY_RATES + "Amount value is not found");
        }
        double amount;
        try {
            amount = Double.parseDouble(amountString);
        } catch (RuntimeException exception) {
            logger.error("Amount is not double");
            throw new CurrencyParseException(ERROR_PARSING_CURRENCY_RATES + "Amount is not double");
        }
        return amount;
    }
}
