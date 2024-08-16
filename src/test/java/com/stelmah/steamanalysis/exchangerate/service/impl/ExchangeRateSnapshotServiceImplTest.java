package com.stelmah.steamanalysis.exchangerate.service.impl;

import com.stelmah.steamanalysis.exchangerate.dto.ExchangeRateSnapshotDto;
import com.stelmah.steamanalysis.exchangerate.entity.ExchangeRateSnapshot;
import com.stelmah.steamanalysis.exchangerate.mapper.ExchangeRateSnapshotMapper;
import com.stelmah.steamanalysis.exchangerate.repository.ExchangeRateSnapshotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ExchangeRateSnapshotServiceImplTest {

    @Mock
    private ExchangeRateSnapshotMapper exchangeRateSnapshotMapper;

    @Mock
    private ExchangeRateSnapshotRepository exchangeRateSnapshotRepository;

    @InjectMocks
    private ExchangeRateSnapshotServiceImpl exchangeRateSnapshotService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveSnapshotWithoutRatesSuccess() {
        // Given
        ExchangeRateSnapshotDto snapshotDto = new ExchangeRateSnapshotDto();
        ExchangeRateSnapshot mappedSnapshot = new ExchangeRateSnapshot();
        mappedSnapshot.setId(1L);  // Simulating a pre-existing ID

        ExchangeRateSnapshot savedSnapshot = new ExchangeRateSnapshot();
        savedSnapshot.setId(2L);  // Simulating the ID after saving to DB

        when(exchangeRateSnapshotMapper.toEntity(snapshotDto)).thenReturn(mappedSnapshot);
        when(exchangeRateSnapshotRepository.save(any(ExchangeRateSnapshot.class))).thenReturn(savedSnapshot);

        // When
        ExchangeRateSnapshot result = exchangeRateSnapshotService.saveSnapshotWithoutRates(snapshotDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(2L);  // Ensure the ID is as expected after saving
        verify(exchangeRateSnapshotMapper, times(1)).toEntity(snapshotDto);
        verify(exchangeRateSnapshotRepository, times(1)).save(mappedSnapshot);
        assertThat(mappedSnapshot.getId()).isNull();  // Ensure ID is null before saving
    }

    @Test
    void testSaveSnapshotWithoutRatesNullIdBeforeSave() {
        // Given
        ExchangeRateSnapshotDto snapshotDto = new ExchangeRateSnapshotDto();
        ExchangeRateSnapshot mappedSnapshot = new ExchangeRateSnapshot();
        mappedSnapshot.setId(5L);  // Initial ID that should be nullified before saving

        when(exchangeRateSnapshotMapper.toEntity(snapshotDto)).thenReturn(mappedSnapshot);
        when(exchangeRateSnapshotRepository.save(any(ExchangeRateSnapshot.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        ExchangeRateSnapshot result = exchangeRateSnapshotService.saveSnapshotWithoutRates(snapshotDto);

        // Then
        assertThat(result.getId()).isNull();  // Ensure ID was nullified before saving
        verify(exchangeRateSnapshotMapper, times(1)).toEntity(snapshotDto);
        verify(exchangeRateSnapshotRepository, times(1)).save(mappedSnapshot);
    }
}