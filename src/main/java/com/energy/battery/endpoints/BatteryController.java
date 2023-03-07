package com.energy.battery.endpoints;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.energy.battery.io.ApiPaginatedResponse;
import com.energy.battery.io.BatteryApiFilter;
import com.energy.battery.io.BatteryIO;
import com.energy.battery.io.BatteryNamesWithStatsIO;
import com.energy.battery.services.BatteryService;

@RestController
@RequestMapping(value = "/batteries")
public class BatteryController {

  private static final Logger LOGGER = LoggerFactory.getLogger(BatteryController.class);
  
  @Autowired
  private BatteryService batteryService;
  
  @PostMapping(value = "/create", consumes = {APPLICATION_JSON_VALUE},
      produces = {APPLICATION_JSON_VALUE})
  public BatteryIO create(@RequestBody BatteryIO batteryIO) {
    LOGGER.info("Request received to create a battery with body: {}", batteryIO);
    return batteryService.save(batteryIO);
  }

  @PostMapping(value = "/find-by-postcode-range", consumes = {APPLICATION_JSON_VALUE},
      produces = {APPLICATION_JSON_VALUE})
  public ApiPaginatedResponse<BatteryIO> findByPostCodeRange(
      @RequestBody BatteryApiFilter batteryApiFilter
  ) {
    LOGGER.info("Request received find Battery with post code range with body: {}", batteryApiFilter);
    return batteryService.findByPostCodeRange(batteryApiFilter);
  }

  @PostMapping(value = "/find-names-by-postcode-range", consumes = {APPLICATION_JSON_VALUE},
      produces = {APPLICATION_JSON_VALUE})
  public ApiPaginatedResponse<BatteryNamesWithStatsIO> findNamesByPostCodeRange(
      @RequestBody BatteryApiFilter batteryApiFilter) {
    LOGGER.info("Request received find Battery names with post code range with body: {}",
        batteryApiFilter);
    return batteryService.findBatteryNamesByPostCodeRange(batteryApiFilter);
  }

}
