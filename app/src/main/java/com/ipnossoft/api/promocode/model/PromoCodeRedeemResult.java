package com.ipnossoft.api.promocode.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;

public class PromoCodeRedeemResult {
  private String code;
  @JsonProperty("inapppurchase")
  private String contentId;
  @JsonProperty("effective_date")
  private Date effectiveDate;
  @JsonProperty("expiration_date")
  private Date expirationDate;
  @JsonProperty("max_redeem")
  private int maxRedeem;
  @JsonProperty("redeem_count")
  private int redeemCount;

  public PromoCodeRedeemResult() {
  }

  public String getCode() {
    return this.code;
  }

  public Date getEffectiveDate() {
    return this.effectiveDate;
  }

  public Date getExpirationDate() {
    return this.expirationDate;
  }

  public String getFeatureId() {
    return this.contentId;
  }

  public int getMaxRedeem() {
    return this.maxRedeem;
  }

  public int getRedeemCount() {
    return this.redeemCount;
  }

  public boolean isUnlimited() {
    return this.maxRedeem == -1;
  }

  public void setCode(String var1) {
    this.code = var1;
  }

  public void setEffectiveDate(Date var1) {
    this.effectiveDate = var1;
  }

  public void setExpirationDate(Date var1) {
    this.expirationDate = var1;
  }

  public void setFeatureId(String var1) {
    this.contentId = var1;
  }

  public void setMaxRedeem(int var1) {
    this.maxRedeem = var1;
  }

  public void setRedeemCount(int var1) {
    this.redeemCount = var1;
  }
}
