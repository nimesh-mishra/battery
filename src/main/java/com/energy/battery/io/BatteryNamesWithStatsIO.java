package com.energy.battery.io;

import java.util.List;

public class BatteryNamesWithStatsIO {

  private List<String> names;
  private Stats stats;
  
  public BatteryNamesWithStatsIO() {}

  public BatteryNamesWithStatsIO(List<String> names, Stats stats) {
    this.names = names;
    this.stats = stats;
  }

  public List<String> getNames() {
    return names;
  }

  public void setNames(List<String> names) {
    this.names = names;
  }

  public Stats getStats() {
    return stats;
  }

  public void setStats(Stats stats) {
    this.stats = stats;
  }

  public static class Stats {

    private Long totalCapacity;
    private Double averageCapacity;

    public Stats() {}

    public Stats(Long totalCapacity, Double averageCapacity) {
      this.totalCapacity = totalCapacity;
      this.averageCapacity = averageCapacity;
    }

    public Long getTotalCapacity() {
      return totalCapacity;
    }

    public void setTotalCapacity(Long totalCapacity) {
      this.totalCapacity = totalCapacity;
    }

    public Double getAverageCapacity() {
      return averageCapacity;
    }

    public void setAverageCapacity(Double averageCapacity) {
      this.averageCapacity = averageCapacity;
    }

  }
}
