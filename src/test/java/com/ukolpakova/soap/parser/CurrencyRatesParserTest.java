package com.ukolpakova.soap.parser;

import com.ukolpakova.soap.exception.CurrencyParseException;
import com.ukolpakova.soap.model.CurrencyRate;
import com.ukolpakova.soap.parser.handler.CurrencyRatesParserHandler;
import com.ukolpakova.soap.wsclient.generated.GetCurrentFxRatesResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrencyRatesParserTest {
    @Mock
    private GetCurrentFxRatesResponse.GetCurrentFxRatesResult mockedCurrentFxRatesResult;
    @Mock
    private CurrencyRatesParserHandler mockedCurrencyRatesParserHandler;
    @Mock
    private SAXParser mockedSaxParser;

    @InjectMocks
    private CurrencyRatesParser underTest;

    @Test
    void parseCurrencyRates_whenXmlIsValid_thenReturnsCurrencyMap() throws Exception {
        Element currentFxRatesXml = prepareTestCurrentFxRatesXml();
        when(mockedCurrentFxRatesResult.getContent()).thenReturn(Collections.singletonList(currentFxRatesXml));
        when(mockedCurrencyRatesParserHandler.getCurrencyRatesList()).thenReturn(getExpectedCurrencyRates());

        List<CurrencyRate> actualCurrencyRates = underTest.parseCurrencyRates(mockedCurrentFxRatesResult);

        Assertions.assertNotNull(actualCurrencyRates);
        Assertions.assertTrue(actualCurrencyRates.containsAll(getExpectedCurrencyRates()));
    }

    @Test
    void parseCurrencyRates_whenContentIsEmpty_thenThrowException() {
        when(mockedCurrentFxRatesResult.getContent()).thenReturn(Collections.emptyList());

        try {
            underTest.parseCurrencyRates(mockedCurrentFxRatesResult);
            fail();
        } catch (Exception exception) {
            Assertions.assertTrue(exception instanceof CurrencyParseException);
            Assertions.assertEquals("Error while parsing currency list. Document is empty", exception.getMessage());
        }
    }

    @Test
    void parseCurrencyRates_whenFxRatesIsNull_thenThrowException() {
        when(mockedCurrentFxRatesResult.getContent()).thenReturn(Collections.singletonList(null));

        try {
            underTest.parseCurrencyRates(mockedCurrentFxRatesResult);
            fail();
        } catch (Exception exception) {
            Assertions.assertTrue(exception instanceof CurrencyParseException);
            Assertions.assertEquals("Error while parsing currency list. FxRates is null", exception.getMessage());
        }
    }

    private Element prepareTestCurrentFxRatesXml() throws URISyntaxException, ParserConfigurationException, IOException, SAXException {
        File file = new File(getClass().getClassLoader().getResource("testCurrentFxRates.xml").toURI());
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        return db.parse(file).getDocumentElement();
    }

    private List<CurrencyRate> getExpectedCurrencyRates() {
        return List.of(new CurrencyRate("AUD", new BigDecimal("1.6312")),
                new CurrencyRate("BGN", new BigDecimal("1.9558")),
                new CurrencyRate("BRL", new BigDecimal("5.5096"))
        );
    }
}