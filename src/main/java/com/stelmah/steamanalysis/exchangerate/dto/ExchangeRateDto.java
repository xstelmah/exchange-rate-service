package com.stelmah.steamanalysis.exchangerate.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ExchangeRateDto {

    private String baseCurrency;
    private String targetCurrency;
    private BigDecimal rate;
    private LocalDateTime timestamp;
}
