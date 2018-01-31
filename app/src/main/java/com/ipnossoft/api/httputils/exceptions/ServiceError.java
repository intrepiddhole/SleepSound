package com.ipnossoft.api.httputils.exceptions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(
        ignoreUnknown = true
)
public class ServiceError extends Exception {
  private String code;
  private String error_code;
  private String message;

  public ServiceError() {
  }

  public String getCode() {
    return this.code;
  }

  public String getError_code() {
    return this.error_code;
  }

  public String getMessage() {
    return this.message;
  }

  public void setCode(String var1) {
    this.code = var1;
  }

  public void setError_code(String var1) {
    this.error_code = var1;
  }

  public void setMessage(String var1) {
    this.message = var1;
  }
}
