package com.stelmah.steamanalysis.exchangerate.service.impl;

import com.stelmah.steamanalysis.exchangerate.dto.ExchangeRateDto;
import com.stelmah.steamanalysis.exchangerate.entity.ExchangeRate;
import com.stelmah.steamanalysis.exchangerate.entity.ExchangeRateSnapshot;
import com.stelmah.steamanalysis.exchangerate.mapper.ExchangeRateMapper;
import com.stelmah.steamanalysis.exchangerate.repository.ExchangeRateRepository;
import com.stelmah.steamanalysis.exchangerate.service.ExchangeRateService;
import com.stelmah.steamanalysis.exchangerate.service.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExchangeRateServiceImpl implements ExchangeRateService {

    private static final String MAIN_SERVICE_CURRENCY = "USD";

    private final ExchangeRateMapper exchangeRateMapper;

    private final ExchangeRateRepository exchangeRateRepository;

    @Override
    public ExchangeRateDto getLatestRates(String baseCurrency, String targetCurrency) {
        return exchangeRateRepository.findAnyLatestExchangeRateBetweenCurrencies(baseCurrency, targetCurrency, PageRequest.of(0, 1))
                .map(exchangeRateMapper::toDto)
                .or(() -> findCrossRateUsingMainCurrency(MAIN_SERVICE_CURRENCY, baseCurrency, targetCurrency))
                .map(exchangeRateDto -> swapExchangeRateIfNeeded(exchangeRateDto, baseCurrency, targetCurrency))
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND,
                        "Exchange rate for pair (" + baseCurrency + "/" + targetCurrency + ") not found"));
    }

    @Override
    @Transactional
    public List<ExchangeRate> saveAll(ExchangeRateSnapshot snapshot, List<ExchangeRateDto> exchangeRateDtos) {
        var exchangeRates = exchangeRateDtos
                .stream()
                .filter(Objects::nonNull)
                .filter(exchangeRateDto -> !BigDecimal.ZERO.equals(exchangeRateDto.getRate()))
                .map(exchangeRateMapper::toEntity)
                .peek(exchangeRate -> exchangeRate.setSnapshot(snapshot))
                .toList();
        // save in batches
        exchangeRates = exchangeRateRepository.saveAll(exchangeRates);
        log.info("Added {} exchangeRates for snapshot({})", exchangeRates.size(), snapshot.getId());

        return exchangeRates;
    }

    private ExchangeRateDto swapExchangeRateIfNeeded(ExchangeRateDto dto, String baseCurrency, String targetCurrency) {
        // Check if the base and target currencies are swapped
        if (targetCurrency.equals(dto.getBaseCurrency()) && baseCurrency.equals(dto.getTargetCurrency())) {
            if (BigDecimal.ZERO.equals(dto.getRate())) {
                log.warn("Exchange rate for {} to {} is zero, skipping this entry.",
                        dto.getBaseCurrency(), dto.getTargetCurrency());
                return null;
            }
            var adjustedRate = BigDecimal.ONE.divide(dto.getRate(), 4, RoundingMode.HALF_UP);
            dto.setBaseCurrency(baseCurrency);
            dto.setTargetCurrency(targetCurrency);
            dto.setRate(adjustedRate);
        }
        return dto;
    }

    /**
     * Finds the exchange rate between two currencies by calculating the cross rate
     * using a main currency as an intermediary.
     * <p><strong>Example:</strong></p>
     * <pre>
     *     mainCurrency = "USD";
     *     baseCurrency = "PLN";
     *     targetCurrency = "RUB";
     *
     *     // Suppose the following exchange rates are available:
     *     // USD -> PLN = 3.80
     *     // USD -> RUB = 75.00
     *
     *     // The method will calculate the exchange rate between PLN and RUB as:
     *     // PLN -> RUB = (USD -> RUB) / (USD -> PLN) = 75.00 / 3.80 ≈ 19.7368
     *
     *     // The resulting ExchangeRateDto will have:
     *     // baseCurrency = "PLN"
     *     // targetCurrency = "RUB"
     *     // rate ≈ 19.7368
     *     // timestamp = max(timestamp of USD -> PLN, timestamp of USD -> RUB)
     * </pre>
     */
    private Optional<ExchangeRateDto> findCrossRateUsingMainCurrency(
            String mainCurrency,
            String baseCurrency,
            String targetCurrency) {
        Optional<ExchangeRate> mainToBaseRate = exchangeRateRepository.findLatestByBaseAndTargetCurrencies(
                mainCurrency, baseCurrency, PageRequest.of(0, 1));

        Optional<ExchangeRate> mainToTargetRate = exchangeRateRepository.findLatestByBaseAndTargetCurrencies(
                mainCurrency, targetCurrency, PageRequest.of(0, 1));

        if (mainToBaseRate.isEmpty() || mainToTargetRate.isEmpty()) {
            return Optional.empty();
        }

        var baseExchangeRate = mainToBaseRate.get();
        var targetExchangeRate = mainToTargetRate.get();

        var baseRate = baseExchangeRate.getRate();
        var targetRate = targetExchangeRate.getRate();

        if (BigDecimal.ZERO.equals(baseRate)) {
            log.warn("Exchange rate({}) has zero rate, skipping this entry.", baseExchangeRate.getId());
            return Optional.empty();
        }

        // Calculate cross rate
        BigDecimal adjustedRate = targetRate.divide(baseRate, 4, RoundingMode.HALF_UP);

        var exchangeRateDto = ExchangeRateDto.builder()
                .baseCurrency(baseCurrency)
                .targetCurrency(targetCurrency)
                .rate(adjustedRate)
                .timestamp(DateUtil.max(baseExchangeRate.getTimestamp(), targetExchangeRate.getTimestamp()))
                .build();
        return Optional.of(exchangeRateDto);
    }

}