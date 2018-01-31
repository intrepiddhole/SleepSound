package com.ipnossoft.api.dynamiccontent;

import android.content.Context;
import com.ipnossoft.api.httputils.ServiceConfig;

public class InAppPurchaseServiceConfig extends ServiceConfig {
  private Context context;
  private String purchasePassword;

  public InAppPurchaseServiceConfig(InAppPurchaseServiceConfig var1) {
    this(var1.getServiceUrl(), var1.getUsername(), var1.getApiKey(), var1.getAppId(), var1.purchasePassword, var1.context);
  }

  public InAppPurchaseServiceConfig(ServiceConfig var1, String var2, Context var3) {
    this(var1.getServiceUrl(), var1.getUsername(), var1.getApiKey(), var1.getAppId(), var2, var3);
  }

  public InAppPurchaseServiceConfig(String var1, String var2, String var3, String var4, String var5, Context var6) {
    super(var1, var2, var3, var4);
    this.setPurchasePassword(var5);
    this.setContext(var6);
  }

  public Context getContext() {
    return this.context;
  }

  public String getPurchasePassword() {
    return this.purchasePassword;
  }

  public void setContext(Context var1) {
    this.context = var1.getApplicationContext();
  }

  public void setPurchasePassword(String var1) {
    this.purchasePassword = var1;
  }
}