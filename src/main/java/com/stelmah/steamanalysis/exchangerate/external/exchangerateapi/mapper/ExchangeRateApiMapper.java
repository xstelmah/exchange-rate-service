package com.stelmah.steamanalysis.exchangerate.external.exchangerateapi.mapper;

import com.stelmah.steamanalysis.exchangerate.dto.ExchangeRateDto;
import com.stelmah.steamanalysis.exchangerate.dto.ExchangeRateSnapshotDto;
import com.stelmah.steamanalysis.exchangerate.external.exchangerateapi.dto.ExchangeRateApiResponseDto;
import com.stelmah.steamanalysis.exchangerate.service.util.DateUtil;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, imports = {LocalDateTime.class})
public interface ExchangeRateApiMapper {

    String VENDOR_NAME = "EXCHANGE_RATE_API";

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "vendorTimestamp", source = "timeLastUpdateUnix", qualifiedByName = "mapUnixToLocalDateTime")
    @Mapping(target = "serverTimestamp", expression = "java(LocalDateTime.now())")
    @Mapping(target = "vendor", constant = VENDOR_NAME)
    @Mapping(target = "exchangeRates", source = "dto", qualifiedByName = "mapToExchangeRates")
    ExchangeRateSnapshotDto toExchangeRateSnapshotDto(ExchangeRateApiResponseDto dto);

    @Named("mapToExchangeRates")
    static List<ExchangeRateDto> mapToExchangeRates(ExchangeRateApiResponseDto dto) {
        if (dto == null || dto.getConversionRates() == null) {
            return List.of();
        }
        return dto.getConversionRates().entrySet().stream()
                .filter(currencyRateEntry -> currencyRateEntry.getValue() != null)
                .filter(currencyRateEntry -> !BigDecimal.ZERO.equals(currencyRateEntry.getValue()))
                .map(currencyRateEntry ->
                        ExchangeRateDto.builder()
                        .rate(currencyRateEntry.getValue())
                        .targetCurrency(currencyRateEntry.getKey())
                        .baseCurrency(dto.getBaseCode())
                        .timestamp(mapUnixToLocalDateTime(dto.getTimeLastUpdateUnix()))
                        .build()
                )
                .toList();
    }

    @Named("mapUnixToLocalDateTime")
    static LocalDateTime mapUnixToLocalDateTime(long unixTimestamp) {
        return DateUtil.toLocalDateTime(unixTimestamp);
    }

}
