package com.stelmah.steamanalysis.exchangerate.repository;

import com.stelmah.steamanalysis.exchangerate.entity.ExchangeRate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {

    @Query("SELECT er FROM ExchangeRate er " +
            "WHERE (er.baseCurrency = :baseCurrency AND er.targetCurrency = :targetCurrency) " +
            "OR (er.baseCurrency = :targetCurrency AND er.targetCurrency = :baseCurrency) " +
            "ORDER BY er.timestamp DESC")
    Optional<ExchangeRate> findLatestByBaseAndTargetCurrencies(
            @Param("baseCurrency") String baseCurrency,
            @Param("targetCurrency") String targetCurrency,
            PageRequest pageRequest
    );
}