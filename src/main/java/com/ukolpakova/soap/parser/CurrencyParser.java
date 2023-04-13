package com.ukolpakova.soap.parser;

import com.ukolpakova.soap.exception.CurrencyParseException;
import com.ukolpakova.soap.model.Currency;
import com.ukolpakova.soap.parser.handler.CurrencyListParserHandler;
import com.ukolpakova.soap.parser.utils.NodeToInputStreamConverter;
import com.ukolpakova.soap.wsclient.generated.GetCurrencyListResponse;
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
import java.util.Map;
import java.util.Objects;

import static com.ukolpakova.soap.localization.ParseCurrencyListErrorMessageConstant.CURRENCY_LIST_RESULT_IS_NULL;
import static com.ukolpakova.soap.localization.ParseCurrencyListErrorMessageConstant.DOCUMENT_IS_EMPTY;
import static com.ukolpakova.soap.localization.ParseCurrencyListErrorMessageConstant.FXRATES_IS_NULL;

/**
 * Parser for currency list.
 */
@Component
public class CurrencyParser {

    private final Logger logger = LoggerFactory.getLogger(CurrencyParser.class);
    private final CurrencyListParserHandler currencyListParserHandler;
    private final SAXParser saxParser;

    public CurrencyParser(CurrencyListParserHandler currencyListParserHandler, SAXParser saxParser) {
        this.currencyListParserHandler = currencyListParserHandler;
        this.saxParser = saxParser;
    }

    /**
     * It assumed that XML for parsing is get from SOAP server.
     * It's recommended to use this method in try-catch block to avoid unexpected errors in case of any changes in SOAP response.
     *
     * @param currencyList SOAP response that contains XML data for parsing currency list
     * @return map of currency code and {@link Currency}
     */
    public Map<String, Currency> parseCurrencyList(GetCurrencyListResponse.GetCurrencyListResult currencyList)
            throws SAXException, IOException, TransformerException {
        if (Objects.isNull(currencyList)) {
            logger.error("currencyList is null");
            throw new CurrencyParseException(CURRENCY_LIST_RESULT_IS_NULL);
        }
        List<Object> document = currencyList.getContent();
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
        saxParser.parse(inputStream, currencyListParserHandler);
        return currencyListParserHandler.getCurrenciesMap();
    }


}
