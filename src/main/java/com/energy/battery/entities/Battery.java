package com.energy.battery.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Index;
import javax.persistence.Table;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(
    name = "batteries", 
    indexes = {
        @Index(name = "idx_name", columnList = "name"),
        @Index(name = "idx_post_code", columnList = "post_code")
    }
)
@EntityListeners(AuditingEntityListener.class)
public class Battery extends AbstractEntity {

  @Column(name = "name", nullable = false)
  private String name;
  @Column(name = "post_code", nullable = false)
  private Integer postCode;
  @Column(name = "capacity", nullable = false)
  private Integer capacity;

  public Battery() {}

  public Battery(String name, Integer postCode, Integer capacity) {
    this.name = name;
    this.postCode = postCode;
    this.capacity = capacity;
  }

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
        .toString();
  }

}
