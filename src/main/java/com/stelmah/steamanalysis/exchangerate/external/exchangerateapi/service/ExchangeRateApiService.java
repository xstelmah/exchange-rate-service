package com.stelmah.steamanalysis.exchangerate.external.exchangerateapi.service;

import com.stelmah.steamanalysis.exchangerate.dto.ExchangeRateSnapshotDto;
import com.stelmah.steamanalysis.exchangerate.external.exchangerateapi.exception.ExchangeRateApiException;

public interface ExchangeRateApiService {

    ExchangeRateSnapshotDto fetchLatestExchangeRates(String baseCurrency) throws ExchangeRateApiException;
}
