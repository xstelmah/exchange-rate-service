package com.stelmah.steamanalysis.exchangerate.service.impl;

import com.stelmah.steamanalysis.exchangerate.dto.ExchangeRateDto;
import com.stelmah.steamanalysis.exchangerate.entity.ExchangeRate;
import com.stelmah.steamanalysis.exchangerate.entity.ExchangeRateSnapshot;
import com.stelmah.steamanalysis.exchangerate.mapper.ExchangeRateMapper;
import com.stelmah.steamanalysis.exchangerate.repository.ExchangeRateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.client.HttpClientErrorException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ExchangeRateServiceImplTest {

    @Mock
    private ExchangeRateMapper exchangeRateMapper;

    @Mock
    private ExchangeRateRepository exchangeRateRepository;

    @InjectMocks
    private ExchangeRateServiceImpl exchangeRateService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetLatestRatesSuccess() {
        // Given
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setRate(new BigDecimal("0.85"));

        ExchangeRateDto exchangeRateDto = ExchangeRateDto.builder()
                .baseCurrency("USD")
                .targetCurrency("EUR")
                .rate(new BigDecimal("0.85"))
                .build();

        when(exchangeRateRepository.findAnyLatestExchangeRateBetweenCurrencies(anyString(), anyString(), any(PageRequest.class)))
                .thenReturn(Optional.of(exchangeRate));
        when(exchangeRateMapper.toDto(exchangeRate)).thenReturn(exchangeRateDto);

        // When
        ExchangeRateDto result = exchangeRateService.getLatestRates("USD", "EUR");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getBaseCurrency()).isEqualTo("USD");
        assertThat(result.getTargetCurrency()).isEqualTo("EUR");
        assertThat(result.getRate()).isEqualTo(new BigDecimal("0.85"));
    }

    @Test
    void testGetLatestRatesCrossRate() {
        // Given
        ExchangeRate mainToBaseRate = new ExchangeRate();
        mainToBaseRate.setRate(new BigDecimal("3.80"));
        mainToBaseRate.setTimestamp(LocalDateTime.now().minusDays(1));

        ExchangeRate mainToTargetRate = new ExchangeRate();
        mainToTargetRate.setRate(new BigDecimal("75.00"));
        mainToTargetRate.setTimestamp(LocalDateTime.now());

        when(exchangeRateRepository.findAnyLatestExchangeRateBetweenCurrencies(anyString(), eq("PLN"), any(PageRequest.class)))
                .thenReturn(Optional.empty());
        when(exchangeRateRepository.findLatestByBaseAndTargetCurrencies(eq("USD"), eq("PLN"), any(PageRequest.class)))
                .thenReturn(Optional.of(mainToBaseRate));
        when(exchangeRateRepository.findLatestByBaseAndTargetCurrencies(eq("USD"), eq("RUB"), any(PageRequest.class)))
                .thenReturn(Optional.of(mainToTargetRate));

        // When
        ExchangeRateDto result = exchangeRateService.getLatestRates("PLN", "RUB");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getBaseCurrency()).isEqualTo("PLN");
        assertThat(result.getTargetCurrency()).isEqualTo("RUB");
        assertThat(result.getRate()).isEqualByComparingTo(new BigDecimal("19.7368"));
    }

    @Test
    void testGetLatestRatesCrossRateWithZeroRate() {
        // Given
        ExchangeRate mainToBaseRate = new ExchangeRate();
        mainToBaseRate.setRate(BigDecimal.ZERO);
        mainToBaseRate.setTimestamp(LocalDateTime.now().minusDays(1));

        ExchangeRate mainToTargetRate = new ExchangeRate();
        mainToTargetRate.setRate(new BigDecimal("75.00"));
        mainToTargetRate.setTimestamp(LocalDateTime.now());

        when(exchangeRateRepository.findAnyLatestExchangeRateBetweenCurrencies(anyString(), eq("PLN"), any(PageRequest.class)))
                .thenReturn(Optional.empty());
        when(exchangeRateRepository.findLatestByBaseAndTargetCurrencies(eq("USD"), eq("PLN"), any(PageRequest.class)))
                .thenReturn(Optional.of(mainToBaseRate));
        when(exchangeRateRepository.findLatestByBaseAndTargetCurrencies(eq("USD"), eq("RUB"), any(PageRequest.class)))
                .thenReturn(Optional.of(mainToTargetRate));

        // When
        assertThatThrownBy(() -> exchangeRateService.getLatestRates("PLN", "RUB"))
                .isInstanceOf(HttpClientErrorException.class)
                .hasMessageContaining("Exchange rate for pair (PLN/RUB) not found");;
    }

    @Test
    void testGetLatestRatesNotFound() {
        // Given
        when(exchangeRateRepository.findAnyLatestExchangeRateBetweenCurrencies(anyString(), anyString(), any(PageRequest.class)))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> exchangeRateService.getLatestRates("USD", "EUR"))
                .isInstanceOf(HttpClientErrorException.class)
                .hasMessageContaining("Exchange rate for pair (USD/EUR) not found");
    }

    @Test
    void testSaveAllSuccess() {
        // Given
        ExchangeRateSnapshot snapshot = new ExchangeRateSnapshot();
        ExchangeRateDto exchangeRateDto = ExchangeRateDto.builder()
                .baseCurrency("USD")
                .targetCurrency("EUR")
                .rate(new BigDecimal("0.85"))
                .build();

        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setRate(new BigDecimal("0.85"));
        exchangeRate.setSnapshot(snapshot);

        when(exchangeRateMapper.toEntity(exchangeRateDto)).thenReturn(exchangeRate);
        when(exchangeRateRepository.saveAll(anyList())).thenReturn(List.of(exchangeRate));

        // When
        List<ExchangeRate> result = exchangeRateService.saveAll(snapshot, List.of(exchangeRateDto));

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRate()).isEqualTo(new BigDecimal("0.85"));
        assertThat(result.get(0).getSnapshot()).isEqualTo(snapshot);
        verify(exchangeRateRepository, times(1)).saveAll(anyList());
    }

    @Test
    void testSwapExchangeRateIfNeeded() {
        // Given
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setRate(new BigDecimal("0.85"));

        ExchangeRateDto exchangeRateDto = ExchangeRateDto.builder()
                .baseCurrency("USD")
                .targetCurrency("EUR")
                .rate(new BigDecimal("0.85"))
                .build();

        when(exchangeRateRepository.findAnyLatestExchangeRateBetweenCurrencies(anyString(), anyString(), any(PageRequest.class)))
                .thenReturn(Optional.of(exchangeRate));
        when(exchangeRateMapper.toDto(exchangeRate)).thenReturn(exchangeRateDto);

        // When
        ExchangeRateDto result = exchangeRateService.getLatestRates("EUR", "USD");


        // Then
        assertThat(result).isNotNull();
        assertThat(result.getBaseCurrency()).isEqualTo("EUR");
        assertThat(result.getTargetCurrency()).isEqualTo("USD");
        assertThat(result.getRate()).isEqualByComparingTo(new BigDecimal("1.1765"));
    }

    @Test
    void testSwapExchangeRateIfNeededWithZeroRate() {
        // Given
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setRate(new BigDecimal("0"));

        ExchangeRateDto exchangeRateDto = ExchangeRateDto.builder()
                .baseCurrency("USD")
                .targetCurrency("EUR")
                .rate(new BigDecimal("0"))
                .build();

        // When
        when(exchangeRateRepository.findAnyLatestExchangeRateBetweenCurrencies(anyString(), anyString(), any(PageRequest.class)))
                .thenReturn(Optional.of(exchangeRate));
        when(exchangeRateMapper.toDto(exchangeRate)).thenReturn(exchangeRateDto);


        // Then
        assertThatThrownBy(() -> exchangeRateService.getLatestRates("EUR", "USD"))
                .isInstanceOf(HttpClientErrorException.class)
                .hasMessageContaining("Exchange rate for pair (EUR/USD) not found");
    }

    @Test
    void testFindCrossRateUsingMainCurrencySuccess() {
        // Given
        ExchangeRate mainToBaseRate = new ExchangeRate();
        mainToBaseRate.setRate(new BigDecimal("3.80"));
        mainToBaseRate.setTimestamp(LocalDateTime.now().minusDays(1));

        ExchangeRate mainToTargetRate = new ExchangeRate();
        mainToTargetRate.setRate(new BigDecimal("75.00"));
        mainToTargetRate.setTimestamp(LocalDateTime.now());

        when(exchangeRateRepository.findLatestByBaseAndTargetCurrencies(eq("USD"), eq("PLN"), any(PageRequest.class)))
                .thenReturn(Optional.of(mainToBaseRate));
        when(exchangeRateRepository.findLatestByBaseAndTargetCurrencies(eq("USD"), eq("RUB"), any(PageRequest.class)))
                .thenReturn(Optional.of(mainToTargetRate));

        // When
        ExchangeRateDto result = exchangeRateService.getLatestRates("PLN", "RUB");

        // Then
        assertThat(result.getBaseCurrency()).isEqualTo("PLN");
        assertThat(result.getTargetCurrency()).isEqualTo("RUB");
        assertThat(result.getRate()).isEqualByComparingTo(new BigDecimal("19.7368"));
    }

}
