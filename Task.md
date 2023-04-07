# Coding assignment
Adapter service for FX Rates provided by "Lietuvos Bankas" .
## Description
Write a simple adapter service which integrates with "Lietuvos Bankas":
1. Consume "Lietuvos Bankas" FX Rates SOAP Web Service
   https://www.lb.lt/webservices/fxrates/FxRates.asmx?WSDL .
   Web Service is described in https://www.lb.lt/webservices/FxRates/en/ .
2. Expose REST API which returns all available currencies including:
   1. Currency name in English and Lithuanian.
   2. Currency acronym (e.g. EUR, SEK).
   3. Current Rate against EUR.

## Requirements
- Use Java 8 or later. Make sure to use Java 8+ features.
- Use Spring Boot 2+ or Spring 5+.
- Add unit tests.
- Provide README.md with:
  - an example of REST API request to invoke your API.
  - setup instructions, if your solution requires additional work to set up and run.
- Follow coding best practices and make sure that provided code is clean, readable and well-structured.
- Publish your code to public source control system (e.g. Github, Bitbucket).