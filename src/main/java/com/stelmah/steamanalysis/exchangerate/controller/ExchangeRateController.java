package com.stelmah.steamanalysis.exchangerate.controller;

import com.stelmah.steamanalysis.exchangerate.dto.ExchangeRateDto;
import com.stelmah.steamanalysis.exchangerate.dto.ExchangeRateSnapshotDto;
import com.stelmah.steamanalysis.exchangerate.facade.ExchangeRateFacade;
import com.stelmah.steamanalysis.exchangerate.service.ExchangeRateService;
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

    private final ExchangeRateService exchangeRateService;
    private final ExchangeRateFacade exchangeRateFacade;

    @GetMapping("/pair/{from}/{to}/latest")
    public ExchangeRateDto getLatestExchangeRate(
            @PathVariable("from") String baseCurrency,
            @PathVariable("to") String targetCurrency
    ) {
        return exchangeRateService.getLatestRates(baseCurrency, targetCurrency);
    }

    @PostMapping("/fetch")
    public ResponseEntity<ExchangeRateSnapshotDto> fetchLatestExchangeRate() {
        var snapshotDto = exchangeRateFacade.fetchAndSaveExchangeRates();

        return ResponseEntity.ok(snapshotDto);
    }

}
