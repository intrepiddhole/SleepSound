package com.ipnossoft.api.dynamiccontent;

import com.ipnossoft.api.dynamiccontent.model.InAppPurchase;
import java.util.List;

public abstract interface InAppPurchaseService
{
  public abstract List<InAppPurchase> availableInAppPurchases();
  
  public abstract boolean didFetchAvailableInAppPurchases();
  
  public abstract void downloadInAppPurchase(String paramString1, String paramString2, InAppPurchaseDownloadProgressTracker paramInAppPurchaseDownloadProgressTracker);
  
  public abstract void fetchAvailableInAppPurchases();
  
  public abstract InAppPurchase inAppPurchaseWithIdentifier(String paramString);
}