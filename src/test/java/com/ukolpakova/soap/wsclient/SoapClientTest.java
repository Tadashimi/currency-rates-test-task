package com.ukolpakova.soap.wsclient;

import com.ukolpakova.soap.wsclient.generated.FxRates;
import com.ukolpakova.soap.wsclient.generated.GetCurrencyListResponse;
import com.ukolpakova.soap.wsclient.generated.GetCurrentFxRatesResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class SoapClientTest {

    public static final String EU_RATE_TYPE = "EU";
    private final FxRates fxRatesService = new FxRates();

    @Test
    public void getCurrencyList() {
        GetCurrencyListResponse.GetCurrencyListResult currencyList = fxRatesService.getFxRatesSoap().getCurrencyList();
        List<Object> currencyListContent = currencyList.getContent();
        System.out.println(currencyListContent);
        Assertions.assertNotNull(currencyListContent);
        Assertions.assertFalse(currencyListContent.isEmpty());
    }

    @Test
    public void getCurrentFxRates() {
        GetCurrentFxRatesResponse.GetCurrentFxRatesResult eu = fxRatesService.getFxRatesSoap().getCurrentFxRates(EU_RATE_TYPE);
        List<Object> fxRatesContent = eu.getContent();
        System.out.println(fxRatesContent);
        Assertions.assertNotNull(fxRatesContent);
        Assertions.assertFalse(fxRatesContent.isEmpty());
    }
}
