package com.ipnossoft.api.featuremanager.iab.handlers;

import android.util.Log;
import com.ipnossoft.api.dynamiccontent.model.InAppPurchase;
import com.ipnossoft.api.featuremanager.FeatureActionListener;
import com.ipnossoft.api.featuremanager.FeatureManager;
import com.ipnossoft.api.featuremanager.data.Subscription;
import java.util.*;
import org.onepf.oms.appstore.googleUtils.*;

//cavaj
public class IABQueryInventoryFinishedHandler
        implements org.onepf.oms.appstore.googleUtils.IabHelper.QueryInventoryFinishedListener
{

  private FeatureManager featureManager;
  private boolean querySkuDetails;

  public IABQueryInventoryFinishedHandler(FeatureManager featuremanager, boolean flag)
  {
    querySkuDetails = false;
    featureManager = featuremanager;
    querySkuDetails = flag;
  }

  public void onQueryInventoryFinished(IabResult iabresult, Inventory inventory)
  {
    /*if(!iabresult.isFailure()) goto _L2; else goto _L1
    _L1:
    Log.e("IAB", (new StringBuilder()).append("Failed to get inventory. ").append(iabresult.getResponse()).append(": ").append(iabresult.getMessage()).toString());
    _L4:
    featureManager.setSetupFinished(true);
    featureManager.setQueryInventoryFinished(true);
    featureManager.notifyOnSetupFinished();
    return;
    _L2:
    final Subscription subscription;
    Log.d("IAB", (new StringBuilder()).append("Got the inventory: Purchases:").append(inventory.getAllPurchases().size()).append(" Owned:").append(inventory.getAllOwnedSkus().size()).toString());
    featureManager.setPurchaseInventory(inventory);
    if(querySkuDetails)
    {
      featureManager.setIsInitialized(true);
    }
    subscription = featureManager.getActiveSubscription();
    if(inventory.getAllOwnedSkus().size() != 0)
    {
      break MISSING_BLOCK_LABEL_264;
    }
    if(subscription == null) goto _L4; else goto _L3
    _L3:
    if(!subscription.isActive() || subscription.isFromPromoCode()) goto _L4; else goto _L5
    _L5:
    Log.d("IAB", (new StringBuilder()).append("User subscription doesn't exist in inventory: ").append(subscription.getIdentifier()).append(", unsubscribing.").toString());
    featureManager.unsubscribe(subscription.getIdentifier(), new FeatureActionListener() {

      final IABQueryInventoryFinishedHandler this$0;
      final Subscription val$subscription;

      public void onSuccess()
      {
        Log.w("IAB", (new StringBuilder()).append("Subscription ").append(subscription.getIdentifier()).append(" removed locally.").toString());
      }


      {
        this$0 = IABQueryInventoryFinishedHandler.this;
        subscription = subscription1;
        super();
      }
    });
          goto _L4
          iabresult;
    featureManager.setSetupFinished(true);
    featureManager.setQueryInventoryFinished(true);
    featureManager.notifyOnSetupFinished();
    throw iabresult;
    ArrayList arraylist;
    Iterator iterator;
    arraylist = new ArrayList();
    iterator = inventory.getAllOwnedSkus().iterator();
    _L9:
    String s;
    final Purchase purchase;
    InAppPurchase inapppurchase;
    if(!iterator.hasNext())
    {
      break MISSING_BLOCK_LABEL_640;
    }
    s = (String)iterator.next();
    Log.d("IAB", (new StringBuilder()).append("Previously purchased feature in inventory: ").append(s).toString());
    purchase = inventory.getPurchase(s);
    inapppurchase = featureManager.getInAppPurchase(s);
    if(inapppurchase == null)
    {
      break MISSING_BLOCK_LABEL_627;
    }
    boolean flag = featureManager.isPurchaseValid(purchase);
    if(!flag)
    {
      break MISSING_BLOCK_LABEL_534;
    }
    FeatureManager featuremanager = featureManager;
    if(purchase.getPurchaseTime() == 0L) goto _L7; else goto _L6
    _L6:
    iabresult = new Date(purchase.getPurchaseTime());
    _L14:
    featuremanager.notifyPurchaseCompleted(inapppurchase, purchase, iabresult);
    _L15:
    if(!inapppurchase.isSubscription()) goto _L9; else goto _L8
    _L8:
    iabresult = new Subscription(inapppurchase, purchase);
    if(!iabresult.isActive() || !flag)
    {
      break MISSING_BLOCK_LABEL_571;
    }
    Log.i("IAB", (new StringBuilder()).append("Previously purchased active subscription detected: ").append(s).toString());
    if(subscription == null) goto _L11; else goto _L10
    _L10:
    if(subscription.getIdentifier().equals(iabresult.getIdentifier())) goto _L9; else goto _L11
    _L11:
    if(subscription == null) goto _L13; else goto _L12
    _L12:
    if(featureManager.getInAppPurchase(subscription.getIdentifier()).getSubscriptionDuration().intValue() == -1) goto _L9; else goto _L13
    _L13:
    featureManager.setActiveSubscription(iabresult);
          goto _L9
    _L7:
    iabresult = new Date();
          goto _L14
    Log.e("IAB", (new StringBuilder()).append("Wrong developer payload for purchased feature [").append(purchase.getSku()).append("]").toString());
          goto _L15
    Log.w("IAB", (new StringBuilder()).append("Previously purchased inactive subscription detected: ").append(s).append(", unsubscribing...").toString());
    featureManager.unsubscribe(purchase.getSku(), new FeatureActionListener() {

      final IABQueryInventoryFinishedHandler this$0;
      final Purchase val$purchase;

      public void onSuccess()
      {
        Log.i("IAB", (new StringBuilder()).append("Purchase ").append(purchase.getSku()).append(" consumed").toString());
      }


      {
        this$0 = IABQueryInventoryFinishedHandler.this;
        purchase = purchase1;
        super();
      }
    });
          goto _L9
    arraylist.add(s);
          goto _L9
    featureManager.notifyUnresolvedPurchase(arraylist);
          goto _L4*/
  }
}
