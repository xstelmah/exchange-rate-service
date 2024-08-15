package com.stelmah.steamanalysis.exchangerate.external.exchangerateapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Data
public class ExchangeRateApiResponseDto {

    @JsonProperty("result")
    private String result;

    @JsonProperty("documentation")
    private String documentation;

    @JsonProperty("terms_of_use")
    private String termsOfUse;

    @JsonProperty("time_last_update_unix")
    private long timeLastUpdateUnix;

    @JsonProperty("time_last_update_utc")
    private String timeLastUpdateUtc;

    @JsonProperty("time_next_update_unix")
    private long timeNextUpdateUnix;

    @JsonProperty("time_next_update_utc")
    private String timeNextUpdateUtc;

    @JsonProperty("base_code")
    private String baseCode;

    @JsonProperty("error-type") // why not a snake_case??? Lol
    private String errorType;

    @JsonProperty("conversion_rates")
    private Map<String, BigDecimal> conversionRates = new HashMap<>();

}