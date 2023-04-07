package com.ukolpakova.soap.parser;

import com.ukolpakova.soap.exception.CurrencyParseException;
import com.ukolpakova.soap.model.CurrencyRate;
import com.ukolpakova.soap.wsclient.generated.GetCurrentFxRatesResponse;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CurrencyRatesParser {
    private static final String currencyCodeEUR = "EUR";
    private static final String fxRateTag = "FxRate";
    private static final String currencyAmountTag = "CcyAmt";
    private static final String ERROR_PARSING_CURRENCY_RATES = "Error while parsing currency rates: ";

    private final Logger logger = LoggerFactory.getLogger(CurrencyRatesParser.class);

    private final GetCurrentFxRatesResponse.GetCurrentFxRatesResult currentEUFxRates;

    public CurrencyRatesParser(GetCurrentFxRatesResponse.GetCurrentFxRatesResult currentEUFxRates) {
        this.currentEUFxRates = currentEUFxRates;
    }

    public List<CurrencyRate> parseCurrencyRates() {
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
        return parseFxRates(fxRates);
    }

    private List<CurrencyRate> parseFxRates(Element fxRates) {
        logger.debug("Starting parsing currency fxRates: {}", fxRates);
        NodeList fxRateNodeList = fxRates.getElementsByTagName(fxRateTag);
        return parseRates(fxRateNodeList);
    }

    private List<CurrencyRate> parseRates(NodeList fxRateNodeList) {
        logger.debug("Starting parsing currency fxRateNodeList: {}", fxRateNodeList);
        List<CurrencyRate> currencyRates = new ArrayList<>();
        for (int i = 0; i < fxRateNodeList.getLength(); i++) {
            Pair<String, Double> currencyRateData = parseAmount((Element) fxRateNodeList.item(i));
            if (Objects.isNull(currencyRateData)) {
                logger.error("Amount is not found");
                throw new CurrencyParseException(ERROR_PARSING_CURRENCY_RATES + "Amount is not found");
            }
            currencyRates.add(new CurrencyRate(currencyRateData.getLeft(), currencyRateData.getRight()));
        }
        return currencyRates;
    }

    private Pair<String, Double> parseAmount(Element fxRateNode) {
        logger.debug("Starting parsing amount from fxRateNode: {}", fxRateNode);
        NodeList currencyAmountNodeList = fxRateNode.getElementsByTagName(currencyAmountTag);
        for (int j = 0; j < currencyAmountNodeList.getLength(); j++) {
            Node currentNode = currencyAmountNodeList.item(j);
            String currencyCode = currentNode.getFirstChild().getTextContent();
            if (!currencyCodeEUR.equals(currencyCode)) {
                String currencyAmountInOneEuro = currentNode.getLastChild().getTextContent();
                try {
                    double amount = Double.parseDouble(currencyAmountInOneEuro);
                    return new ImmutablePair<>(currencyCode, amount);
                } catch (RuntimeException exception) {
                    logger.error("Amount is not double");
                    throw new CurrencyParseException(ERROR_PARSING_CURRENCY_RATES + "Amount is not double");
                }
            }
        }
        return null;
    }
}
