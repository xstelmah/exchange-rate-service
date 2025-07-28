package com.stelmah.steamanalysis.exchangerate.repository;

import com.stelmah.steamanalysis.exchangerate.entity.ExchangeRateSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExchangeRateSnapshotRepository extends JpaRepository<ExchangeRateSnapshot, Long> {

    Optional<ExchangeRateSnapshot> findFirstByVendorOrderByVendorTimestampDesc(String vendor);
}
