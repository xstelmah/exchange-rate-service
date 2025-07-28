package com.stelmah.steamanalysis.exchangerate.external.exchangerateapi.service;

import com.stelmah.steamanalysis.exchangerate.dto.ExchangeRateSnapshotDto;
import com.stelmah.steamanalysis.exchangerate.external.exchangerateapi.exception.ExchangeRateApiException;

import java.time.LocalDate;

public interface ExchangeRateApiService {

    ExchangeRateSnapshotDto fetchLatestExchangeRates(String baseCurrency) throws ExchangeRateApiException;

    ExchangeRateSnapshotDto fetchHistoricalExchangeRates(String baseCurrency, LocalDate date) throws ExchangeRateApiException;
}
