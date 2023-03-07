package com.energy.battery.services;

import com.energy.battery.io.ApiPaginatedResponse;
import com.energy.battery.io.BatteryApiFilter;
import com.energy.battery.io.BatteryIO;
import com.energy.battery.io.BatteryNamesWithStatsIO;

public interface BatteryService {

  BatteryIO save(BatteryIO batteryIO);

  /**
   * Finds the batteries with the given postCode range.
   * 
   * @param batteryApiFilter {@link BatteryApiFilter} The request object
   * @return {@link ApiPaginatedResponse} The response with pagination
   */
  ApiPaginatedResponse<BatteryIO> findByPostCodeRange(BatteryApiFilter batteryApiFilter);

  /**
   * Finds the batteries name and stats with the given postCode range.
   * 
   * @param batteryApiFilter {@link BatteryApiFilter}
   * @return The response with pagination information
   */
  ApiPaginatedResponse<BatteryNamesWithStatsIO> findBatteryNamesByPostCodeRange(
      BatteryApiFilter batteryApiFilter
  );
}
