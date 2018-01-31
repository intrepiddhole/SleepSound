package com.ipnossoft.api.featuremanager;

import com.ipnossoft.api.dynamiccontent.model.InAppPurchase;
import com.ipnossoft.api.featuremanager.data.Subscription;
import java.util.Date;
import java.util.List;
import org.onepf.oms.appstore.googleUtils.Purchase;

public interface FeatureManagerObserver {
  void onFeatureDownloaded(InAppPurchase var1, String[] var2);

  void onFeatureManagerSetupFinished();

  void onFeatureUnlocked(InAppPurchase var1, String var2);

  void onPurchaseCompleted(InAppPurchase var1, Purchase var2, Date var3);

  void onPurchasesAvailable(List<InAppPurchase> var1);

  void onSubscriptionChanged(Subscription var1, boolean var2);

  void onUnresolvedPurchases(List<String> var1);
}
