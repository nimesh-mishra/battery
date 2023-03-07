package com.energy.battery.mappers;

import org.mapstruct.Mapper;
import com.energy.battery.entities.Battery;
import com.energy.battery.io.BatteryIO;

@Mapper(componentModel = "spring")
public interface BatteryMapper {

  BatteryIO toIO(Battery battery);

  Battery toEntity(BatteryIO batteryIO);
}
