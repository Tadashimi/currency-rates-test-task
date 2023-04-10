package com.ukolpakova.soap.parser;

import com.ukolpakova.soap.exception.CurrencyParseException;
import com.ukolpakova.soap.model.CurrencyRate;
import com.ukolpakova.soap.wsclient.generated.GetCurrentFxRatesResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

class CurrencyRatesParserTest {

    @Mock
    private GetCurrentFxRatesResponse.GetCurrentFxRatesResult mockedCurrentFxRatesResult;

    private final CurrencyRatesParser underTest = new CurrencyRatesParser();

    private AutoCloseable autoCloseable;

    @BeforeEach
    public void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void closeMocks() throws Exception {
        autoCloseable.close();
    }

    @Test
    void parseCurrencyRates_whenXmlIsValid_thenReturnsCurrencyMap() throws URISyntaxException, ParserConfigurationException, IOException, SAXException {
        Element currentFxRatesXml = prepareTestCurrentFxRatesXml();
        when(mockedCurrentFxRatesResult.getContent()).thenReturn(Collections.singletonList(currentFxRatesXml));

        List<CurrencyRate> actualCurrencyRates = underTest.parseCurrencyRates(mockedCurrentFxRatesResult);

        Assertions.assertNotNull(actualCurrencyRates);
        Assertions.assertTrue(actualCurrencyRates.containsAll(getExpectedCurrencyCodes()));
    }

    @Test
    void parseCurrencyRates_whenContentIsEmpty_thenThrowException() {
        when(mockedCurrentFxRatesResult.getContent()).thenReturn(Collections.emptyList());

        try {
            underTest.parseCurrencyRates(mockedCurrentFxRatesResult);
            fail();
        } catch (Exception exception) {
            Assertions.assertTrue(exception instanceof CurrencyParseException);
            Assertions.assertEquals("Error while parsing currency rates: Document is empty", exception.getMessage());
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
            Assertions.assertEquals("Error while parsing currency rates: FxRates is null", exception.getMessage());
        }
    }

    private Element prepareTestCurrentFxRatesXml() throws URISyntaxException, ParserConfigurationException, IOException, SAXException {
        File file = new File(getClass().getClassLoader().getResource("testCurrentFxRates.xml").toURI());
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        return db.parse(file).getDocumentElement();
    }

    private Set<CurrencyRate> getExpectedCurrencyCodes() {
        return Set.of(new CurrencyRate("AUD", 1.6312),
                new CurrencyRate("BGN", 1.9558),
                new CurrencyRate("BRL", 5.5096)
        );
    }
}