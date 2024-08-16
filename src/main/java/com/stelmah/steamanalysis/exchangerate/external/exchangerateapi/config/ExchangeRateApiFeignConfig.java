package com.stelmah.steamanalysis.exchangerate.external.exchangerateapi.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class ExchangeRateApiFeignConfig {

    @Value("${exchange.rate.api.key}")
    private String apiKey;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            // Modify the URL to include the API key
            String url = template.url();
            url = url.replace("API_KEY", apiKey);
            template.uri(url);
        };
    }
}
