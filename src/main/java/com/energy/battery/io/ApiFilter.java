package com.energy.battery.io;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

public abstract class ApiFilter {

  private int currentPage = 0;
  private int pageSize = 10;
  private Direction sortOrder;
  private List<String> sortFields = new LinkedList<>();

  public void setCurrentPage(int currentPage) {
    this.currentPage = currentPage;
  }

  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }

  public void setSortOrder(Direction sortOrder) {
    this.sortOrder = sortOrder;
  }

  public int getCurrentPage() {
    return currentPage;
  }

  public int getPageSize() {
    return pageSize;
  }

  public Direction getSortOrder() {
    return sortOrder;
  }

  public List<String> getSortFields() {
    return sortFields;
  }

  public void setSortFields(List<String> sortFields) {
    this.sortFields.clear();
    this.sortFields.addAll(sortFields);
  }

  public Pageable toPageable() {
    Sort sort = null;
    if (!isNull(sortOrder) && !sortFields.isEmpty()) {
      List<Order> orders = sortFields.stream()
          .map(field -> new Order(sortOrder, field))
          .collect(toList());
      sort = Sort.by(orders);
    }
    return Objects.nonNull(sort)
        ? PageRequest.of(currentPage, pageSize, sort)
        : PageRequest.of(currentPage, pageSize);
  }

  protected ApiFilter() {
    initWithDefaultValues();
  }

  /**
   * Set default values per model requirements.
   */
  public abstract void initWithDefaultValues();


  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
        .append("sortOrder", sortOrder)
        .append("sortFields", sortFields)
        .append("currentPage", currentPage)
        .append("pageSize", pageSize)
        .toString();
  }

}
