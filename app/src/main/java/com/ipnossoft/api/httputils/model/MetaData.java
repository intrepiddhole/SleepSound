package com.ipnossoft.api.httputils.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MetaData {
  private int limit;
  private String next;
  private int offset;
  private String previous;
  private int totalCount;

  public MetaData(@JsonProperty("limit") int var1, @JsonProperty("next") String var2, @JsonProperty("offset") int var3, @JsonProperty("previous") String var4, @JsonProperty("total_count") int var5) {
    this.setLimit(var1);
    this.setNext(var2);
    this.setOffset(var3);
    this.setPrevious(var4);
    this.setTotalCount(var5);
  }

  public int getLimit() {
    return this.limit;
  }

  public String getNext() {
    return this.next;
  }

  public int getOffset() {
    return this.offset;
  }

  public String getPrevious() {
    return this.previous;
  }

  public int getTotalCount() {
    return this.totalCount;
  }

  public void setLimit(int var1) {
    this.limit = var1;
  }

  public void setNext(String var1) {
    this.next = var1;
  }

  public void setOffset(int var1) {
    this.offset = var1;
  }

  public void setPrevious(String var1) {
    this.previous = var1;
  }

  public void setTotalCount(int var1) {
    this.totalCount = var1;
  }
}
