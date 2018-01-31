package com.ipnossoft.api.featuremanager;

import android.app.Activity;
import android.support.annotation.StringRes;
import com.ipnossoft.api.dynamiccontent.InAppPurchaseServiceConfig;

public class FeatureManagerConfig extends InAppPurchaseServiceConfig {
  private Activity activity;

  public FeatureManagerConfig(String var1, @StringRes int var2, @StringRes int var3, @StringRes int var4, @StringRes int var5, Activity var6) {
    this(var1, var6.getResources().getString(var2), var6.getResources().getString(var3), var6.getResources().getString(var4), var6.getResources().getString(var5), var6);
  }

  public FeatureManagerConfig(String var1, String var2, String var3, String var4, String var5, Activity var6) {
    super(var1, var2, var3, var4, var5, var6.getApplicationContext());
    this.setActivity(var6);
  }

  public Activity getActivity() {
    return this.activity;
  }

  public void setActivity(Activity var1) {
    this.activity = var1;
  }
}
