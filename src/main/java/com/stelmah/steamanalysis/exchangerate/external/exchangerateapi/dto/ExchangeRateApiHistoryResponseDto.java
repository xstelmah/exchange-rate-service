package com.stelmah.steamanalysis.exchangerate.external.exchangerateapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Data
public class ExchangeRateApiHistoryResponseDto {

    @JsonProperty("result")
    private String result;

    @JsonProperty("documentation")
    private String documentation;

    @JsonProperty("terms_of_use")
    private String termsOfUse;

    @JsonProperty("year")
    private int year;

    @JsonProperty("month")
    private int month;

    @JsonProperty("day")
    private int day;

    @JsonProperty("base_code")
    private String baseCode;

    @JsonProperty("error-type") // why not a snake_case??? Lol
    private String errorType;

    @JsonProperty("conversion_rates")
    private Map<String, BigDecimal> conversionRates = new HashMap<>();

}