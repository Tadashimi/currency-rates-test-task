package com.ukolpakova.soap.controller;

import com.ukolpakova.soap.constant.CurrencyNameLanguage;
import com.ukolpakova.soap.exception.CurrencyParseException;
import com.ukolpakova.soap.exception.EntityNotFoundCurrencyException;
import com.ukolpakova.soap.model.Currency;
import com.ukolpakova.soap.response.CurrencyRatesResponse;
import com.ukolpakova.soap.service.RatesService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Map;

import static com.ukolpakova.soap.localization.EntityNotFoundErrorMessageConstant.CURRENCY_INFO_NOT_FOUND;
import static com.ukolpakova.soap.localization.ParseCurrencyRatesErrorMessageConstant.CURRENCY_RATES_GENERAL_ERROR;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RatesController.class)
public class RatesControllerMvcTest {

    private static final String apiRatesUrl = "/api/rates";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RatesService serviceUnderTest;

    @Test
    public void getCurrencyRates_whenServiceReturnsList_thenStatusIsOkAndBodyContainsList() throws Exception {
        Currency testCurrency = new Currency("a", Map.of(CurrencyNameLanguage.EN, "enName"));
        CurrencyRatesResponse ratesResponse = new CurrencyRatesResponse(testCurrency, 10);
        when(serviceUnderTest.getCurrencyRates()).thenReturn(Collections.singletonList(ratesResponse));
        this.mockMvc.perform(get(apiRatesUrl)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("[{\"currencyCode\":\"a\",\"currencyNames\":{\"EN\":\"enName\"},\"currencyAmount\":10.0}]"));
    }

    @Test
    public void getCurrencyRates_whenServiceReturnsEmptyList_thenStatusIsOkAndBodyIsEmpty() throws Exception {
        when(serviceUnderTest.getCurrencyRates()).thenReturn(Collections.emptyList());
        this.mockMvc.perform(get(apiRatesUrl)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    public void getCurrencyRates_whenServiceThrowEntityNotFoundException_thenStatusIs404() throws Exception {
        String expectedErrorMessage = "Currency info is not found for currency";
        when(serviceUnderTest.getCurrencyRates()).thenThrow(new EntityNotFoundCurrencyException(CURRENCY_INFO_NOT_FOUND));
        this.mockMvc.perform(get(apiRatesUrl)).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(expectedErrorMessage));
    }

    @Test
    public void getCurrencyRates_whenServiceThrowCurrencyParseException_thenStatusIs500() throws Exception {
        String expectedErrorMessage = "Error while parsing currencies rates";
        when(serviceUnderTest.getCurrencyRates()).thenThrow(new CurrencyParseException(CURRENCY_RATES_GENERAL_ERROR));
        this.mockMvc.perform(get(apiRatesUrl)).andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(expectedErrorMessage));
    }
}
