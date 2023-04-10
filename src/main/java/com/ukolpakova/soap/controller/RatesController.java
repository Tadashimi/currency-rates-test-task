package com.ukolpakova.soap.controller;

import com.ukolpakova.soap.response.CurrencyRatesResponse;
import com.ukolpakova.soap.service.RatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Declare the main application controller.
 */
@RestController
@RequestMapping("/api/rates")
public class RatesController {

    private final RatesService ratesService;

    @Autowired
    public RatesController(RatesService ratesService) {
        this.ratesService = ratesService;
    }

    @GetMapping
    public List<CurrencyRatesResponse> getCurrencyRates() {
        return ratesService.getCurrencyRates();
    }
}
