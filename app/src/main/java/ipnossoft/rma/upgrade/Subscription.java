package ipnossoft.rma.upgrade;

import android.content.Context;
import android.support.v4.media.MediaBrowserCompat;
import android.util.Log;
import android.view.View;
import com.ipnossoft.api.dynamiccontent.model.InAppPurchase;
import com.ipnossoft.api.featuremanager.FeatureActionListener;
import com.ipnossoft.api.featuremanager.FeatureManager;
import com.ipnossoft.api.featuremanager.exceptions.FeatureManagerException;
import java.util.List;
import org.onepf.oms.appstore.googleUtils.SkuDetails;

// Referenced classes of package ipnossoft.rma.upgrade:
//            SubscriptionOfferResolver

public abstract class Subscription
        implements android.view.View.OnClickListener
{

  public static final String NA_PRICE = "N/A";
  private static volatile boolean purchaseFlowInProgress;
  private MediaBrowserCompat.SubscriptionCallback callback;
  Context context;
  private String errorMessage;
  private View exitButtonLayout;
  String replacedPrice;
  SkuDetails replacedSkuDetails;
  private InAppPurchase subscriptionTier1;
  private InAppPurchase subscriptionTier2;
  private InAppPurchase subscriptionTier3;
  View subscriptionView;
  private View tier1Button;
  String tier1Price;
  SkuDetails tier1SkuDetails;
  private View tier2Button;
  String tier2Price;
  SkuDetails tier2SkuDetails;
  private View tier3Button;
  String tier3MonthlyPrice;
  String tier3Price;
  SkuDetails tier3SkuDetails;

  Subscription(View view, MediaBrowserCompat.SubscriptionCallback subscriptioncallback, Context context1)
  {
    tier3Price = "N/A";
    tier3MonthlyPrice = "N/A";
    tier2Price = "N/A";
    tier1Price = "N/A";
    replacedPrice = "N/A";
    tier3SkuDetails = null;
    tier2SkuDetails = null;
    tier1SkuDetails = null;
    replacedSkuDetails = null;
    subscriptionView = view;
    callback = subscriptioncallback;
    context = context1;
    try
    {
      List<InAppPurchase> view_1 = SubscriptionOfferResolver.getThreeButtonSubscriptions();
      String subscriptioncallback_1 = SubscriptionOfferResolver.getReplacedSubscriptionIdentifier();
      tier3SkuDetails = FeatureManager.getInstance().getPurchaseDetails(((InAppPurchase)view_1.get(2)).getIdentifier());
      tier2SkuDetails = FeatureManager.getInstance().getPurchaseDetails(((InAppPurchase)view_1.get(1)).getIdentifier());
      tier1SkuDetails = FeatureManager.getInstance().getPurchaseDetails(((InAppPurchase)view_1.get(0)).getIdentifier());
    }
    // Misplaced declaration of an exception variable
    catch(Exception e)
    {
      Log.e("Subscription", "Unexpected ERROR.", e);
      return;
    }
    if(subscriptioncallback == null)
    {
      replacedSkuDetails = FeatureManager.getInstance().getPurchaseDetails(null);
    }
  }

  private InAppPurchase getSubscriptionPurchase(int i)
  {
    List list = SubscriptionOfferResolver.getThreeButtonSubscriptions();
    if(list != null && list.size() > 0)
    {
      return (InAppPurchase)list.get(i - 1);
    } else
    {
      return null;
    }
  }

  private boolean isPriceInvalid(View view)
  {
    boolean flag1 = false;
    boolean flag = flag1;
    if(errorMessage != null)
    {
      flag = flag1;

      if(!errorMessage.equals(""))
      {
        if(view == tier1Button)
        {
          if(tier3SkuDetails == null)
          {
            flag = true;
          } else
          {
            flag = false;
          }
        } else
        if(view == tier2Button)
        {
          if(tier2SkuDetails == null)
          {
            flag = true;
          } else
          {
            flag = false;
          }
        } else
        {
          flag = flag1;
          if(view == tier3Button)
          {
            if(tier1SkuDetails == null)
            {
              flag = true;
            } else
            {
              flag = false;
            }
          }
        }
      }
    }
    if(flag)
    {
      //callback.onSubscriptionFailure(new Exception(errorMessage), null, 0);
      return true;
    } else
    {
      return false;
    }
  }

  public static boolean isPurchaseFlowInProgress()
  {
    return purchaseFlowInProgress;
  }

  private static void setPurchaseFlowInProgress(boolean flag)
  {
    purchaseFlowInProgress = flag;
  }

  public void build()
  {
    getTier3Subscription();
    loadViewsFromSubscriptionView();
    setupSubscriptionView();
  }

  protected String getErrorMessage()
  {
    return errorMessage;
  }

  double getNumberOfMonth(InAppPurchase inapppurchase)
  {
    int i = inapppurchase.getSubscriptionDuration().intValue();
    int j = inapppurchase.getSubscriptionDurationUnit();
    if(j == 2)
    {
      return (double)i;
    }
    if(j == 1)
    {
      return (double)i * 12D;
    }
    if(j == 3)
    {
      return ((double)i * 12D) / 52D;
    } else
    {
      return 1.0D;
    }
  }

  boolean getPrices()
  {
    if(tier3SkuDetails != null)
    {
      tier3Price = tier3SkuDetails.getPrice();
    }
    if(tier2SkuDetails != null)
    {
      tier2Price = tier2SkuDetails.getPrice();
    }
    if(tier1SkuDetails != null)
    {
      tier1Price = tier1SkuDetails.getPrice();
    }
    if(replacedSkuDetails != null)
    {
      replacedPrice = replacedSkuDetails.getPrice();
    }
    return tier3SkuDetails != null && tier2SkuDetails != null && tier1SkuDetails != null;
  }

  public SkuDetails getSkuDetailsForSubscription(String s)
  {
    if(s != null)
    {
      if(tier3SkuDetails != null && s.equals(tier3SkuDetails.getSku()))
      {
        return tier3SkuDetails;
      }
      if(tier2SkuDetails != null && s.equals(tier2SkuDetails.getSku()))
      {
        return tier2SkuDetails;
      }
      if(tier1SkuDetails != null && s.equals(tier1SkuDetails.getSku()))
      {
        return tier1SkuDetails;
      }
    }
    return null;
  }

  protected InAppPurchase getTier1Subscription()
  {
    if(subscriptionTier1 == null)
    {
      subscriptionTier1 = getSubscriptionPurchase(1);
    }
    return subscriptionTier1;
  }

  protected InAppPurchase getTier2Subscription()
  {
    if(subscriptionTier2 == null)
    {
      subscriptionTier2 = getSubscriptionPurchase(2);
    }
    return subscriptionTier2;
  }

  protected InAppPurchase getTier3Subscription()
  {
    if(subscriptionTier3 == null)
    {
      subscriptionTier3 = getSubscriptionPurchase(3);
    }
    return subscriptionTier3;
  }

  protected abstract void loadViewsFromSubscriptionView();

  public void onClick(View view)
  {
    if(!isPriceInvalid(view))
    {
      if(view == tier1Button)
      {
        subscribe(1);
        return;
      }
      if(view == tier2Button)
      {
        subscribe(2);
        return;
      }
      if(view == tier3Button)
      {
        subscribe(3);
        return;
      }
      if(view == exitButtonLayout)
      {
        //callback.onSubscriptionCancel();
        return;
      }
    }
  }

  void setErrorMessage(String s)
  {
    errorMessage = s;
  }

  void setupClickListeners(View view, View view1, View view2, View view3)
  {
    tier1Button = view;
    tier2Button = view1;
    tier3Button = view2;
    exitButtonLayout = view3;
    view.setClickable(true);
    view1.setClickable(true);
    view2.setClickable(true);
    exitButtonLayout.setClickable(true);
    view.setOnClickListener(this);
    view1.setOnClickListener(this);
    view2.setOnClickListener(this);
    exitButtonLayout.setOnClickListener(this);
  }

  protected abstract void setupSubscriptionView();

  void subscribe(final int tier)
  {
    /*InAppPurchase inapppurchase;
    setPurchaseFlowInProgress(true);
    inapppurchase = getSubscriptionPurchase(tier);
    if(inapppurchase != null)
    {
      break MISSING_BLOCK_LABEL_61;
    }
    callback.onSubscriptionFailure(new Exception((new StringBuilder()).append("Tier ").append(tier).append(" purchase does not exist.").toString()), null, 0);
    setPurchaseFlowInProgress(false);
    return;
    final String featureId = inapppurchase.getIdentifier();
    callback.onSubscriptionProcessTriggered(featureId, tier);
    FeatureManager.getInstance().subscribe(featureId, inapppurchase.isSubscriptionAutoRenewable(), new FeatureActionListener() {

      final Subscription this$0;
      final String val$featureId;
      final int val$tier;

      public void onFailure(FeatureManagerException featuremanagerexception)
      {
        callback.onSubscriptionFailure(featuremanagerexception, featureId, tier);
      }

      public void onSuccess()
      {
        callback.onSubscriptionSuccess(featureId, tier);
      }


      {
        this$0 = Subscription.this;
        featureId = s;
        tier = i;
        super();
      }
    });
    setPurchaseFlowInProgress(false);
    return;
    Exception exception;
    exception;
    setPurchaseFlowInProgress(false);
    throw exception;*/
  }

  public interface SubscriptionCallback {
    void onSubscriptionCancel();

    void onSubscriptionFailure(Exception var1, String var2, int var3);

    void onSubscriptionProcessTriggered(String var1, int var2);

    void onSubscriptionSuccess(String var1, int var2);
  }
}
