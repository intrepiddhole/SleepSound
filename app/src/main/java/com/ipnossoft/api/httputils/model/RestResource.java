package com.ipnossoft.api.httputils.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(
        ignoreUnknown = true
)
public class RestResource {
  private String identifier;
  private String resourceName;

  public RestResource(String var1) {
    this.setResourceName(var1);
  }

  public String getIdentifier() {
    return this.identifier;
  }

  public String getResourceName() {
    return this.resourceName;
  }

  @JsonProperty("identifier")
  public void setIdentifier(String var1) {
    this.identifier = var1;
  }

  @JsonIgnore
  public void setResourceName(String var1) {
    this.resourceName = var1;
  }
}