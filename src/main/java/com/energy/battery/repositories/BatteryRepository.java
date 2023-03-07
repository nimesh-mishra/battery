package com.energy.battery.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import com.energy.battery.entities.Battery;

public interface BatteryRepository extends PagingAndSortingRepository<Battery, Long> {

  Page<Battery> findByPostCodeBetween(Integer startRange, Integer endRange, Pageable pageable);
}
