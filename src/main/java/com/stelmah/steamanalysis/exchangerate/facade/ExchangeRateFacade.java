package com.stelmah.steamanalysis.exchangerate.facade;

import com.stelmah.steamanalysis.exchangerate.dto.ExchangeRateSnapshotDto;

public interface ExchangeRateFacade {

    ExchangeRateSnapshotDto fetchAndSaveExchangeRates();
}
