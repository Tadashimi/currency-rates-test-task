package com.ukolpakova.soap.parser;

import com.ukolpakova.soap.exception.CurrencyParseException;
import com.ukolpakova.soap.model.CurrencyRate;
import com.ukolpakova.soap.parser.handler.CurrencyRatesParserHandler;
import com.ukolpakova.soap.parser.utils.NodeToInputStreamConverter;
import com.ukolpakova.soap.wsclient.generated.GetCurrentFxRatesResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.SAXParser;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

import static com.ukolpakova.soap.localization.ParseCurrencyRatesErrorMessageConstant.CURRENCY_RATES_RESULT_IS_NULL;
import static com.ukolpakova.soap.localization.ParseCurrencyRatesErrorMessageConstant.DOCUMENT_IS_EMPTY;
import static com.ukolpakova.soap.localization.ParseCurrencyRatesErrorMessageConstant.FXRATES_IS_NULL;

/**
 * Parser for currency rates.
 */
@Component
public class CurrencyRatesParser {
    private final Logger logger = LoggerFactory.getLogger(CurrencyRatesParser.class);
    private final CurrencyRatesParserHandler currencyRatesParserHandler;
    private final SAXParser saxParser;

    public CurrencyRatesParser(CurrencyRatesParserHandler currencyRatesParserHandler, SAXParser saxParser) {
        this.currencyRatesParserHandler = currencyRatesParserHandler;
        this.saxParser = saxParser;
    }

    /**
     * It assumed that XML for parsing is get from SOAP server.
     * It's recommended to use this method in try-catch block to avoid unexpected errors in case of any changes in SOAP response.
     *
     * @param currentEUFxRates SOAP response that contains XML data for parsing currency rates
     * @return list of {@link CurrencyRate}
     */
    public List<CurrencyRate> parseCurrencyRates(GetCurrentFxRatesResponse.GetCurrentFxRatesResult currentEUFxRates)
            throws TransformerException, IOException, SAXException {
        if (Objects.isNull(currentEUFxRates)) {
            logger.error("currentEUFxRates is null");
            throw new CurrencyParseException(CURRENCY_RATES_RESULT_IS_NULL);
        }
        List<Object> document = currentEUFxRates.getContent();
        logger.debug("Starting parsing currency list document: {}", document);
        if (Objects.isNull(document) || document.isEmpty()) {
            logger.error("document is empty");
            throw new CurrencyParseException(DOCUMENT_IS_EMPTY);
        }
        Object fxRates = document.get(0);
        if (Objects.isNull(fxRates)) {
            logger.error("FxRates is null");
            throw new CurrencyParseException(FXRATES_IS_NULL);
        }
        InputStream inputStream = NodeToInputStreamConverter.convertNodeToInputStream((Node) fxRates);
        saxParser.parse(inputStream, currencyRatesParserHandler);
        return currencyRatesParserHandler.getCurrencyRatesList();
    }
}
