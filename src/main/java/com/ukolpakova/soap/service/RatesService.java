package com.ukolpakova.soap.service;

import com.ukolpakova.soap.exception.CurrencyParseException;
import com.ukolpakova.soap.exception.EntityNotFoundCurrencyException;
import com.ukolpakova.soap.model.Currency;
import com.ukolpakova.soap.model.CurrencyRate;
import com.ukolpakova.soap.parser.CurrencyParser;
import com.ukolpakova.soap.parser.CurrencyRatesParser;
import com.ukolpakova.soap.response.CurrencyRatesResponse;
import com.ukolpakova.soap.wsclient.generated.FxRates;
import com.ukolpakova.soap.wsclient.generated.FxRatesSoap;
import com.ukolpakova.soap.wsclient.generated.GetCurrencyListResponse;
import com.ukolpakova.soap.wsclient.generated.GetCurrentFxRatesResponse;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.ukolpakova.soap.localization.EntityNotFoundErrorMessageConstant.CURRENCY_INFO_NOT_FOUND;
import static com.ukolpakova.soap.localization.ParseCurrencyListErrorMessageConstant.CURRENCY_LIST_GENERAL_ERROR;
import static com.ukolpakova.soap.localization.ParseCurrencyRatesErrorMessageConstant.CURRENCY_RATES_GENERAL_ERROR;

/**
 * Service adapter to convert the data from SOAP service to REST response
 */
@Service
public class RatesService {

    private final Logger logger = LoggerFactory.getLogger(RatesService.class);
    private final CurrencyParser currencyParser;
    private final CurrencyRatesParser currencyRatesParser;

    private FxRatesSoap fxRatesSoap;

    public RatesService(CurrencyParser currencyParser, CurrencyRatesParser currencyRatesParser) {
        this.currencyParser = currencyParser;
        this.currencyRatesParser = currencyRatesParser;
    }

    @PostConstruct
    public void initSoapServer() {
        fxRatesSoap = new FxRates().getFxRatesSoap();
    }

    public List<CurrencyRatesResponse> getCurrencyRates() {
        Map<String, Currency> currenciesMap = getCurrencyMap();
        List<CurrencyRate> currentEUFxRates = getCurrentEUFxRates();
        return mergeCurrenciesDataToCurrencyRatesResponse(currenciesMap, currentEUFxRates);
    }

    private Map<String, Currency> getCurrencyMap() {
        logger.debug("Getting the currency list from SOAP server");
        GetCurrencyListResponse.GetCurrencyListResult currencyList = fxRatesSoap.getCurrencyList();
        try {
            return currencyParser.parseCurrencyList(currencyList);
        } catch (RuntimeException ex) {
            logger.error("Currency list parsing failed. See details in exception: ", ex);
            throw new CurrencyParseException(CURRENCY_LIST_GENERAL_ERROR);
        }
    }

    private List<CurrencyRate> getCurrentEUFxRates() {
        logger.debug("Getting the currency rates from SOAP server");
        GetCurrentFxRatesResponse.GetCurrentFxRatesResult currentEUFxRates = fxRatesSoap.getCurrentFxRates("EU");
        try {
            return currencyRatesParser.parseCurrencyRates(currentEUFxRates);
        } catch (RuntimeException ex) {
            logger.error("Currency rates parsing failed. See details in exception: ", ex);
            throw new CurrencyParseException(CURRENCY_RATES_GENERAL_ERROR);
        }
    }

    private List<CurrencyRatesResponse> mergeCurrenciesDataToCurrencyRatesResponse(Map<String, Currency> currenciesMap,
                                                                                   List<CurrencyRate> currentEUFxRates) {
        logger.debug("Merging parsed SOAP responses to currency rates response for REST");
        return currentEUFxRates.stream()
                .map(currencyRate -> createCurrencyRatesResponseFromCurrencyAndRate(currencyRate,
                        currenciesMap.get(currencyRate.getCurrencyCode())))
                .collect(Collectors.toList());
    }

    private CurrencyRatesResponse createCurrencyRatesResponseFromCurrencyAndRate(CurrencyRate currencyRate, Currency currency) {
        if (Objects.isNull(currency)) {
            String currencyCode = currencyRate.getCurrencyCode();
            logger.error("Could not find the data for currency code {}", currencyCode);
            throw new EntityNotFoundCurrencyException(CURRENCY_INFO_NOT_FOUND);
        }
        return new CurrencyRatesResponse(currency, currencyRate.getCurrencyAmount());
    }
}
