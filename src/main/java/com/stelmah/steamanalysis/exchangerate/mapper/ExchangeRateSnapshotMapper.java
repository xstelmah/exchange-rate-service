package com.stelmah.steamanalysis.exchangerate.mapper;

import com.stelmah.steamanalysis.exchangerate.dto.ExchangeRateSnapshotDto;
import com.stelmah.steamanalysis.exchangerate.entity.ExchangeRateSnapshot;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ExchangeRateSnapshotMapper {

    ExchangeRateSnapshot toEntity(ExchangeRateSnapshotDto dto);

    ExchangeRateSnapshotDto toDto(ExchangeRateSnapshot entity);
}
