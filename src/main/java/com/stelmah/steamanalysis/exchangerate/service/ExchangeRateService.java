package com.stelmah.steamanalysis.exchangerate.service;

import com.stelmah.steamanalysis.exchangerate.dto.ExchangeRateDto;
import com.stelmah.steamanalysis.exchangerate.entity.ExchangeRate;
import com.stelmah.steamanalysis.exchangerate.entity.ExchangeRateSnapshot;

import java.util.List;

public interface ExchangeRateService {

    ExchangeRateDto getLatestRates(String baseCurrency, String targetCurrency);

    List<ExchangeRate> saveAll(ExchangeRateSnapshot snapshot, List<ExchangeRateDto> exchangeRateDtos);
}
