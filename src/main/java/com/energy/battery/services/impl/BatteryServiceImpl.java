package com.energy.battery.services.impl;

import static java.util.Objects.nonNull;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.energy.battery.entities.Battery;
import com.energy.battery.io.ApiPaginatedResponse;
import com.energy.battery.io.BatteryApiFilter;
import com.energy.battery.io.BatteryIO;
import com.energy.battery.io.BatteryNamesWithStatsIO;
import com.energy.battery.io.BatteryNamesWithStatsIO.Stats;
import com.energy.battery.mappers.BatteryMapper;
import com.energy.battery.repositories.BatteryRepository;
import com.energy.battery.services.BatteryService;

@Service
public class BatteryServiceImpl implements BatteryService {

  @Autowired
  private BatteryMapper batteryMapper;

  @Autowired
  private BatteryRepository repository;

  @Override
  public BatteryIO save(BatteryIO batteryIO) {
    Battery battery = batteryMapper.toEntity(batteryIO);
    return Optional.ofNullable(battery)
        .map(repository::save)
        .map(batteryMapper::toIO)
        .orElse(batteryIO);
  }

  @Override
  public ApiPaginatedResponse<BatteryIO> findByPostCodeRange(BatteryApiFilter batteryApiFilter) {
    if (!isValidPostCodeRange(batteryApiFilter)) {
      return new ApiPaginatedResponse<>();
    }
    Page<BatteryIO> pages = findByPostCodeRange(
        batteryApiFilter.getStartPostCode(), batteryApiFilter.getEndPostCode(),
        batteryApiFilter.toPageable()
        )
        .map(batteryMapper::toIO);
    return ApiPaginatedResponse.fromPage(pages);
  }

  @Override
  public ApiPaginatedResponse<BatteryNamesWithStatsIO> findBatteryNamesByPostCodeRange(
      BatteryApiFilter batteryApiFilter
  ) {
    if (!isValidPostCodeRange(batteryApiFilter)) {
      return new ApiPaginatedResponse<>();
    }
    Page<Battery> batteries = findByPostCodeRange(
        batteryApiFilter.getStartPostCode(), batteryApiFilter.getEndPostCode(), 
        batteryApiFilter.toPageable()
    );
    List<String> names = batteries.map(Battery::getName).toList();
    IntSummaryStatistics summary = batteries.stream()
        .map(Battery::getCapacity)
        .mapToInt(Integer::intValue)
        .summaryStatistics();
    BatteryNamesWithStatsIO stats =
        new BatteryNamesWithStatsIO(names, new Stats(summary.getSum(), summary.getAverage()));
    ApiPaginatedResponse<BatteryNamesWithStatsIO> response = new ApiPaginatedResponse<>();
    response.setContent(stats);
    response.setPageDetailsFromPage(batteries);
    return response;
  }

  private Page<Battery> findByPostCodeRange(
      Integer startRange, Integer endRange, Pageable pageable
  ){
    return repository.findByPostCodeBetween(startRange, endRange, pageable);
  }

  private boolean isValidPostCodeRange(BatteryApiFilter batteryApiFilter) {
    return nonNull(batteryApiFilter.getStartPostCode())
        && nonNull(batteryApiFilter.getEndPostCode())
        && batteryApiFilter.getStartPostCode().compareTo(batteryApiFilter.getEndPostCode()) <= 0;
  }
}
