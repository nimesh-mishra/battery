package com.energy.battery.services.impl;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import com.energy.battery.entities.Battery;
import com.energy.battery.io.ApiPaginatedResponse;
import com.energy.battery.io.BatteryApiFilter;
import com.energy.battery.io.BatteryIO;
import com.energy.battery.io.BatteryNamesWithStatsIO;
import com.energy.battery.mappers.BatteryMapper;
import com.energy.battery.repositories.BatteryRepository;

@ExtendWith(SpringExtension.class)
class BatteryServiceTest {

  private static final String NAME = "AMARON";
  private static final Integer CAPACITY = 780;
  private static final Integer POST_CODE = 7800;

  @Mock
  private BatteryMapper batteryMapper;

  @Mock
  private BatteryRepository batteryRepository;

  @InjectMocks
  private BatteryServiceImpl batteryService;

  @Test
  void testSaveWillAddEntryToTheDB() {
    // Given
    BatteryIO batteryIO = buildBatteryIO(NAME, POST_CODE, CAPACITY);
    Battery battery = buildBattery(NAME, POST_CODE, CAPACITY);
    given(batteryMapper.toEntity(batteryIO)).willReturn(battery);
    given(batteryRepository.save(battery)).willReturn(battery);
    given(batteryMapper.toIO(battery)).willReturn(batteryIO);
    // When
    BatteryIO savedIO = batteryService.save(batteryIO);
    // Then
    assertNotNull(savedIO);
    assertEquals(batteryIO.getName(), savedIO.getName());
    assertEquals(batteryIO.getCapacity(), savedIO.getCapacity());
    assertEquals(batteryIO.getPostCode(), savedIO.getPostCode());
    verify(batteryMapper).toIO(battery);
    verify(batteryMapper).toEntity(batteryIO);
    verify(batteryRepository).save(battery);
  }

  @Test
  void testFindByPostCodeRangeWithNonExistingPostCodeRangeWillReturnEmpty() {
    // Given
    BatteryApiFilter filter = buildBatteryApiFilter(7500, 7600);
    given(batteryRepository.findByPostCodeBetween(eq(7500), eq(7600), any()))
        .willReturn(Page.empty());
    // When
    ApiPaginatedResponse<BatteryIO> response = batteryService.findByPostCodeRange(filter);
    // Then
    assertNotNull(response);
    assertTrue(response.getContents().isEmpty());
    verify(batteryMapper, never()).toIO(any());
  }

  @Test
  void testFindByPostCodeRangeWithInvalidPostCodeRangeWillReturnEmpty() {
    // Given
    BatteryApiFilter filter = buildBatteryApiFilter(7600, 7500);
    // When
    ApiPaginatedResponse<BatteryIO> response = batteryService.findByPostCodeRange(filter);
    // Then
    assertNotNull(response);
    assertTrue(response.getContents().isEmpty());
    verify(batteryMapper, never()).toIO(any());
    verify(batteryRepository, never()).findByPostCodeBetween(any(), any(), any());
  }

  @Test
  void testFindBatteryNamesByPostCodeRangeWithInvalidPostCodeRangeWillReturnEmpty() {
    // Given
    BatteryApiFilter filter = buildBatteryApiFilter(7600, 7500);
    // When
    ApiPaginatedResponse<BatteryNamesWithStatsIO> response = batteryService
        .findBatteryNamesByPostCodeRange(filter);
    // Then
    assertNotNull(response);
    assertTrue(response.getContents().isEmpty());
    verify(batteryRepository, never()).findByPostCodeBetween(any(), any(), any());
  }
  
  @Test
  void testFindBatteryNamesByPostCodeRangeWithOutOfRangePostCodeRangeWillReturnEmpty() {
    // Given
    BatteryApiFilter filter = buildBatteryApiFilter(7500, 7600);
    given(batteryRepository.findByPostCodeBetween(eq(7500), eq(7600), any()))
        .willReturn(Page.empty());
    // When
    ApiPaginatedResponse<BatteryNamesWithStatsIO> response = batteryService
        .findBatteryNamesByPostCodeRange(filter);
    // Then
    assertNotNull(response);
    assertTrue(response.getContents().isEmpty());
    verify(batteryRepository).findByPostCodeBetween(eq(7500), eq(7600), any());
  }

  @Test
  void testFindBatteryNamesByPostCodeRangeWithValidRangePostCodeRangeWillBatteryNamesAndStats() {
    // Given
    BatteryApiFilter filter = buildBatteryApiFilter(7500, 7600);
    List<Battery> batteries = asList(
        buildBattery(NAME, POST_CODE, CAPACITY),
        buildBattery(NAME + "1", POST_CODE, CAPACITY)
    );
    given(batteryRepository.findByPostCodeBetween(eq(7500), eq(7600), any()))
        .willReturn(new PageImpl<>(batteries));
    // When
    ApiPaginatedResponse<BatteryNamesWithStatsIO> response =
        batteryService.findBatteryNamesByPostCodeRange(filter);
    // Then
    assertNotNull(response);
    assertNotNull(response.getContent());
    BatteryNamesWithStatsIO statsIO = response.getContent();
    assertEquals(asList(NAME, NAME + "1"), statsIO.getNames());
    assertEquals(780.0D, statsIO.getStats().getAverageCapacity());
    assertEquals(1560, statsIO.getStats().getTotalCapacity());
    verify(batteryRepository).findByPostCodeBetween(eq(7500), eq(7600), any());
  }

  private BatteryApiFilter buildBatteryApiFilter(Integer start, Integer end) {
    BatteryApiFilter filter = mock(BatteryApiFilter.class);
    given(filter.getStartPostCode()).willReturn(start);
    given(filter.getEndPostCode()).willReturn(end);
    return filter;
  }

  private BatteryIO buildBatteryIO(String name, Integer postCode, Integer capacity) {
    BatteryIO io = mock(BatteryIO.class);
    given(io.getName()).willReturn(name);
    given(io.getCapacity()).willReturn(capacity);
    given(io.getPostCode()).willReturn(postCode);
    return io;
  }

  private Battery buildBattery(String name, Integer postCode, Integer capacity) {
    Battery battery = mock(Battery.class);
    given(battery.getName()).willReturn(name);
    given(battery.getCapacity()).willReturn(capacity);
    given(battery.getPostCode()).willReturn(postCode);
    return battery;
  }
}
