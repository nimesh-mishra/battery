package com.energy.battery.config.authentication;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ApplicationID {

  public enum ApplicationIDType {
    USER, SYSTEM_USER
  }

  private ApplicationIDType type;

  public ApplicationIDType getType() {
    return type;
  }

  public ApplicationID(ApplicationIDType type) {
    this.type = type;
  }


  public static ApplicationID systemDefault() {
    return new ApplicationID(ApplicationIDType.SYSTEM_USER);
  }

  public boolean hasType(ApplicationIDType type) {
    return getType().equals(type);
  }

  public boolean isSystemUser() {
    return hasType(ApplicationIDType.SYSTEM_USER);
  }


  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
        .append("type", getType())
        .build();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ApplicationID that = (ApplicationID) o;
    return new EqualsBuilder()
        .append(this.getType(), that.getType())
        .append(this.isSystemUser(), that.isSystemUser())
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder()
        .append(this.getType())
        .append(this.isSystemUser())
        .toHashCode();
  }
}
