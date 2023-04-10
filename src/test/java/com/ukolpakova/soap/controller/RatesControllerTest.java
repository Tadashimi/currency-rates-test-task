package com.ukolpakova.soap.controller;

import com.ukolpakova.soap.constant.CurrencyNameLanguage;
import com.ukolpakova.soap.exception.CurrencyParseException;
import com.ukolpakova.soap.model.Currency;
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
import java.util.Map;

import static com.ukolpakova.soap.localization.ParseCurrencyRatesErrorMessageConstant.CURRENCY_RATES_GENERAL_ERROR;
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
        String expectedErrorMessage = "Error while parsing currencies rates";
        when(ratesService.getCurrencyRates()).thenThrow(new CurrencyParseException(CURRENCY_RATES_GENERAL_ERROR));

        try {
            underTest.getCurrencyRates();
            fail("Exception should be thrown");
        } catch (Exception exception) {
            Assertions.assertTrue(exception instanceof CurrencyParseException);
            Assertions.assertEquals(expectedErrorMessage, exception.getMessage());
        }
    }

    private CurrencyRatesResponse generateTestRatesResponse() {
        Currency currency = new Currency("ADP", Map.of(CurrencyNameLanguage.EN, "Currency"));
        return new CurrencyRatesResponse(currency, 12.0);
    }
}