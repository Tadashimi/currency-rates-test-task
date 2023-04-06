package com.ukolpakova.soap.controller;

import com.ukolpakova.soap.wsclient.generated.FxRates;
import com.ukolpakova.soap.wsclient.generated.GetCurrencyListResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/rates")
public class RatesController {

    @GetMapping
    public List<Object> getCurrencyList() {
        FxRates fxRatesService = new FxRates();
        GetCurrencyListResponse.GetCurrencyListResult currencyList = fxRatesService.getFxRatesSoap().getCurrencyList();
        List<Object> currencyListContent = currencyList.getContent();
        return currencyListContent;
    }
}
