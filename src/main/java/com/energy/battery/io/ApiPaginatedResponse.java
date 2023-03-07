package com.energy.battery.io;

import java.util.LinkedList;
import java.util.List;
import org.springframework.data.domain.Page;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class ApiPaginatedResponse<T> {

  private PageDetails pageDetails;
  private T content;
  private List<T> contents = new LinkedList<>();

  public static <T> ApiPaginatedResponse<T> fromPage(Page<T> page) {
    ApiPaginatedResponse<T> paginate = new ApiPaginatedResponse<>();
    PageDetails pageDetails = new PageDetails();
    /* we start page index from 1, spring starts at 0 index */
    pageDetails.setCurrentPage(page.getNumber() + 1);
    pageDetails.setTotalPages(page.getTotalPages());
    pageDetails.setTotalCount(page.getTotalElements());
    pageDetails.setPageCount(page.getNumberOfElements());
    paginate.setPageDetails(pageDetails);
    paginate.setContents(page.getContent());
    return paginate;
  }

  public void setPageDetailsFromPage(Page<?> page) {
    pageDetails = new PageDetails();
    /* we start page index from 1, spring starts at 0 index */
    pageDetails.setCurrentPage(page.getNumber() + 1);
    pageDetails.setTotalPages(page.getTotalPages());
    pageDetails.setTotalCount(page.getTotalElements());
    pageDetails.setPageCount(page.getNumberOfElements());
  }

  public void setPageDetails(PageDetails pageDetails) {
    this.pageDetails = pageDetails;
  }

  public PageDetails getPageDetails() {
    return pageDetails;
  }

  public void setContents(List<T> contents) {
    this.contents = contents;
  }

  public List<T> getContents() {
    return contents;
  }

  public T getContent() {
    return content;
  }

  public void setContent(T content) {
    this.content = content;
  }

  public static class PageDetails {

    private int currentPage; // current page number starting at 1
    private long pageCount; // total number of records in the current page
    private int totalPages; // total number of pages available
    private long totalCount; // total number of records in the result

    public int getCurrentPage() {
      return currentPage;
    }

    public void setCurrentPage(int currentPage) {
      this.currentPage = currentPage;
    }

    public long getPageCount() {
      return pageCount;
    }

    public void setPageCount(long pageCount) {
      this.pageCount = pageCount;
    }

    public int getTotalPages() {
      return totalPages;
    }

    public void setTotalPages(int totalPages) {
      this.totalPages = totalPages;
    }

    public long getTotalCount() {
      return totalCount;
    }

    public void setTotalCount(long totalCount) {
      this.totalCount = totalCount;
    }
  }

}
