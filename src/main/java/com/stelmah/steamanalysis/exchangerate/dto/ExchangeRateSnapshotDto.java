package com.stelmah.steamanalysis.exchangerate.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class ExchangeRateSnapshotDto {

    private Long id;
    private LocalDateTime vendorTimestamp;
    private LocalDateTime serverTimestamp;
    private String vendor;
    private List<ExchangeRateDto> exchangeRates = new ArrayList<>();
}
