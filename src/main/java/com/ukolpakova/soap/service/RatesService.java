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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class RatesService {

    private final Logger logger = LoggerFactory.getLogger(RatesService.class);

    private FxRatesSoap fxRatesSoap;

    public RatesService() {
    }

    @Autowired
    public RatesService(FxRates soapFxRatesService) {
        this.fxRatesSoap = soapFxRatesService.getFxRatesSoap();
    }

    public List<CurrencyRatesResponse> getCurrencyRates() {
        Map<String, Currency> currenciesMap = getCurrencyMap();
        List<CurrencyRate> currentEUFxRates = getCurrentEUFxRates();
        return mergeCurrenciesDataToCurrencyRatesResponse(currenciesMap, currentEUFxRates);
    }

    private Map<String, Currency> getCurrencyMap() {
        GetCurrencyListResponse.GetCurrencyListResult currencyList = fxRatesSoap.getCurrencyList();
        if (Objects.isNull(currencyList)) {
            logger.error("Soap server return null for currency list");
            throw new EntityNotFoundException("Currency list is not found");
        }
        CurrencyParser currencyParser = getCurrencyParser(currencyList);
        try {
            return currencyParser.parseCurrencyList();
        } catch (RuntimeException ex) {
            logger.error("Currency list parsing failed. See details in exception: ", ex);
            throw new CurrencyParseException("Error while parsing currency list", ex);
        }
    }

    private List<CurrencyRate> getCurrentEUFxRates() {
        GetCurrentFxRatesResponse.GetCurrentFxRatesResult currentEUFxRates = fxRatesSoap.getCurrentFxRates("EU");
        if (Objects.isNull(currentEUFxRates)) {
            logger.error("Soap server return null for currency rates");
            throw new EntityNotFoundException("Currency rates are not found");
        }
        CurrencyRatesParser currencyRateParser = getCurrencyRateParser(currentEUFxRates);
        try {
            return currencyRateParser.parseCurrencyRates();
        } catch (RuntimeException ex) {
            logger.error("Currency rates parsing failed. See details in exception: ", ex);
            throw new CurrencyParseException("Error while parsing currencies rates", ex);
        }
    }

    private List<CurrencyRatesResponse> mergeCurrenciesDataToCurrencyRatesResponse(Map<String, Currency> currenciesMap,
                                                                                   List<CurrencyRate> currentEUFxRates) {
        List<CurrencyRatesResponse> responses = new ArrayList<>();
        for (CurrencyRate currencyRate : currentEUFxRates) {
            Currency currency = currenciesMap.get(currencyRate.getCurrencyCode());
            if (Objects.isNull(currency)) {
                logger.error("Could not find the data for currency code {}", currencyRate.getCurrencyCode());
                throw new EntityNotFoundException("Currency info is not found for currency " + currencyRate.getCurrencyCode());
            }
            responses.add(new CurrencyRatesResponse(currency, currencyRate.getCurrencyAmount()));
        }
        return responses;
    }

    protected CurrencyParser getCurrencyParser(GetCurrencyListResponse.GetCurrencyListResult currencyList) {
        return new CurrencyParser(currencyList);
    }

    protected CurrencyRatesParser getCurrencyRateParser(GetCurrentFxRatesResponse.GetCurrentFxRatesResult currentEUFxRates) {
        return new CurrencyRatesParser(currentEUFxRates);
    }
}
