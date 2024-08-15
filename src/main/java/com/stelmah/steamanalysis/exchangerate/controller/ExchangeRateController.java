package com.stelmah.steamanalysis.exchangerate.controller;

import com.stelmah.steamanalysis.exchangerate.dto.ExchangeRateDto;
import com.stelmah.steamanalysis.exchangerate.service.impl.ExchangeRateServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/exchange-rates")
@RequiredArgsConstructor
public class ExchangeRateController {

    private final ExchangeRateServiceImpl exchangeRateService;

    @GetMapping("/pair/{from}/{to}/latest")
    public ExchangeRateDto getLatestExchangeRate(
            @PathVariable("from") String baseCurrency,
            @PathVariable("to") String targetCurrency
    ) {
        return exchangeRateService.getLatestRates(baseCurrency, targetCurrency);
    }

    @PostMapping("/fetch")
    public ResponseEntity<?> fetchLatestExchangeRate() {
        exchangeRateService.fetchLatestExchangeRate();

        return ResponseEntity.noContent().build();
    }

}
