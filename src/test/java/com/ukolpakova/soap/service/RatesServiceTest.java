package com.ukolpakova.soap.service;

import com.ukolpakova.soap.constant.CurrencyNameLanguage;
import com.ukolpakova.soap.exception.CurrencyParseException;
import com.ukolpakova.soap.model.Currency;
import com.ukolpakova.soap.model.CurrencyRate;
import com.ukolpakova.soap.parser.CurrencyParser;
import com.ukolpakova.soap.parser.CurrencyRatesParser;
import com.ukolpakova.soap.response.CurrencyRatesResponse;
import com.ukolpakova.soap.wsclient.generated.FxRatesSoap;
import com.ukolpakova.soap.wsclient.generated.GetCurrencyListResponse;
import com.ukolpakova.soap.wsclient.generated.GetCurrentFxRatesResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

class RatesServiceTest {
    private static final String ratesType = "EU";
    private static final String currencyNameLT = "Andoros peseta";
    private static final String currencyNameEN = "Andorran peseta";

    @Mock
    private FxRatesSoap fxRatesSoap;

    @Mock
    GetCurrencyListResponse.GetCurrencyListResult mockedCurrencyListResult;

    @Mock
    GetCurrentFxRatesResponse.GetCurrentFxRatesResult mockedCurrentFxRatesResult;

    @Mock
    CurrencyParser mockedCurrencyParser;

    @Mock
    CurrencyRatesParser mockedCurrencyRateParser;

    @InjectMocks
    private RatesService underTest;

    private AutoCloseable openMocks;

    @BeforeEach
    public void openMocks() {
        openMocks = MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(underTest, "fxRatesSoap", fxRatesSoap);
        when(fxRatesSoap.getCurrencyList()).thenReturn(mockedCurrencyListResult);
        when(fxRatesSoap.getCurrentFxRates(ratesType)).thenReturn(mockedCurrentFxRatesResult);
    }

    @AfterEach
    public void releaseMocks() throws Exception {
        openMocks.close();
    }

    @Test
    void getCurrencyRates_whenResponsesAreParsed_thenReturnCurrencyRates() {
        Currency expectedCurrency = generateTestCurrency();
        int expectedCurrencyAmount = 12;
        CurrencyRate testCurrencyRate = new CurrencyRate(expectedCurrency.getCurrencyCode(), expectedCurrencyAmount);
        when(mockedCurrencyParser.parseCurrencyList(mockedCurrencyListResult))
                .thenReturn(Map.of(expectedCurrency.getCurrencyCode(), expectedCurrency));
        when(mockedCurrencyRateParser.parseCurrencyRates(mockedCurrentFxRatesResult))
                .thenReturn(Collections.singletonList(testCurrencyRate));

        List<CurrencyRatesResponse> actualCurrencyRates = underTest.getCurrencyRates();

        Assertions.assertNotNull(actualCurrencyRates);
        Assertions.assertFalse(actualCurrencyRates.isEmpty());
        CurrencyRatesResponse currencyRatesResponse = actualCurrencyRates.get(0);
        Assertions.assertEquals(expectedCurrency.getCurrencyCode(), currencyRatesResponse.getCurrencyCode());
        Assertions.assertEquals(currencyNameLT, currencyRatesResponse.getCurrencyNames().get(CurrencyNameLanguage.LT));
        Assertions.assertEquals(currencyNameEN, currencyRatesResponse.getCurrencyNames().get(CurrencyNameLanguage.EN));
        Assertions.assertEquals(expectedCurrencyAmount, currencyRatesResponse.getCurrencyAmount());
    }

    @Test
    void getCurrencyRates_whenCurrencyParserThrowsException_thenThrowCurrencyParseException() {
        when(mockedCurrencyParser.parseCurrencyList(mockedCurrencyListResult)).thenThrow(new RuntimeException("Exception from parser"));
        try {
            underTest.getCurrencyRates();
            fail();
        } catch (Exception exception) {
            Assertions.assertTrue(exception instanceof CurrencyParseException);
            Assertions.assertEquals("Error while parsing currency list", exception.getLocalizedMessage());
        }
    }

    @Test
    void getCurrencyRates_whenCurrencyRateParserThrowsException_thenThrowCurrencyParseException() {
        when(mockedCurrencyRateParser.parseCurrencyRates(mockedCurrentFxRatesResult)).thenThrow(new RuntimeException("Exception from parser"));
        try {
            underTest.getCurrencyRates();
            fail();
        } catch (Exception exception) {
            Assertions.assertTrue(exception instanceof CurrencyParseException);
            Assertions.assertEquals("Error while parsing currencies rates", exception.getMessage());
        }
    }

    private Currency generateTestCurrency() {
        return new Currency("ADP", Map.of(CurrencyNameLanguage.LT, currencyNameLT,
                CurrencyNameLanguage.EN, currencyNameEN));
    }
}