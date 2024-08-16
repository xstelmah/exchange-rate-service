package com.stelmah.steamanalysis.exchangerate.service;

import com.stelmah.steamanalysis.exchangerate.dto.ExchangeRateSnapshotDto;
import com.stelmah.steamanalysis.exchangerate.entity.ExchangeRateSnapshot;

public interface ExchangeRateSnapshotService {

    ExchangeRateSnapshot saveSnapshotWithoutRates(ExchangeRateSnapshotDto exchangeRateSnapshotDto);
}
