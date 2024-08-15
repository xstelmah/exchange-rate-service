package com.stelmah.steamanalysis.exchangerate.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Table(name = "t_exchange_rate_snapshot")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRateSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime vendorTimestamp;

    private LocalDateTime serverTimestamp;

    private String vendor;

    @OneToMany(mappedBy = "snapshot")
    private List<ExchangeRate> exchangeRates;
}
