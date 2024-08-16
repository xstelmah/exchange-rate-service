package com.stelmah.steamanalysis.exchangerate.service.impl;

import com.stelmah.steamanalysis.exchangerate.dto.ExchangeRateDto;
import com.stelmah.steamanalysis.exchangerate.entity.ExchangeRate;
import com.stelmah.steamanalysis.exchangerate.entity.ExchangeRateSnapshot;
import com.stelmah.steamanalysis.exchangerate.mapper.ExchangeRateMapper;
import com.stelmah.steamanalysis.exchangerate.repository.ExchangeRateRepository;
import com.stelmah.steamanalysis.exchangerate.service.ExchangeRateService;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class ExchangeRateServiceImpl implements ExchangeRateService {

    private final ExchangeRateMapper exchangeRateMapper;

    private final ExchangeRateRepository exchangeRateRepository;

    @Override
    public ExchangeRateDto getLatestRates(String baseCurrency, String targetCurrency) {
        return exchangeRateRepository.findLatestByBaseAndTargetCurrencies(baseCurrency, targetCurrency, PageRequest.of(0, 1))
                .map(exchangeRateMapper::toDto)
                .map(exchangeRateDto -> swapExchangeRateIfNeeded(exchangeRateDto, baseCurrency, targetCurrency))
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
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
        exchangeRates =  exchangeRateRepository.saveAll(exchangeRates);
        log.info("Added {} exchangeRates for snapshot({})", exchangeRates.size(), snapshot.getId());

        return exchangeRates;
    }

    public ExchangeRateDto swapExchangeRateIfNeeded(ExchangeRateDto dto, String baseCurrency, String targetCurrency) {
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


}