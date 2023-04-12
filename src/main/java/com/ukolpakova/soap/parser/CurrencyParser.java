package com.ukolpakova.soap.parser;

import com.ukolpakova.soap.exception.CurrencyParseException;
import com.ukolpakova.soap.model.Currency;
import com.ukolpakova.soap.parser.handler.CurrencyListParserHandler;
import com.ukolpakova.soap.parser.utils.NodeToInputStreamConverter;
import com.ukolpakova.soap.wsclient.generated.GetCurrencyListResponse;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.ukolpakova.soap.localization.ParseCurrencyListErrorMessageConstant.CURRENCY_LIST_RESULT_IS_NULL;
import static com.ukolpakova.soap.localization.ParseCurrencyListErrorMessageConstant.DOCUMENT_IS_EMPTY;

/**
 * Parser for currency list.
 */
@Component
public class CurrencyParser {

    private final Logger logger = LoggerFactory.getLogger(CurrencyParser.class);

    private final CurrencyListParserHandler currencyListParserHandler;

    private SAXParser saxParser;

    @Autowired
    public CurrencyParser(CurrencyListParserHandler currencyListParserHandler) {
        this.currencyListParserHandler = currencyListParserHandler;
    }

    @PostConstruct
    private void init() throws ParserConfigurationException, SAXException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        saxParser = factory.newSAXParser();
    }

    /**
     * It assumed that XML for parsing is get from SOAP server.
     * It's recommended to use this method in try-catch block to avoid unexpected errors in case of any changes in SOAP response.
     *
     * @param currencyList SOAP response that contains XML data for parsing currency list
     * @return map of currency code and {@link Currency}
     */
    public Map<String, Currency> parseCurrencyList(GetCurrencyListResponse.GetCurrencyListResult currencyList) throws SAXException, IOException, TransformerException {
        if (Objects.isNull(currencyList)) {
            logger.error("currencyList is null");
            throw new CurrencyParseException(CURRENCY_LIST_RESULT_IS_NULL);
        }
        List<Object> content = currencyList.getContent();
        if (Objects.isNull(content) || content.isEmpty()) {
            logger.error("content is empty");
            throw new CurrencyParseException(DOCUMENT_IS_EMPTY);
        }
        InputStream inputStream = NodeToInputStreamConverter.convertNodeToInputStream((Node) content.get(0));
        saxParser.parse(inputStream, currencyListParserHandler);
        return currencyListParserHandler.getCurrenciesMap();
    }


}
