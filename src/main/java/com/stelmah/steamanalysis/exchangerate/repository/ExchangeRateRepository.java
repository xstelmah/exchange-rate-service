package com.stelmah.steamanalysis.exchangerate.repository;

import com.stelmah.steamanalysis.exchangerate.entity.ExchangeRate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {

    @Query(""" 
            SELECT er FROM ExchangeRate er
            WHERE (er.baseCurrency = :currency1 AND er.targetCurrency = :currency2)
                OR (er.baseCurrency = :currency2 AND er.targetCurrency = :currency1)
            ORDER BY er.timestamp DESC
            """)
    Optional<ExchangeRate> findAnyLatestExchangeRateBetweenCurrencies(
            @Param("currency1") String currency1,
            @Param("currency2") String currency2,
            PageRequest pageRequest
    );

    @Query("""
            SELECT er FROM ExchangeRate er
            WHERE er.baseCurrency = :baseCurrency
                AND er.targetCurrency = :targetCurrency
            ORDER BY er.timestamp DESC
            """)
    Optional<ExchangeRate> findLatestByBaseAndTargetCurrencies(
            @Param("baseCurrency") String baseCurrency,
            @Param("targetCurrency") String targetCurrency,
            PageRequest pageRequest
    );
}