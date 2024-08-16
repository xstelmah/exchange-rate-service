package com.stelmah.steamanalysis.exchangerate.facade.impl;

import com.stelmah.steamanalysis.exchangerate.dto.ExchangeRateSnapshotDto;
import com.stelmah.steamanalysis.exchangerate.external.exchangerateapi.service.ExchangeRateApiClient;
import com.stelmah.steamanalysis.exchangerate.external.exchangerateapi.service.ExchangeRateApiService;
import com.stelmah.steamanalysis.exchangerate.facade.ExchangeRateFacade;
import com.stelmah.steamanalysis.exchangerate.service.ExchangeRateService;
import com.stelmah.steamanalysis.exchangerate.service.ExchangeRateSnapshotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExchangeRateFacadeImpl implements ExchangeRateFacade {

    private static final String BASE_REQUEST_CURRENCY = "USD";

    private final ExchangeRateService exchangeRateService;
    private final ExchangeRateSnapshotService exchangeRateSnapshotService;

    private final ExchangeRateApiService exchangeRateApiService;

    @Override
    @Transactional
    public ExchangeRateSnapshotDto fetchAndSaveExchangeRates() {
        log.info("Start fetching exchange rates for {} currency", BASE_REQUEST_CURRENCY);
        var snapshotDto = exchangeRateApiService.fetchLatestExchangeRates(BASE_REQUEST_CURRENCY);

        var snapshot =  exchangeRateSnapshotService.saveSnapshotWithoutRates(snapshotDto);

        var rates = exchangeRateService.saveAll(snapshot, snapshotDto.getExchangeRates());

        log.info("End fetching exchange rates for {} currency", BASE_REQUEST_CURRENCY);
        return snapshotDto; // dto may differ from entity due to in-service filtration, mb we should return snapshot id only
    }
}
