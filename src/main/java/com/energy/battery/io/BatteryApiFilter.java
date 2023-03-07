package com.energy.battery.io;

import java.util.Arrays;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.data.domain.Sort.Direction;

public class BatteryApiFilter extends ApiFilter {

  private static final String DEFAULT_SORT_FIELD = "name";

  private Integer startPostCode;
  private Integer endPostCode;

  public Integer getStartPostCode() {
    return startPostCode;
  }

  public void setStartPostCode(Integer startPostCode) {
    this.startPostCode = startPostCode;
  }

  public Integer getEndPostCode() {
    return endPostCode;
  }

  public void setEndPostCode(Integer endPostCode) {
    this.endPostCode = endPostCode;
  }

  @Override
  public void initWithDefaultValues() {
    this.setSortFields(Arrays.asList(DEFAULT_SORT_FIELD));
    this.setSortOrder(Direction.ASC);
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
        .append("startPostCode", startPostCode)
        .append("endPostCode", endPostCode)
        .append("sortOrder", getSortOrder())
        .append("getSortFields()", getSortFields())
        .append("currentPage", getCurrentPage())
        .append("pageSize", getPageSize())
        .toString();
  }

}
