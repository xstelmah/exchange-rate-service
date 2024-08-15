package com.stelmah.steamanalysis.exchangerate.service.impl;

import com.stelmah.steamanalysis.exchangerate.dto.ExchangeRateDto;
import com.stelmah.steamanalysis.exchangerate.entity.ExchangeRate;
import com.stelmah.steamanalysis.exchangerate.entity.ExchangeRateSnapshot;
import com.stelmah.steamanalysis.exchangerate.external.exchangerateapi.dto.ExchangeRateApiResponseDto;
import com.stelmah.steamanalysis.exchangerate.external.exchangerateapi.service.ExchangeRateClient;
import com.stelmah.steamanalysis.exchangerate.mapper.ExchangeRateMapper;
import com.stelmah.steamanalysis.exchangerate.repository.ExchangeRateRepository;
import com.stelmah.steamanalysis.exchangerate.repository.ExchangeRateSnapshotRepository;
import com.stelmah.steamanalysis.exchangerate.service.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExchangeRateServiceImpl implements ExchangeRateService {

    private static final String EXCHANGE_RATE_VENDOR_NAME = "EXCHANGE_RATE";

    private final ExchangeRateClient exchangeRateClient;

    private final ExchangeRateMapper exchangeRateMapper;

    private final ExchangeRateRepository exchangeRateRepository;
    private final ExchangeRateSnapshotRepository exchangeRateSnapshotRepository;

    @Override
    public ExchangeRateDto getLatestRates(String baseCurrency, String targetCurrency) {
        return exchangeRateRepository.findLatestByBaseAndTargetCurrencies(baseCurrency, targetCurrency, PageRequest.of(0, 1))
                .map(exchangeRateMapper::toDto)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
    }

    @Override
    @Transactional
    public void fetchLatestExchangeRates() {
        log.debug("Fetching latest exchange rates");
        var requestCurrency = "USD";
        var result = exchangeRateClient.getLatestExchangeRates(requestCurrency);

        validateResult(result);

        var snapshot = new ExchangeRateSnapshot();
        snapshot.setServerTimestamp(LocalDateTime.now());
        var vendorTimestamp = toLocalDateTime(result.getTimeLastUpdateUnix());
        snapshot.setVendorTimestamp(vendorTimestamp);
        snapshot.setVendor(EXCHANGE_RATE_VENDOR_NAME);
        snapshot = exchangeRateSnapshotRepository.save(snapshot);

        for (var conversionRate: result.getConversionRates().entrySet()) {
            var currency = conversionRate.getKey();
            var rate = conversionRate.getValue();

            if (BigDecimal.ZERO.equals(rate)) {
                log.warn("Skipping conversion rate for pair {}-{}", requestCurrency, conversionRate.getKey());
                continue;
            }

            var exchangeRate = new ExchangeRate();
            exchangeRate.setBaseCurrency(requestCurrency);
            exchangeRate.setTargetCurrency(currency);
            exchangeRate.setTimestamp(vendorTimestamp);
            exchangeRate.setSnapshot(snapshot);
            exchangeRate.setRate(rate);

            exchangeRateRepository.save(exchangeRate);
        }
        log.info("Fetched latest exchange rates, snapshot({}), number of rates({})",
                snapshot.getId(), result.getConversionRates().size());
    }

    private void validateResult(ExchangeRateApiResponseDto result) {
        if (result.getResult() == null || !"success".equals(result.getResult())) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        }
    }

    private static LocalDateTime toLocalDateTime(long unixTimestamp) {
        return LocalDateTime.ofInstant(
                Instant.ofEpochSecond(unixTimestamp),
                ZoneId.systemDefault()
        );
    }
}