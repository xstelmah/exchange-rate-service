package com.stelmah.steamanalysis.exchangerate.service;

import com.stelmah.steamanalysis.exchangerate.dto.ExchangeRateDto;
import com.stelmah.steamanalysis.exchangerate.entity.ExchangeRate;
import com.stelmah.steamanalysis.exchangerate.entity.ExchangeRateSnapshot;

import java.time.LocalDate;
import java.util.List;

public interface ExchangeRateService {

    ExchangeRateDto getLatestRates(String baseCurrency, String targetCurrency);

    ExchangeRateDto getLatestRatesOnDate(String baseCurrency, String targetCurrency, LocalDate date);

    List<ExchangeRate> saveAll(ExchangeRateSnapshot snapshot, List<ExchangeRateDto> exchangeRateDtos);

}
