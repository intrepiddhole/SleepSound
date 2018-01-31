package com.ipnossoft.api.httputils.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class Response {
  private MetaData metaData;

  public Response(@JsonProperty("meta") MetaData var1) {
    this.setMetaData(var1);
  }

  public MetaData getMetaData() {
    return this.metaData;
  }

  public void setMetaData(MetaData var1) {
    this.metaData = var1;
  }
}
