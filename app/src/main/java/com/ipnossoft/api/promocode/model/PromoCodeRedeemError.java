package com.ipnossoft.api.promocode.model;

public class PromoCodeRedeemError {
  private String code;
  private String message;

  public PromoCodeRedeemError() {
  }

  public String getCode() {
    return this.code;
  }

  public String getMessage() {
    return this.message;
  }

  public void setCode(String var1) {
    this.code = var1;
  }

  public void setMessage(String var1) {
    this.message = var1;
  }
}
