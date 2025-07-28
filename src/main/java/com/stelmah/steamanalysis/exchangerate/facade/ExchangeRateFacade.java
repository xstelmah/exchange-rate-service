package com.stelmah.steamanalysis.exchangerate.facade;

import com.stelmah.steamanalysis.exchangerate.dto.ExchangeRateSnapshotDto;

import java.time.LocalDate;

public interface ExchangeRateFacade {

    ExchangeRateSnapshotDto fetchAndSaveExchangeRates();

    ExchangeRateSnapshotDto fetchAndSaveExchangeRates(LocalDate date, String currency);
}
