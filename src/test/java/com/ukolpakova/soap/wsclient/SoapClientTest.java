package com.ukolpakova.soap.wsclient;

import com.oracle.webservices.api.EnvelopeStyle;
import com.oracle.webservices.api.EnvelopeStyleFeature;
import com.ukolpakova.soap.wsclient.generated.FxRates;
import com.ukolpakova.soap.wsclient.generated.GetCurrencyListResponse;
import com.ukolpakova.soap.wsclient.generated.GetCurrentFxRatesResponse;
import com.ukolpakova.soap.wsclient.generated.GetFxRatesForCurrencyResponse;
import com.ukolpakova.soap.wsclient.generated.GetFxRatesResponse;
import jakarta.xml.ws.WebServiceFeature;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class SoapClientTest {

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
    public void getFxRatesContent() {
        GetFxRatesResponse.GetFxRatesResult fxRates = fxRatesService.getFxRatesSoap().getFxRates("EU", "2015-01-02");
        List<Object> fxRatesContent = fxRates.getContent();
        System.out.println(fxRatesContent);
        Assertions.assertNotNull(fxRatesContent);
        Assertions.assertFalse(fxRatesContent.isEmpty());
    }

    @Test
    public void getCurrentFxRates() {
        GetCurrentFxRatesResponse.GetCurrentFxRatesResult eu = fxRatesService.getFxRatesSoap().getCurrentFxRates("EU");
        List<Object> fxRatesContent = eu.getContent();
        System.out.println(fxRatesContent);
        Assertions.assertNotNull(fxRatesContent);
        Assertions.assertFalse(fxRatesContent.isEmpty());
    }

    @Test
    public void getFxRatesForCurrency() {
        GetFxRatesForCurrencyResponse.GetFxRatesForCurrencyResult fxRatesForCurrency = fxRatesService.getFxRatesSoap().getFxRatesForCurrency("EU", "USD", "2015-01-02", "2015-01-03");
        List<Object> fxRatesContent = fxRatesForCurrency.getContent();
        System.out.println(fxRatesContent);
        Assertions.assertNotNull(fxRatesContent);
        Assertions.assertFalse(fxRatesContent.isEmpty());
    }
}
