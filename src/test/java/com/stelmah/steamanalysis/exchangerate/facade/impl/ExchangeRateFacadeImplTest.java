package com.stelmah.steamanalysis.exchangerate.facade.impl;

import com.stelmah.steamanalysis.exchangerate.dto.ExchangeRateSnapshotDto;
import com.stelmah.steamanalysis.exchangerate.entity.ExchangeRate;
import com.stelmah.steamanalysis.exchangerate.entity.ExchangeRateSnapshot;
import com.stelmah.steamanalysis.exchangerate.external.exchangerateapi.service.ExchangeRateApiService;
import com.stelmah.steamanalysis.exchangerate.service.ExchangeRateService;
import com.stelmah.steamanalysis.exchangerate.service.ExchangeRateSnapshotService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ExchangeRateFacadeImplTest {


    @Mock
    private ExchangeRateService exchangeRateService;

    @Mock
    private ExchangeRateSnapshotService exchangeRateSnapshotService;

    @Mock
    private ExchangeRateApiService exchangeRateApiService;

    @InjectMocks
    private ExchangeRateFacadeImpl exchangeRateFacade;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFetchAndSaveExchangeRatesSuccess() {
        // Given
        ExchangeRateSnapshotDto mockSnapshotDto = new ExchangeRateSnapshotDto();
        ExchangeRateSnapshot mockSnapshot = new ExchangeRateSnapshot();
        mockSnapshot.setExchangeRates(List.of(
                ExchangeRate.builder().targetCurrency("EUR").rate(BigDecimal.valueOf(0.85)).build(),
                ExchangeRate.builder().targetCurrency("GBP").rate(BigDecimal.valueOf(0.75)).build()
        ));

        when(exchangeRateApiService.fetchLatestExchangeRates(eq("USD"))).thenReturn(mockSnapshotDto);
        when(exchangeRateSnapshotService.saveSnapshotWithoutRates(any(ExchangeRateSnapshotDto.class)))
                .thenReturn(mockSnapshot);
        when(exchangeRateService.saveAll(any(ExchangeRateSnapshot.class), anyList()))
                .thenReturn(mockSnapshot.getExchangeRates());

        // When
        ExchangeRateSnapshotDto result = exchangeRateFacade.fetchAndSaveExchangeRates();

        // Then
        verify(exchangeRateApiService, times(1)).fetchLatestExchangeRates(eq("USD"));
        verify(exchangeRateSnapshotService, times(1)).saveSnapshotWithoutRates(mockSnapshotDto);
        verify(exchangeRateService, times(1)).saveAll(mockSnapshot, mockSnapshotDto.getExchangeRates());

        assertThat(result).isEqualTo(mockSnapshotDto);
    }

}