package com.stelmah.steamanalysis.exchangerate.mapper;

import com.stelmah.steamanalysis.exchangerate.dto.ExchangeRateDto;
import com.stelmah.steamanalysis.exchangerate.entity.ExchangeRate;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ExchangeRateMapper {

    ExchangeRate toEntity(ExchangeRateDto dto);

    ExchangeRateDto toDto(ExchangeRate entity);
}
