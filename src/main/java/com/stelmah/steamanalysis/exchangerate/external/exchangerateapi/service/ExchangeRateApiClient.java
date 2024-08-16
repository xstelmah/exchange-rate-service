package com.stelmah.steamanalysis.exchangerate.external.exchangerateapi.service;

import com.stelmah.steamanalysis.exchangerate.external.exchangerateapi.config.ExchangeRateApiFeignConfig;
import com.stelmah.steamanalysis.exchangerate.external.exchangerateapi.dto.ExchangeRateApiResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "exchangeRateApiClient", url = "${exchange.rate.api.url}",
        configuration = ExchangeRateApiFeignConfig.class, primary = false)
public interface ExchangeRateApiClient {

    @GetMapping("/API_KEY/latest/{currency}")
    ExchangeRateApiResponseDto fetchLatestExchangeRates(
            @PathVariable("currency") String baseCurrency
    );
}
