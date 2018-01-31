package com.ipnossoft.api.httputils.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class ResourcesResponse<T> extends Response {
  private List<T> resources;

  public ResourcesResponse() {
    super((MetaData)null);
  }

  public ResourcesResponse(@JsonProperty("meta") MetaData var1, @JsonProperty("objects") List<T> var2) {
    super(var1);
    this.setResources(var2);
  }

  public List<T> getResources() {
    return this.resources;
  }

  public void setResources(List<T> var1) {
    this.resources = var1;
  }
}
