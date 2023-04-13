package com.ukolpakova.soap.config;

import com.ukolpakova.soap.wsclient.generated.FxRates;
import com.ukolpakova.soap.wsclient.generated.FxRatesSoap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

@Configuration
public class SOAPClientConfig {
    @Bean
    public FxRatesSoap fxRatesSoap() {
        return new FxRates().getFxRatesSoap();
    }

    @Bean
    public SAXParser saxParser() throws ParserConfigurationException, SAXException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        return factory.newSAXParser();
    }
}
