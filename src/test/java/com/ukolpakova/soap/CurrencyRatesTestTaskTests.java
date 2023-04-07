package com.ukolpakova.soap;

import com.ukolpakova.soap.controller.RatesController;
import com.ukolpakova.soap.service.RatesService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CurrencyRatesTestTaskTests {
    @Autowired
    RatesService ratesService;

    @Autowired
    RatesController controller;

    @Test
    void contextLoads() {
        Assertions.assertNotNull(ratesService);
        Assertions.assertNotNull(controller);
    }
}
