package com.stelmah.steamanalysis.exchangerate.service;

import com.stelmah.steamanalysis.exchangerate.dto.ExchangeRateSnapshotDto;
import com.stelmah.steamanalysis.exchangerate.entity.ExchangeRateSnapshot;

import java.util.Optional;

public interface ExchangeRateSnapshotService {

    Optional<ExchangeRateSnapshot> findLatestByVendor(String vendor);

    ExchangeRateSnapshot saveSnapshotWithoutRates(ExchangeRateSnapshotDto exchangeRateSnapshotDto);
}
