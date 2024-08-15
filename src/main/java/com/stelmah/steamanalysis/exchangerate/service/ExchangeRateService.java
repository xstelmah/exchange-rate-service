package com.stelmah.steamanalysis.exchangerate.service;

import com.stelmah.steamanalysis.exchangerate.dto.ExchangeRateDto;

public interface ExchangeRateService {

    ExchangeRateDto getLatestRates(String baseCurrency, String targetCurrency);

    void fetchLatestExchangeRates();
}
