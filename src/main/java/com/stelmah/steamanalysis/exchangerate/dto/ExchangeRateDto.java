package com.stelmah.steamanalysis.exchangerate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExchangeRateDto {

    private String baseCurrency;
    private String targetCurrency;
    private BigDecimal rate;
    private LocalDateTime timestamp;
}
