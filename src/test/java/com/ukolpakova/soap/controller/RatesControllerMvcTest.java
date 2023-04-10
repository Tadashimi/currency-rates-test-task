package com.ukolpakova.soap.controller;

import com.ukolpakova.soap.constants.CurrencyNameLanguage;
import com.ukolpakova.soap.exception.CurrencyParseException;
import com.ukolpakova.soap.exception.EntityNotFoundException;
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
    private RatesService service;

    @Test
    public void getCurrencyRates_whenServiceReturnsList_thenStatusIsOkAndBodyContainsList() throws Exception {
        Currency testCurrency = new Currency("a", Map.of(CurrencyNameLanguage.EN, "enName"));
        CurrencyRatesResponse ratesResponse = new CurrencyRatesResponse(testCurrency, 10);
        when(service.getCurrencyRates()).thenReturn(Collections.singletonList(ratesResponse));
        this.mockMvc.perform(get(apiRatesUrl)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("[{\"currencyCode\":\"a\",\"currencyNames\":{\"EN\":\"enName\"},\"currencyAmount\":10.0}]"));
    }

    @Test
    public void getCurrencyRates_whenServiceReturnsEmptyList_thenStatusIsOkAndBodyIsEmpty() throws Exception {
        when(service.getCurrencyRates()).thenReturn(Collections.emptyList());
        this.mockMvc.perform(get(apiRatesUrl)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    public void getCurrencyRates_whenServiceThrowEntityNotFoundException_thenStatusIs404() throws Exception {
        String errorMessage = "not found message";
        when(service.getCurrencyRates()).thenThrow(new EntityNotFoundException(errorMessage));
        this.mockMvc.perform(get(apiRatesUrl)).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(errorMessage));
    }

    @Test
    public void getCurrencyRates_whenServiceThrowCurrencyParseException_thenStatusIs500() throws Exception {
        String errorMessage = "parse error message";
        when(service.getCurrencyRates()).thenThrow(new CurrencyParseException(errorMessage));
        this.mockMvc.perform(get(apiRatesUrl)).andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(errorMessage));
    }
}
