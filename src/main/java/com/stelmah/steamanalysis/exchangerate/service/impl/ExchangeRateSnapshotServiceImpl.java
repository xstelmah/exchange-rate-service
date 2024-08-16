package com.stelmah.steamanalysis.exchangerate.service.impl;

import com.stelmah.steamanalysis.exchangerate.dto.ExchangeRateSnapshotDto;
import com.stelmah.steamanalysis.exchangerate.entity.ExchangeRateSnapshot;
import com.stelmah.steamanalysis.exchangerate.mapper.ExchangeRateSnapshotMapper;
import com.stelmah.steamanalysis.exchangerate.repository.ExchangeRateSnapshotRepository;
import com.stelmah.steamanalysis.exchangerate.service.ExchangeRateSnapshotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExchangeRateSnapshotServiceImpl implements ExchangeRateSnapshotService {

    private final ExchangeRateSnapshotMapper exchangeRateSnapshotMapper;
    private final ExchangeRateSnapshotRepository exchangeRateSnapshotRepository;

    @Override
    @Transactional
    public ExchangeRateSnapshot saveSnapshotWithoutRates(ExchangeRateSnapshotDto snapshotDto) {
        var snapshot = exchangeRateSnapshotMapper.toEntity(snapshotDto);
        snapshot.setId(null);

        snapshot = exchangeRateSnapshotRepository.save(snapshot);
        log.info("Saved exchange rate snapshot({}) for vendor({})", snapshot.getId(), snapshot.getVendor());
        return snapshot;
    }
}