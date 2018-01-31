package com.ipnossoft.api.featuremanager.iab.handlers;

import android.util.Log;
import com.ipnossoft.api.featuremanager.FeatureManager;
import com.ipnossoft.api.featuremanager.FeatureManagerListenerResult;
import com.ipnossoft.api.featuremanager.exceptions.FeatureManagerException;
import org.onepf.oms.appstore.googleUtils.IabResult;
import org.onepf.oms.appstore.googleUtils.Purchase;
import org.onepf.oms.appstore.googleUtils.IabHelper.OnIabPurchaseFinishedListener;

public class IABPurchaseFinishedHandler implements OnIabPurchaseFinishedListener {
  private FeatureManager featureManager;
  private FeatureManagerListenerResult<Purchase> listener;

  public IABPurchaseFinishedHandler(FeatureManager var1, FeatureManagerListenerResult<Purchase> var2) {
    this.featureManager = var1;
    this.listener = var2;
  }

  public void onIabPurchaseFinished(IabResult var1, Purchase var2) {
    try {
      Log.e("IAB", "Purchase response: " + var1.getResponse() + " SUCCESS=" + var1.isSuccess() + "MESSAGE=" + var1.getMessage());
      if(!var1.isFailure() && var1.getResponse() != 1) {
        if(var1.isSuccess()) {
          if(this.featureManager.isPurchaseValid(var2)) {
            this.listener.onSuccess(var2);
            this.featureManager.queryInventory();
          } else {
            this.listener.onFailure(FeatureManagerException.fromBillingResponseCode(100, this.featureManager.getConfiguration().getContext()));
          }
        }
      } else {
        int var3 = var1.getResponse();
        this.listener.onFailure(FeatureManagerException.fromBillingResponseCode(var3, this.featureManager.getConfiguration().getContext()));
      }
    } finally {
      this.listener.onComplete();
    }

  }
}
