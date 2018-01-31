package com.ipnossoft.api.featuremanager.data;

import android.support.annotation.NonNull;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ipnossoft.api.dynamiccontent.model.InAppPurchase;
import com.ipnossoft.api.featuremanager.util.DateUtils;
import java.util.Date;

@JsonIgnoreProperties(
        ignoreUnknown = true
)
public class Subscription {
  private boolean autoRenewable;
  private Date expires;
  private boolean fromPromoCode;
  private String identifier;
  private String orderId;
  private String packageName;
  private String purchaseToken;
  private Date purchased;
  private String storeName;
  private int tear;

  public Subscription() {
  }

  public Subscription(InAppPurchase var1) {
    this.identifier = var1.getIdentifier();
    this.autoRenewable = var1.isSubscriptionAutoRenewable();
    this.purchased = new Date();
    this.expires = this.calculateExpirationDate(this.purchased, var1);
  }

  /*public Subscription(InAppPurchase var1, Purchase var2) {
    this.identifier = var1.getIdentifier();
    this.autoRenewable = var1.isSubscriptionAutoRenewable();
    Date var3;
    /*if(var2.getPurchaseTime() != 0L) {
      var3 = new Date(var2.getPurchaseTime());
    } else {
      var3 = new Date();
    }

    this.purchased = var3;
    this.expires = this.calculateExpirationDate(this.purchased, var1);
    this.orderId = var2.getOrderId();
    this.purchaseToken = var2.getToken();
    this.storeName = var2.getAppstoreName();
    this.packageName = var2.getPackageName();
  }*/

  @NonNull
  private Date calculateExpirationDate(Date var1, InAppPurchase var2) {
    Integer var3 = var2.getSubscriptionDuration();
    int var4 = var2.getSubscriptionDurationUnit();
    Integer var5 = var3;
    if(var3.intValue() == -1) {
      var5 = Integer.valueOf(100);
      var4 = 1;
    }

    return DateUtils.add(var1, var4, var5);
  }

  public Date getExpires() {
    return this.expires;
  }

  public String getIdentifier() {
    return this.identifier;
  }

  public String getOrderId() {
    return this.orderId;
  }

  public String getPackageName() {
    return this.packageName;
  }

  public String getPurchaseToken() {
    return this.purchaseToken;
  }

  public Date getPurchased() {
    return this.purchased;
  }

  public String getStoreName() {
    return this.storeName;
  }

  @JsonIgnore
  public boolean isActive() {
    Date var1 = this.getPurchased();
    Date var2 = new Date();
    Date var3 = this.getExpires();
    return var1 != null && (var3 == null || var2.getTime() <= var3.getTime());
  }

  public boolean isAutoRenewable() {
    return this.autoRenewable;
  }

  public boolean isFromPromoCode() {
    return this.fromPromoCode;
  }

  public void setAutoRenewable(boolean var1) {
    this.autoRenewable = var1;
  }

  public void setExpires(Date var1) {
    this.expires = var1;
  }

  public void setFromPromoCode(boolean var1) {
    this.fromPromoCode = var1;
  }

  public void setIdentifier(String var1) {
    this.identifier = var1;
  }

  public void setOrderId(String var1) {
    this.orderId = var1;
  }

  public void setPackageName(String var1) {
    this.packageName = var1;
  }

  public void setPurchaseToken(String var1) {
    this.purchaseToken = var1;
  }

  public void setPurchased(Date var1) {
    this.purchased = var1;
  }

  public void setStoreName(String var1) {
    this.storeName = var1;
  }
}
