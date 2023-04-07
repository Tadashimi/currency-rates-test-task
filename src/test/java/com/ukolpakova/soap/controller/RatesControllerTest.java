package com.ukolpakova.soap.controller;

import com.ukolpakova.soap.exception.CurrencyParseException;
import com.ukolpakova.soap.response.CurrencyRatesResponse;
import com.ukolpakova.soap.service.RatesService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

class RatesControllerTest {
    @Mock
    private RatesService ratesService;

    @InjectMocks
    private RatesController underTest;

    private AutoCloseable openMocks;

    @BeforeEach
    public void openMocks() {
        openMocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void releaseMocks() throws Exception {
        openMocks.close();
    }

    @Test
    void getCurrencyRates_whenServiceReturnsCurrencyRates_thenReturnCurrencyRates() {
        CurrencyRatesResponse testRatesResponse = generateTestRatesResponse();
        List<CurrencyRatesResponse> expectedList = Collections.singletonList(testRatesResponse);
        when(ratesService.getCurrencyRates()).thenReturn(expectedList);

        List<CurrencyRatesResponse> actualCurrencyRates = underTest.getCurrencyRates();

        Assertions.assertNotNull(actualCurrencyRates);
        Assertions.assertTrue(actualCurrencyRates.contains(testRatesResponse));
    }

    @Test
    void getCurrencyRates_whenServiceThrowsException_thenThrowsException() {
        String expectedErrorMessage = "Exception message";
        when(ratesService.getCurrencyRates()).thenThrow(new CurrencyParseException(expectedErrorMessage, new Throwable()));

        try {
            underTest.getCurrencyRates();
            fail("Exception should be thrown");
        } catch (Exception exception) {
            Assertions.assertTrue(exception instanceof CurrencyParseException);
            Assertions.assertEquals(expectedErrorMessage, exception.getMessage());
        }
    }

    private CurrencyRatesResponse generateTestRatesResponse() {
        CurrencyRatesResponse testRatesResponse = new CurrencyRatesResponse();
        testRatesResponse.setNameEN("Andorran peseta");
        testRatesResponse.setNameLT("Andoros peseta");
        testRatesResponse.setCurrencyCode("ADP");
        testRatesResponse.setCurrencyAmount(12.0);
        return testRatesResponse;
    }
}