package com.ukolpakova.soap.service;

import com.ukolpakova.soap.exception.CurrencyParseException;
import com.ukolpakova.soap.exception.EntityNotFoundException;
import com.ukolpakova.soap.model.Currency;
import com.ukolpakova.soap.model.CurrencyRate;
import com.ukolpakova.soap.parser.CurrencyParser;
import com.ukolpakova.soap.parser.CurrencyRatesParser;
import com.ukolpakova.soap.response.CurrencyRatesResponse;
import com.ukolpakova.soap.wsclient.generated.FxRates;
import com.ukolpakova.soap.wsclient.generated.FxRatesSoap;
import com.ukolpakova.soap.wsclient.generated.GetCurrencyListResponse;
import com.ukolpakova.soap.wsclient.generated.GetCurrentFxRatesResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

class RatesServiceTest {
    private static final String ratesType = "EU";

    @Mock
    private FxRatesSoap fxRatesSoap;

    @Mock
    private FxRates soapFxRatesService;

    @Mock
    GetCurrencyListResponse.GetCurrencyListResult mockedCurrencyListResult;

    @Mock
    GetCurrentFxRatesResponse.GetCurrentFxRatesResult mockedCurrentFxRatesResult;

    @Mock
    CurrencyParser mockedCurrencyParser;

    @Mock
    CurrencyRatesParser mockedCurrencyRateParser;

    private RatesService underTest;

    private AutoCloseable openMocks;

    @BeforeEach
    public void openMocks() {
        openMocks = MockitoAnnotations.openMocks(this);
        when(soapFxRatesService.getFxRatesSoap()).thenReturn(fxRatesSoap);
        underTest = Mockito.spy(new RatesService(soapFxRatesService));
        when(fxRatesSoap.getCurrencyList()).thenReturn(mockedCurrencyListResult);
        when(fxRatesSoap.getCurrentFxRates(ratesType)).thenReturn(mockedCurrentFxRatesResult);
        when(underTest.getCurrencyParser(mockedCurrencyListResult)).thenReturn(mockedCurrencyParser);
        when(underTest.getCurrencyRateParser(mockedCurrentFxRatesResult)).thenReturn(mockedCurrencyRateParser);
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
        when(mockedCurrencyParser.parseCurrencyList()).thenReturn(Map.of(expectedCurrency.getCurrencyCode(), expectedCurrency));
        when(mockedCurrencyRateParser.parseCurrencyRates()).thenReturn(Collections.singletonList(testCurrencyRate));

        List<CurrencyRatesResponse> actualCurrencyRates = underTest.getCurrencyRates();

        Assertions.assertNotNull(actualCurrencyRates);
        Assertions.assertFalse(actualCurrencyRates.isEmpty());
        CurrencyRatesResponse currencyRatesResponse = actualCurrencyRates.get(0);
        Assertions.assertEquals(expectedCurrency.getCurrencyCode(), currencyRatesResponse.getCurrencyCode());
        Assertions.assertEquals(expectedCurrency.getNameEN(), currencyRatesResponse.getNameEN());
        Assertions.assertEquals(expectedCurrency.getNameLT(), currencyRatesResponse.getNameLT());
        Assertions.assertEquals(expectedCurrencyAmount, currencyRatesResponse.getCurrencyAmount());
    }

    @Test
    void getCurrencyRates_whenCurrencyListResponseIsNull_thenThrowException() {
        when(fxRatesSoap.getCurrencyList()).thenReturn(null);
        try {
            underTest.getCurrencyRates();
            fail();
        } catch (Exception exception) {
            Assertions.assertTrue(exception instanceof EntityNotFoundException);
            Assertions.assertEquals("Currency list is not found", exception.getMessage());
        }
    }

    @Test
    void getCurrencyRates_whenCurrentFxRatesResponseIsNull_thenThrowException() {
        when(fxRatesSoap.getCurrentFxRates(ratesType)).thenReturn(null);
        try {
            underTest.getCurrencyRates();
            fail();
        } catch (Exception exception) {
            Assertions.assertTrue(exception instanceof EntityNotFoundException);
            Assertions.assertEquals("Currency rates are not found", exception.getMessage());
        }
    }

    @Test
    void getCurrencyRates_whenCurrencyParserThrowsException_thenThrowCurrencyParseException() {
        when(mockedCurrencyParser.parseCurrencyList()).thenThrow(new RuntimeException("Exception from parser"));
        try {
            underTest.getCurrencyRates();
            fail();
        } catch (Exception exception) {
            Assertions.assertTrue(exception instanceof CurrencyParseException);
            Assertions.assertEquals("Error while parsing currency list", exception.getMessage());
            Assertions.assertEquals("Exception from parser", exception.getCause().getMessage());
        }
    }

    @Test
    void getCurrencyRates_whenCurrencyRateParserThrowsException_thenThrowCurrencyParseException() {
        when(mockedCurrencyRateParser.parseCurrencyRates()).thenThrow(new RuntimeException("Exception from parser"));
        try {
            underTest.getCurrencyRates();
            fail();
        } catch (Exception exception) {
            Assertions.assertTrue(exception instanceof CurrencyParseException);
            Assertions.assertEquals("Error while parsing currencies rates", exception.getMessage());
            Assertions.assertEquals("Exception from parser", exception.getCause().getMessage());
        }
    }

    private Currency generateTestCurrency() {
        return new Currency("ADP", "Andoros peseta", "Andorran peseta");
    }
}