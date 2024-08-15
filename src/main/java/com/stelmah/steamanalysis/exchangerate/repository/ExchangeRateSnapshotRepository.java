package com.stelmah.steamanalysis.exchangerate.repository;

import com.stelmah.steamanalysis.exchangerate.entity.ExchangeRateSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExchangeRateSnapshotRepository extends JpaRepository<ExchangeRateSnapshot, Long> {
}
