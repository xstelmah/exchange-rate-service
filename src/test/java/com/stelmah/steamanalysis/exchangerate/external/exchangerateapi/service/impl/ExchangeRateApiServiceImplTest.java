package com.stelmah.steamanalysis.exchangerate.external.exchangerateapi.service.impl;

import com.stelmah.steamanalysis.exchangerate.dto.ExchangeRateSnapshotDto;
import com.stelmah.steamanalysis.exchangerate.external.exchangerateapi.dto.ExchangeRateApiResponseDto;
import com.stelmah.steamanalysis.exchangerate.external.exchangerateapi.exception.ExchangeRateApiException;
import com.stelmah.steamanalysis.exchangerate.external.exchangerateapi.mapper.ExchangeRateApiMapper;
import com.stelmah.steamanalysis.exchangerate.external.exchangerateapi.service.ExchangeRateApiClient;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import feign.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class ExchangeRateApiServiceImplTest {

    @Mock
    private ExchangeRateApiClient exchangeRateApiClient;

    @Mock
    private ExchangeRateApiMapper exchangeRateApiMapper;

    @InjectMocks
    private ExchangeRateApiServiceImpl exchangeRateApiService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFetchLatestExchangeRatesSuccess() throws ExchangeRateApiException {
        // Given
        ExchangeRateApiResponseDto mockResponse = new ExchangeRateApiResponseDto();
        mockResponse.setResult("success");
        mockResponse.setConversionRates(Map.of("USD", BigDecimal.ONE, "EUR", BigDecimal.valueOf(0.85)));
        ExchangeRateSnapshotDto expectedDto = new ExchangeRateSnapshotDto();

        when(exchangeRateApiClient.fetchLatestExchangeRates(anyString())).thenReturn(mockResponse);
        when(exchangeRateApiMapper.toExchangeRateSnapshotDto(mockResponse)).thenReturn(expectedDto);

        // When
        ExchangeRateSnapshotDto result = exchangeRateApiService.fetchLatestExchangeRates("USD");

        // Then
        assertThat(result).isEqualTo(expectedDto);
    }

    @Test
    void testFetchLatestExchangeRatesNullResponse() {
        // Given
        when(exchangeRateApiClient.fetchLatestExchangeRates(anyString())).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> exchangeRateApiService.fetchLatestExchangeRates("USD"))
                .isInstanceOf(ExchangeRateApiException.class)
                .hasMessageContaining("Response is null");
    }

    @Test
    void testFetchLatestExchangeRatesInvalidResult() {
        // Given
        ExchangeRateApiResponseDto mockResponse = new ExchangeRateApiResponseDto();
        mockResponse.setResult("failure");

        when(exchangeRateApiClient.fetchLatestExchangeRates(anyString())).thenReturn(mockResponse);

        // When & Then
        assertThatThrownBy(() -> exchangeRateApiService.fetchLatestExchangeRates("USD"))
                .isInstanceOf(ExchangeRateApiException.class)
                .hasMessageContaining("Failed response");
    }

    @Test
    void testFetchLatestExchangeRatesEmptyConversionRates() {
        // Given
        ExchangeRateApiResponseDto mockResponse = new ExchangeRateApiResponseDto();
        mockResponse.setResult("success");
        mockResponse.setConversionRates(Map.of());

        when(exchangeRateApiClient.fetchLatestExchangeRates(anyString())).thenReturn(mockResponse);

        // When & Then
        assertThatThrownBy(() -> exchangeRateApiService.fetchLatestExchangeRates("USD"))
                .isInstanceOf(ExchangeRateApiException.class)
                .hasMessageContaining("No conversion rates found");
    }

    @Test
    void testFetchLatestExchangeRatesFeignError() {
        // Mock the client to throw a 500 Internal Server Error exception
        when(exchangeRateApiClient.fetchLatestExchangeRates(anyString()))
                .thenThrow(createFeignException(500, "Internal Server Error", "Internal server error occurred"));

        // When & Then
        assertThatThrownBy(() -> exchangeRateApiService.fetchLatestExchangeRates("USD"))
                .isInstanceOf(ExchangeRateApiException.class)
                .hasMessageContaining("Failed to handle")
                .hasCauseInstanceOf(FeignException.class);
    }

    private FeignException createFeignException(int status, String reason, String body) {
        // Create a fake request
        Request request = Request.create(Request.HttpMethod.GET, "http://localhost", Collections.emptyMap(), null, new RequestTemplate());

        // Create a fake response with the specified status, reason, and body
        Response response = Response.builder()
                .request(request)
                .status(status)
                .reason(reason)
                .headers(Collections.emptyMap())
                .body(body, StandardCharsets.UTF_8)
                .build();

        // Create and return a FeignException using errorStatus
        return FeignException.errorStatus("fetchLatestExchangeRates", response);
    }
}