package com.energy.battery.io;

import java.io.Serializable;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class BatteryIO extends AbstractEntityIO implements Serializable {

  private static final long serialVersionUID = -5357725879543019694L;

  private String name;
  private Integer postCode;
  private Integer capacity;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getPostCode() {
    return postCode;
  }

  public void setPostCode(Integer postCode) {
    this.postCode = postCode;
  }

  public Integer getCapacity() {
    return capacity;
  }

  public void setCapacity(Integer capacity) {
    this.capacity = capacity;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
        .append("name", name)
        .append("postCode", postCode)
        .append("capacity", capacity)
        .append("getId()", getId())
        .append("getUpdatedDate()", getUpdatedDate())
        .append("isDeleted()", isDeleted())
        .toString();
  }

}
