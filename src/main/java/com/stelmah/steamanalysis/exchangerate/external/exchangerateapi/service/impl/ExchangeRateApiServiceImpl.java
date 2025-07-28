package com.stelmah.steamanalysis.exchangerate.external.exchangerateapi.service.impl;

import com.stelmah.steamanalysis.exchangerate.dto.ExchangeRateSnapshotDto;
import com.stelmah.steamanalysis.exchangerate.external.exchangerateapi.dto.ExchangeRateApiHistoryResponseDto;
import com.stelmah.steamanalysis.exchangerate.external.exchangerateapi.dto.ExchangeRateApiResponseDto;
import com.stelmah.steamanalysis.exchangerate.external.exchangerateapi.exception.ExchangeRateApiException;
import com.stelmah.steamanalysis.exchangerate.external.exchangerateapi.mapper.ExchangeRateApiMapper;
import com.stelmah.steamanalysis.exchangerate.external.exchangerateapi.service.ExchangeRateApiClient;
import com.stelmah.steamanalysis.exchangerate.external.exchangerateapi.service.ExchangeRateApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ExchangeRateApiServiceImpl implements ExchangeRateApiService {

    private final ExchangeRateApiClient exchangeRateApiClient;
    private final ExchangeRateApiMapper exchangeRateApiMapper;

    @Override
    public ExchangeRateSnapshotDto fetchLatestExchangeRates(String baseCurrency) throws ExchangeRateApiException {
        try {
            var responseDto = exchangeRateApiClient.fetchLatestExchangeRates(baseCurrency);

            validate(responseDto);

            return exchangeRateApiMapper.toExchangeRateSnapshotDto(responseDto);
        } catch (ExchangeRateApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ExchangeRateApiException("Failed to handle", e)
                    .addContextValue("baseCurrency", baseCurrency);
        }
    }

    @Override
    public ExchangeRateSnapshotDto fetchHistoricalExchangeRates(String currency, LocalDate date) throws ExchangeRateApiException {
        try {
            var responseDto = exchangeRateApiClient.fetchHistoricalExchangeRates(
                    currency,
                    date.getYear(),
                    date.getMonthValue(),
                    date.getDayOfMonth()
            );

            validate(responseDto);

            return exchangeRateApiMapper.toExchangeRateSnapshotDto(responseDto);
        } catch (ExchangeRateApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ExchangeRateApiException("Failed to handle", e)
                    .addContextValue("currency", currency)
                    .addContextValue("date", date);
        }
    }

    private void validate(ExchangeRateApiResponseDto responseDto) {
        if (responseDto == null) {
            throw new ExchangeRateApiException("Response is null");
        }
        if (responseDto.getResult() == null || !"success".equals(responseDto.getResult())) {
            throw new ExchangeRateApiException("Failed response")
                    .addContextValue("response", responseDto);
        }
        if (CollectionUtils.isEmpty(responseDto.getConversionRates())) {
            throw new ExchangeRateApiException("No conversion rates found")
                    .addContextValue("response", responseDto);
        }
    }

    private void validate(ExchangeRateApiHistoryResponseDto responseDto) {
        if (responseDto == null) {
            throw new ExchangeRateApiException("Response is null");
        }
        if (responseDto.getResult() == null || !"success".equals(responseDto.getResult())) {
            throw new ExchangeRateApiException("Failed response")
                    .addContextValue("response", responseDto);
        }
        if (CollectionUtils.isEmpty(responseDto.getConversionRates())) {
            throw new ExchangeRateApiException("No conversion rates found")
                    .addContextValue("response", responseDto);
        }
    }
}
