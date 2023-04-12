package com.ukolpakova.soap.parser;

import com.ukolpakova.soap.constant.CurrencyNameLanguage;
import com.ukolpakova.soap.exception.CurrencyParseException;
import com.ukolpakova.soap.model.Currency;
import com.ukolpakova.soap.parser.handler.CurrencyListParserHandler;
import com.ukolpakova.soap.wsclient.generated.GetCurrencyListResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
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
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

class CurrencyParserTest {

    @Mock
    private GetCurrencyListResponse.GetCurrencyListResult mockedCurrencyList;
    @Mock
    private CurrencyListParserHandler mockedCurrencyListParserHandler;
    @MockBean
    private final CurrencyParser underTest = new CurrencyParser(mockedCurrencyListParserHandler);

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
    void parseCurrencyRates_whenXmlIsValid_thenReturnsCurrencyMap() throws Exception {
        Element currencyListXml = prepareTestCurrencyListXml();
        when(mockedCurrencyList.getContent()).thenReturn(Collections.singletonList(currencyListXml));

        Map<String, Currency> actualCurrencyMap = underTest.parseCurrencyList(mockedCurrencyList);

        Assertions.assertNotNull(actualCurrencyMap);
        Assertions.assertTrue(actualCurrencyMap.keySet().containsAll(getExpectedCurrencyCodes()));
        Assertions.assertTrue(actualCurrencyMap.values().containsAll(getExpectedValues()));
        Currency expectedCurrency = getExpectedValues().get(0);
        Currency actualCurrency = actualCurrencyMap.get(expectedCurrency.getCurrencyCode());
        CurrencyNameLanguage lt = CurrencyNameLanguage.LT;
        CurrencyNameLanguage en = CurrencyNameLanguage.EN;
        Assertions.assertEquals(expectedCurrency.getCurrencyNames().get(lt), actualCurrency.getCurrencyNames().get(lt));
        Assertions.assertEquals(expectedCurrency.getCurrencyNames().get(en), actualCurrency.getCurrencyNames().get(en));
    }

    @Test
    void parseCurrencyRates_whenContentIsEmpty_thenThrowException() {
        when(mockedCurrencyList.getContent()).thenReturn(Collections.emptyList());

        try {
            underTest.parseCurrencyList(mockedCurrencyList);
            fail();
        } catch (Exception exception) {
            Assertions.assertTrue(exception instanceof CurrencyParseException);
            Assertions.assertEquals("Error while parsing currency list. Document is empty", exception.getMessage());
        }
    }

    @Test
    void parseCurrencyRates_whenFxRatesIsNull_thenThrowException() {
        when(mockedCurrencyList.getContent()).thenReturn(Collections.singletonList(null));

        try {
            underTest.parseCurrencyList(mockedCurrencyList);
            fail();
        } catch (Exception exception) {
            Assertions.assertTrue(exception instanceof CurrencyParseException);
            Assertions.assertEquals("Error while parsing currency list. FxRates is null", exception.getMessage());
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

    private List<Currency> getExpectedValues() {
        return List.of(new Currency("ADP", Map.of(CurrencyNameLanguage.LT, "Andoros peseta", CurrencyNameLanguage.EN, "Andorran peseta")),
                new Currency("AED", Map.of(CurrencyNameLanguage.LT, "Jungtinių Arabų Emiratų dirhamas", CurrencyNameLanguage.EN, "UAE dirham")),
                new Currency("AFN", Map.of(CurrencyNameLanguage.LT, "Afganistano afganis", CurrencyNameLanguage.EN, "Afghani")),
                new Currency("ALL", Map.of(CurrencyNameLanguage.LT, "Albanijos lekas", CurrencyNameLanguage.EN, "Albanian lek")),
                new Currency("AMD", Map.of(CurrencyNameLanguage.LT, "Armėnijos dramas", CurrencyNameLanguage.EN, "Armenian dram"))
        );
    }
}