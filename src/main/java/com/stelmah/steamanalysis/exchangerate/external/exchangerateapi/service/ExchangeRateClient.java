package com.stelmah.steamanalysis.exchangerate.external.exchangerateapi.service;

import com.stelmah.steamanalysis.exchangerate.external.exchangerateapi.config.ExchangeRateFeignConfig;
import com.stelmah.steamanalysis.exchangerate.external.exchangerateapi.dto.ExchangeRateApiResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "exchangeRateClient", url = "${exchange.rate.api.url}", configuration = ExchangeRateFeignConfig.class)
public interface ExchangeRateClient {

    @GetMapping("/API_KEY/latest/{currency}")
    ExchangeRateApiResponseDto getLatestExchangeRates(
            @PathVariable("currency") String baseCurrency
    );
}
