package com.ukolpakova.soap.parser;

import com.ukolpakova.soap.exception.CurrencyParseException;
import com.ukolpakova.soap.model.Currency;
import com.ukolpakova.soap.wsclient.generated.GetCurrencyListResponse;
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
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

class CurrencyParserTest {

    @Mock
    private GetCurrencyListResponse.GetCurrencyListResult mockedCurrencyList;

    private CurrencyParser underTest;
    private AutoCloseable autoCloseable;

    @BeforeEach
    public void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new CurrencyParser(mockedCurrencyList);
    }

    @AfterEach
    public void closeMocks() throws Exception {
        autoCloseable.close();
    }

    @Test
    void parseCurrencyRates_whenXmlIsValid_thenReturnsCurrencyMap() throws URISyntaxException, ParserConfigurationException, IOException, SAXException {
        Element currencyListXml = prepareTestCurrencyListXml();
        when(mockedCurrencyList.getContent()).thenReturn(Collections.singletonList(currencyListXml));

        Map<String, Currency> actualCurrencyMap = underTest.parseCurrencyList();

        Assertions.assertNotNull(actualCurrencyMap);
        Assertions.assertTrue(actualCurrencyMap.keySet().containsAll(getExpectedCurrencyCodes()));
        Assertions.assertTrue(actualCurrencyMap.values().containsAll(getExpectedValues()));
    }

    @Test
    void parseCurrencyRates_whenContentIsEmpty_thenThrowException() {
        when(mockedCurrencyList.getContent()).thenReturn(Collections.emptyList());

        try {
            underTest.parseCurrencyList();
            fail();
        } catch (Exception exception) {
            Assertions.assertTrue(exception instanceof CurrencyParseException);
            Assertions.assertEquals("Error while parsing currency list: Document is empty", exception.getMessage());
        }
    }

    @Test
    void parseCurrencyRates_whenFxRatesIsNull_thenThrowException() {
        when(mockedCurrencyList.getContent()).thenReturn(Collections.singletonList(null));

        try {
            underTest.parseCurrencyList();
            fail();
        } catch (Exception exception) {
            Assertions.assertTrue(exception instanceof CurrencyParseException);
            Assertions.assertEquals("Error while parsing currency list: FxRates is null", exception.getMessage());
        }
    }

    private Element prepareTestCurrencyListXml() throws URISyntaxException, ParserConfigurationException, IOException, SAXException {
        File file = new File(getClass().getClassLoader().getResource("testCurrencyList.xml").toURI());
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        return db.parse(file).getDocumentElement();
    }

    private Set<String> getExpectedCurrencyCodes() {
        return Set.of("ADP", "AED", "AFN", "ALL", "AMD");
    }

    private Set<Currency> getExpectedValues() {
        return Set.of(new Currency("ADP", "Andoros peseta", "Andorran peseta"),
                new Currency("AED", "Jungtinių Arabų Emiratų dirhamas", "UAE dirham"),
                new Currency("AFN", "Afganistano afganis", "Afghani"),
                new Currency("ALL", "Albanijos lekas", "Albanian lek"),
                new Currency("AMD", "Armėnijos dramas", "Armenian dram")
                );
    }
}