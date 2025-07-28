package com.stelmah.steamanalysis.exchangerate.controller;

import com.stelmah.steamanalysis.exchangerate.dto.ExchangeRateDto;
import com.stelmah.steamanalysis.exchangerate.dto.ExchangeRateSnapshotDto;
import com.stelmah.steamanalysis.exchangerate.facade.ExchangeRateFacade;
import com.stelmah.steamanalysis.exchangerate.service.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/exchange-rates")
@RequiredArgsConstructor
@Slf4j
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

    @GetMapping("/pair/{from}/{to}/{date}")
    public ExchangeRateDto getExchangeRateOnDate(
            @PathVariable("from") String baseCurrency,
            @PathVariable("to") String targetCurrency,
            @PathVariable("date") LocalDate date
    ) {
        return exchangeRateService.getLatestRatesOnDate(baseCurrency, targetCurrency, date);
    }

    @PostMapping("/fetch")
    @SneakyThrows
    public ResponseEntity<List<ExchangeRateSnapshotDto>> fetchLatestExchangeRate(
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to,
            @RequestParam(required = false) String currency,
            @RequestParam(required = false) long delay // delay in ms
    ) {
        if (from == null) {
            from = LocalDate.now();
        }
        if (to == null) {
            to = LocalDate.now();
        }
        if (currency == null) {
            currency = "USD";
        }
        if (from.isAfter(to)) {
            throw new ContextedRuntimeException("Bad request dates")
                    .addContextValue("from", from)
                    .addContextValue("to", to);
        }
        var snapshots = new ArrayList<ExchangeRateSnapshotDto>();
        var date = from;
        while (date.isBefore(to) || date.isEqual(to)) {
            try {
                Thread.sleep(delay);
                var snapshotDto = exchangeRateFacade.fetchAndSaveExchangeRates(date, currency);
                snapshotDto.setExchangeRates(null);
                snapshots.add(snapshotDto);
            } catch (Exception e) {
                log.error("", e);
            }
            date = date.plusDays(1);
        }

        return ResponseEntity.ok(snapshots);
    }

}
