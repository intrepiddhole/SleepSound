package com.ipnossoft.api.dynamiccontent;

import com.ipnossoft.api.dynamiccontent.model.InAppPurchase;

public abstract interface InAppPurchaseDownloadProgressTracker
{
  public abstract void downloadCancelled(InAppPurchase paramInAppPurchase);

  public abstract void downloadDone(InAppPurchase paramInAppPurchase, String[] paramArrayOfString);

  public abstract void downloadFailed(InAppPurchase paramInAppPurchase, Exception paramException);

  public abstract void downloadProgressChanged(InAppPurchase paramInAppPurchase, double paramDouble, DownloadStages paramDownloadStages);
}