package com.ipnossoft.api.featuremanager;

import android.content.*;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;

import com.ipnossoft.api.dynamiccontent.DownloadStages;
import com.ipnossoft.api.dynamiccontent.InAppPurchaseDownloadProgressTracker;
import com.ipnossoft.api.dynamiccontent.InAppPurchaseServiceAndroid;
import com.ipnossoft.api.dynamiccontent.model.InAppPurchase;
import com.ipnossoft.api.featuremanager.data.Subscription;
import com.ipnossoft.api.featuremanager.exceptions.FeatureManagerException;
import com.ipnossoft.api.featuremanager.exceptions.FeatureOperationAlreadyCompleted;
import com.ipnossoft.api.featuremanager.exceptions.FeatureOperationFailedException;
import com.ipnossoft.api.featuremanager.iab.IABConfig;
import com.ipnossoft.api.featuremanager.iab.MarketCustomParam;
import com.ipnossoft.api.featuremanager.iab.handlers.IABPurchaseFinishedHandler;
import com.ipnossoft.api.featuremanager.iab.handlers.IABQueryInventoryFinishedHandler;
import com.ipnossoft.api.featuremanager.util.DateUtils;
import com.ipnossoft.api.featuremanager.util.Utils;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.onepf.oms.Appstore;
import org.onepf.oms.OpenIabHelper;
import org.onepf.oms.appstore.AmazonAppstore;
import org.onepf.oms.appstore.GooglePlay;
import org.onepf.oms.appstore.SamsungApps;
import org.onepf.oms.appstore.googleUtils.*;

//cavaj
public class FeatureManager
{

  private static final String ACTIVE_SUBSCRIPTION = "active_subscription";
  public static final String PROPERTY_CUSTOM_MARKET = "custom_market";
  public static final String PROPERTY_FEATUREMANAGER_DEBUG_LOGGING = "featuremanager_debug_logging";
  public static final String TAG = "FeatureManager";
  private static FeatureManager instance;
  private Subscription activeSubscription;
  protected List availablePurchases;
  protected AtomicReference availablePurchasesHolder;
  protected FeatureManagerConfig config;
  private boolean debugLog;
  protected IABQueryInventoryFinishedHandler iabQueryOwnedInventoryHandler;
  protected IABQueryInventoryFinishedHandler iabQueryPurchasableInventoryHandler;
  protected InAppPurchaseServiceAndroid inAppPurchaseService;
  private AtomicBoolean initialized;
  private boolean isForOldUserWhoBoughtPremiumInAppPurchase;
  protected MarketCustomParam market;
  protected ConcurrentSkipListSet observers;
  protected org.onepf.oms.OpenIabHelper.Options openIABOptions;
  private OpenIabHelper openIab;
  protected AtomicReference purchaseInventoryHolder;
  protected AtomicBoolean queryInventoryFinished;
  protected AtomicBoolean setupFinished;

  public FeatureManager()
  {
    purchaseInventoryHolder = new AtomicReference(null);
    availablePurchasesHolder = new AtomicReference(null);
    observers = new ConcurrentSkipListSet(new Comparator<FeatureManagerObserver>() {
      @Override
      public int compare(FeatureManagerObserver featuremanagerobserver, FeatureManagerObserver featuremanagerobserver1)
      {
        return featuremanagerobserver != featuremanagerobserver1 ? -1 : 0;
      }
    });
    setupFinished = new AtomicBoolean(false);
    queryInventoryFinished = new AtomicBoolean(true);
    initialized = new AtomicBoolean(false);
    iabQueryOwnedInventoryHandler = new IABQueryInventoryFinishedHandler(this, false);
    iabQueryPurchasableInventoryHandler = new IABQueryInventoryFinishedHandler(this, true);
    debugLog = false;
    isForOldUserWhoBoughtPremiumInAppPurchase = false;
  }

  private boolean checkSubscription(String s, FeatureActionListener featureactionlistener)
  {
    InAppPurchase inapppurchase = getInAppPurchase(s);
    if(inapppurchase == null)
    {
      featureactionlistener.onFailure(new FeatureManagerException((new StringBuilder()).append("InAppPurchase with id=\"").append(s).append("\" does not exist.").toString()));
      return false;
    }
    if(!inapppurchase.isSubscription())
    {
      featureactionlistener.onFailure(new FeatureManagerException((new StringBuilder()).append("InAppPurchase with id=\"").append(s).append("\" is not a subscription.").toString()));
      return false;
    }
    if(getActiveSubscription() != null && getActiveSubscription().isActive() && getActiveSubscription().getIdentifier().equals(s))
    {
      featureactionlistener.onFailure(new FeatureOperationAlreadyCompleted((new StringBuilder()).append("InAppPurchase with id=\"").append(s).append("\" is already subscribed.").toString()));
      return false;
    } else
    {
      return true;
    }
  }

  private void doBuyFeature(final String featureId, String s, final FeatureActionListener listener)
  {
    String s1 = generateDeveloperPayloadForFeature(featureId);
    openIab.launchPurchaseFlow(config.getActivity(), featureId, s, 10001, new IABPurchaseFinishedHandler(this,
            new FeatureManagerListenerResult(){
              public void onComplete()
              {
                listener.onComplete();
              }

              public void onFailure(FeatureManagerException featuremanagerexception)
              {
                listener.onFailure(featuremanagerexception);
              }

              public void onSuccess(Object obj)
              {
                onSuccess((Purchase)obj);
              }

              public void onSuccess(Purchase purchase)
              {
                listener.onSuccess();
                listener.onSuccess(purchase);
                InAppPurchase inapppurchase = getInAppPurchase(featureId);
                FeatureManager featuremanager = FeatureManager.this;
                Date date;
                if(purchase.getPurchaseTime() != 0L)
                {
                  date = new Date(purchase.getPurchaseTime());
                } else
                {
                  date = new Date();
                }
                featuremanager.notifyPurchaseCompleted(inapppurchase, purchase, date);
              }
            }), s1);
  }

  private void doUnlockFeature(String s, String s1)
          throws FeatureManagerException
  {
    InAppPurchase inapppurchase = getInAppPurchase(s);
    if(inapppurchase != null)
    {
      notifyPurchaseUnlocked(inapppurchase, s1);
      return;
    } else
    {
      throw new FeatureOperationFailedException((new StringBuilder()).append("InAppPurchase identifier is invalid: ").append(s).toString());
    }
  }

  private Appstore getCustomOrDefaultStore()
  {
    org.onepf.oms.OpenIabHelper.Options options;
    switch(getMarketCustomParam())
    {
      default:
        return new GooglePlay(config.getContext(), "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvfNpyKSb2ic5TRhKK6RBciTV+GmmFBCfLvzX" +
                "y4i3ySX24F5/xDxj9eLgdYT6tM9rCRpgRMVSFr6PS9g4FK8WYOxzhr7hzXuiCk63DSwJFk29Uzrjk9Cn" +
                "kOGjJoIO+BBuaMbVVW5hMhwDh6YqmJwpOU67MNRlr1Wq+CLl6e6zpxNuiO+qWx/JSZqVOvwfM2TCaDGn" +
                "Fg+e5yB0Xhls1Bc26Vs/c/R3f8WmEx4NwHZv5gZybnc3x6O1Qqp+FvkXs5yv+kz3du6YdsUNfNOT6vec" +
                "TYDjm7Siv4SKEPrvV/MLLpyFrpLMZqY5E8BzX19rBzGGifXucEHjHz5t1lG5KB3BGQIDAQAB"
        ) {
          public boolean isPackageInstaller(String s)
          {
            return true;
          }
        };

      case AMAZON: // '\001'
        return new AmazonAppstore(config.getContext()) {
                public boolean isBillingAvailable(String s)
                {
                  return true;
                }

                public boolean isPackageInstaller(String s)
                {
                  return true;
                }
            };

      case SAMSUNG: // '\002'
        options = new org.onepf.oms.OpenIabHelper.Options();
        break;
    }
    return new SamsungApps(config.getActivity(), options)
    {
        public boolean isBillingAvailable(String s)
        {
          return true;
        }

      public boolean isPackageInstaller(String s)
      {
        return true;
      }
    };
  }

  public static FeatureManager getInstance()
  {
    if(instance == null)
    {
      instance = new FeatureManager();
    }
    return instance;
  }

  private Date getSubscriptionExpiryDate(InAppPurchase inapppurchase, Purchase purchase)
  {
    Integer integer = inapppurchase.getSubscriptionDuration();
    int i = inapppurchase.getSubscriptionDurationUnit();
    Integer inapppurchase_1 = integer;
    if(integer.intValue() == -1)
    {
      inapppurchase_1 = Integer.valueOf(100);
      i = 1;
    }
    return DateUtils.add(new Date(purchase.getPurchaseTime()), i, inapppurchase_1);
  }

  private void notifyPurchasesAvailable(List list)
  {
    for(Iterator iterator = observers.iterator(); iterator.hasNext(); ((FeatureManagerObserver)iterator.next()).onPurchasesAvailable(list)) { }
  }

  private void queryOwnedAndPurchasableInventory(List list)
  {
    ArrayList arraylist = new ArrayList();
    for(Iterator list_1 = list.iterator(); list_1.hasNext(); arraylist.add(((InAppPurchase)list_1.next()).getIdentifier())) { }
    try
    {
      openIab.queryInventoryAsync(true, arraylist, arraylist, iabQueryPurchasableInventoryHandler);
      return;
    }
    // Misplaced declaration of an exception variable
    catch(Exception e)
    {
      Log.e("FeatureManager", "", e);
    }
  }

  private void queryOwnedInventory()
  {
    openIab.queryInventoryAsync(false, iabQueryOwnedInventoryHandler);
  }

  private void setActiveSubscription(Subscription subscription, boolean flag)
  {
    Subscription subscription1 = activeSubscription;
    if(flag)
    {
      saveActiveSubscription(subscription);
    }
    activeSubscription = subscription;
    if(subscription == null && subscription1 != null)
    {
      notifySubscriptionChanged(subscription1, false);
    } else
    {
      if(subscription != null && subscription != subscription1)
      {
        notifySubscriptionChanged(subscription, subscription.isActive());
        return;
      }
      if(subscription == null)
      {
        notifySubscriptionChanged(null, false);
        return;
      }
    }
  }

  private void setActiveSubscription(Purchase purchase, InAppPurchase inapppurchase)
  {
    setActiveSubscription(new Subscription(inapppurchase));
  }

  private boolean setupDebugLogging()
  {
    return true;
  }

  private OpenIabHelper setupOpenIABHelper(org.onepf.oms.OpenIabHelper.Options options, FeatureManagerCallback featuremanagercallback)
  {
    boolean flag = featuremanagercallback.preOpenIABSetup(config.getActivity());
    OpenIabHelper options_1 = new OpenIabHelper(config.getActivity(), options);
    if(flag)
    {
      options_1.startSetup(new IabHelper.OnIabSetupFinishedListener() {
        @Override
        public void onIabSetupFinished(IabResult iabresult) {
          if(!iabresult.isSuccess())
          {
            Log.e("FeatureManager", (new StringBuilder()).append("Failed to setup OpenIAB helper. ").append(iabresult.getResponse()).append(": ").append(iabresult.getMessage()).toString());
            setSetupFinished(true);
            return;
          } else
          {
            Log.d("FeatureManager", (new StringBuilder()).append("Querying inventory. ").append(iabresult.getResponse()).append(": ").append(iabresult.getMessage()).toString());
            (new AsyncTask() {

              protected Object doInBackground(Object aobj[])
              {
                return doInBackground((Void[])aobj);
              }

              protected Void doInBackground(Void avoid[])
              {
                queryInventory();
                return null;
              }
            }).execute(new Void[0]);
            return;
          }
        }
      });
      return options_1;
    } else
    {
      return null;
    }
  }

  private org.onepf.oms.OpenIabHelper.Options setupOpenIABOptions()
  {
    return (new org.onepf.oms.OpenIabHelper.Options.Builder()).setStoreSearchStrategy(2).addStoreKeys(IABConfig.getStoreKeys()).addAvailableStores(new Appstore[] {
            getCustomOrDefaultStore()
    }).build();
  }

  public void buyFeature(String s, String s1, FeatureActionListener featureactionlistener)
  {
    try {
      doBuyFeature(s, s1, featureactionlistener);
      featureactionlistener.onSuccess();
      featureactionlistener.onComplete();
    }catch (Exception e){
      try {
        featureactionlistener.onFailure(FeatureManagerException.fromException(e));
        featureactionlistener.onComplete();
      } catch (Exception e1) {
        featureactionlistener.onComplete();
      }
    }
  }

  public void cancelDownloadFeature()
  {
    inAppPurchaseService.cancelDownload();
  }

  public void configureFeatureManager(FeatureManagerConfig featuremanagerconfig, FeatureManagerCallback featuremanagercallback, boolean flag)
  {
    config = featuremanagerconfig;
    setupDebugLogging();
    isForOldUserWhoBoughtPremiumInAppPurchase = flag;
    setPurchaseInventory(new Inventory());
    featuremanagerconfig.setAppId((new StringBuilder()).append(featuremanagerconfig.getAppId()).append("-").append(getMarketCustomParam()).toString());
    inAppPurchaseService = new InAppPurchaseServiceAndroid(featuremanagerconfig);
    availablePurchases = new ArrayList();
    setupOpenIAB(featuremanagercallback);
  }

  public void consumeInAppPurchases()
          throws IabException
  {
    Object obj = getPurchases();
    if(obj != null && !((List) (obj)).isEmpty())
    {
      Purchase purchase;
      for(obj = ((List) (obj)).iterator(); ((Iterator) (obj)).hasNext(); openIab.consume(purchase))
      {
        purchase = (Purchase)((Iterator) (obj)).next();
      }

    }
  }

  public void downloadFeature(String s, final FeatureManagerListenerResult listener)
  {
    inAppPurchaseService.downloadInAppPurchase(s, "downloads", new InAppPurchaseDownloadProgressTracker() {

      public void downloadCancelled(InAppPurchase inapppurchase)
      {
        if(listener != null)
        {
          listener.onCancel();
          listener.onComplete();
        }
      }

      public void downloadDone(InAppPurchase inapppurchase, String as[])
      {
        if(listener != null)
        {
          notifyFeatureDownloaded(inapppurchase, as);
          listener.onSuccess(as);
          listener.onComplete();
        }
      }

      public void downloadFailed(InAppPurchase inapppurchase, Exception exception)
      {
        if(listener != null)
        {
          listener.onFailure(FeatureManagerException.fromException(exception));
          listener.onComplete();
        }
      }

      public void downloadProgressChanged(InAppPurchase inapppurchase, double d, DownloadStages downloadstages)
      {
        if(listener != null)
        {
          listener.onProgressChange(d, downloadstages.ordinal());
        }
      }

    });
  }

  public void fetchAvailableFeatures()
  {
    fetchAvailableFeatures(null);
  }

  public void fetchAvailableFeatures(AsyncOperationListener asyncoperationlistener)
  {
    tryFetchAvailableFeatures(asyncoperationlistener);
  }

  public String generateDeveloperPayloadForFeature(String s)
  {
    return IABDeveloperPayloadGenerator.generate(s);
  }

  public Subscription getActiveSubscription()
  {
    return activeSubscription;
  }

  public List getAvailableFeatures()
  {
    synchronized (this) {
      Object obj;
      obj = new ArrayList();
      for(Iterator iterator = inAppPurchaseService.availableInAppPurchases().iterator(); iterator.hasNext(); ((ArrayList) (obj)).add(((InAppPurchase)iterator.next()).getIdentifier())) { }
      return ((List) (obj));
    }
  }

  public List getAvailableSubscriptions()
  {
    ArrayList arraylist = new ArrayList();
    Iterator iterator = getInAppPurchases().iterator();
    do
    {
      if(!iterator.hasNext())
      {
        break;
      }
      InAppPurchase inapppurchase = (InAppPurchase)iterator.next();
      if(inapppurchase.isSubscription())
      {
        arraylist.add(inapppurchase);
      }
    } while(true);
    return arraylist;
  }

  public FeatureManagerConfig getConfiguration()
  {
    return config;
  }

  public InAppPurchase getInAppPurchase(String s)
  {
    return inAppPurchaseService.inAppPurchaseWithIdentifier(s);
  }

  public List getInAppPurchases()
  {
    List list = (List)availablePurchasesHolder.get();
    if(list != null)
    {
      return list;
    } else
    {
      return new ArrayList();
    }
  }

  public MarketCustomParam getMarketCustomParam()
  {
    int i;
    if(market != null)
    {
      return market;
    }
    i = getResourceId("string", "custom_market");
    if(i != 0)
      market = MarketCustomParam.fromString(config.getContext().getString(i));
    if(market == null)
    {
      market = MarketCustomParam.GOOGLE;
    }
    return market;
  }

  public OpenIabHelper getOpenIab()
  {
    return openIab;
  }

  public List getOwnedPurchases()
  {
    ArrayList arraylist = new ArrayList();
    if(getPurchaseInventory() != null)
    {
      for(Iterator iterator = getPurchaseInventory().getAllOwnedSkus().iterator(); iterator.hasNext(); arraylist.add(getPurchase((String)iterator.next()))) { }
    }
    return arraylist;
  }

  public Purchase getPurchase(String s)
  {
    return getPurchaseInventory().getPurchase(s);
  }

  public SkuDetails getPurchaseDetails(String s)
  {
    return getPurchaseInventory().getSkuDetails(s);
  }

  public Inventory getPurchaseInventory()
  {
    return (Inventory)purchaseInventoryHolder.get();
  }

  public List getPurchases()
  {
    if(getPurchaseInventory() == null)
    {
      return new ArrayList();
    } else
    {
      return getPurchaseInventory().getAllPurchases();
    }
  }

  public int getResourceId(String s, String s1)
  {
    return config.getContext().getResources().getIdentifier(s1, s, config.getContext().getPackageName());
  }

  public boolean handlePurchaseFlowActivityResult(int i, int j, Intent intent)
  {
    return openIab.handleActivityResult(i, j, intent);
  }

  public boolean hasActiveSubscription()
  {
    Subscription subscription = getActiveSubscription();
    return subscription != null && subscription.isActive();
  }

  public boolean isFeaturePurchased(String s)
  {
    return getPurchaseInventory() != null && getPurchaseInventory().getAllOwnedSkus().contains(s);
  }

  public boolean isFeatureUnlocked(String s)
  {
    return getPurchase(s) != null && !isSubscriptionExpired(s);
  }

  public boolean isInitialized()
  {
    return initialized.get();
  }

  public boolean isPurchaseValid(Purchase purchase)
  {
    if(purchase.getDeveloperPayload() != null)
    {
      String s = generateDeveloperPayloadForFeature(purchase.getSku());
      return purchase.getDeveloperPayload().equals(s);
    } else
    {
      return true;
    }
  }

  public boolean isQueryInventoryFinished()
  {
    return queryInventoryFinished.get();
  }

  public boolean isSetupFinished()
  {
    return setupFinished.get();
  }

  public boolean isSubscriptionExpired(String s)
  {
    return false;
    /*boolean flag1 = true;
    InAppPurchase inapppurchase;
    inapppurchase = getInAppPurchase(s);
    Purchase s_1 = getPurchase(s);
    boolean flag;
    flag = flag1;
    if(s == null)
    {
      break MISSING_BLOCK_LABEL_85;
    }
    if(s_1.getPurchaseTime() == 0L)
    {
      return true;
    }
    long l;
    long l1;
    l = getSubscriptionExpiryDate(inapppurchase, s).getTime();
    l1 = (new Date()).getTime();
    flag = flag1;
    if(l >= l1)
    {
      return false;
    }
    break MISSING_BLOCK_LABEL_85;
    Throwable s_2 = null;
    Log.e("FeatureManager", "Failed to check has subscription expired.", s_2);
    flag = false;
    return flag;*/
  }

  public boolean isSubscriptionPurchased(String s)
  {
    return getPurchase(s) != null;
  }

  public boolean isSubscriptionUpgradeAvailable()
  {
    return false;
    /*boolean flag1 = false;
    boolean flag = flag1;
    if(getOpenIab() != null)
    {
      Appstore appstore = getOpenIab().getConnectedAppstore();
      flag = flag1;
      if(appstore != null)
      {
        flag = flag1;
        if(appstore instanceof GooglePlay)
        {
          flag = flag1;
          if(((GooglePlay)appstore).getBillingAPIVersion() == 5)
          {
            flag = true;
          }
        }
      }
    }
    return flag;*/
  }

  public boolean isSubscriptionValid(String s)
  {
    return getInAppPurchase(s) != null;
  }

  public void loadActiveSubscription()
  {
    /*if(activeSubscription != null)
    {
      break MISSING_BLOCK_LABEL_68;
    }
    String s = config.getContext().getSharedPreferences("active_subscription", 0).getString("JSON", null);
    if(s != null)
    {
      try
      {
        setActiveSubscription((Subscription)Utils.jsonToObject(s, Subscription.class), false);
        return;
      }
      catch(Exception exception)
      {
        Log.e("FeatureManager", "Error while trying to load active subscription data.", exception);
      }
      break MISSING_BLOCK_LABEL_67;
    }
    setActiveSubscription(((Subscription) (null)), false);
    return;
    return;
    setActiveSubscription(activeSubscription, false);
    return;*/
  }

  protected void notifyFeatureDownloaded(InAppPurchase inapppurchase, String as[])
  {
    for(Iterator iterator = observers.iterator(); iterator.hasNext(); ((FeatureManagerObserver)iterator.next()).onFeatureDownloaded(inapppurchase, as)) { }
  }

  public void notifyOnSetupFinished()
  {
    for(Iterator iterator = observers.iterator(); iterator.hasNext(); ((FeatureManagerObserver)iterator.next()).onFeatureManagerSetupFinished()) { }
  }

  public void notifyPurchaseCompleted(InAppPurchase inapppurchase, Purchase purchase, Date date)
  {
    for(Iterator iterator = observers.iterator(); iterator.hasNext(); ((FeatureManagerObserver)iterator.next()).onPurchaseCompleted(inapppurchase, purchase, date)) { }
  }

  protected void notifyPurchaseUnlocked(InAppPurchase inapppurchase, String s)
  {
    for(Iterator iterator = observers.iterator(); iterator.hasNext(); ((FeatureManagerObserver)iterator.next()).onFeatureUnlocked(inapppurchase, s)) { }
  }

  protected void notifySubscriptionChanged(Subscription subscription, boolean flag)
  {
    for(Iterator iterator = observers.iterator(); iterator.hasNext(); ((FeatureManagerObserver)iterator.next()).onSubscriptionChanged(subscription, flag)) { }
  }

  public void notifyUnresolvedPurchase(List list)
  {
    for(Iterator iterator = observers.iterator(); iterator.hasNext(); ((FeatureManagerObserver)iterator.next()).onUnresolvedPurchases(list)) { }
  }

  public void onDestroy()
  {
    if(openIab != null)
    {
      openIab.dispose();
      openIab = null;
    }
  }

  public void queryInventory()
  {
    List list = getAvailableSubscriptions();
    if(list.size() == 0)
    {
      queryOwnedInventory();
      return;
    }
    try
    {
      queryOwnedAndPurchasableInventory(list);
      return;
    }
    catch(Exception exception)
    {
      Log.e("FeatureManager", "", exception);
    }
    return;
  }

  public void redeem3MonthsSubscription()
  {
    Subscription subscription = getActiveSubscription();
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.MONTH, 3);
    if(subscription == null || subscription.getExpires().before(calendar.getTime()))
    {
      Object obj = new InAppPurchase();
      ((InAppPurchase) (obj)).setSubscriptionDuration(Integer.valueOf(3));
      ((InAppPurchase) (obj)).setIdentifier("ipnossoft.rma.free.subscription.tier2");
      obj = new Subscription(((InAppPurchase) (obj)));
      ((Subscription) (obj)).setFromPromoCode(true);
      setActiveSubscription(((Subscription) (obj)));
    }
  }

  public String redeemFeature(String s)
  {
    InAppPurchase inapppurchase = getInAppPurchase(s);
    Purchase purchase = getPurchase(s);
    if(inapppurchase != null && inapppurchase.isSubscription())
    {
      Subscription subscription = new Subscription(inapppurchase);
      subscription.setFromPromoCode(true);
      setActiveSubscription(subscription);
    }
    if(inapppurchase != null)
    {
      if(purchase != null)
      {
        return purchase.getAppstoreName();
      } else
      {
        return inapppurchase.getName();
      }
    } else
    {
      return String.valueOf(s);
    }
  }

  public void redeemLifetimeSubscription()
  {
    Object obj = new InAppPurchase();
    ((InAppPurchase) (obj)).setSubscriptionDuration(Integer.valueOf(1200));
    ((InAppPurchase) (obj)).setIdentifier("ipnossoft.rma.free.subscription.tier3");
    obj = new Subscription(((InAppPurchase) (obj)));
    ((Subscription) (obj)).setFromPromoCode(true);
    setActiveSubscription(((Subscription) (obj)));
  }

  public void registerObserver(FeatureManagerObserver featuremanagerobserver)
  {
    observers.add(featuremanagerobserver);
  }

  public void saveActiveSubscription(Subscription subscription)
  {
    android.content.SharedPreferences.Editor editor = config.getContext().getSharedPreferences("active_subscription", 0).edit();
    if(subscription != null)
    {
      editor.putString("JSON", Utils.objectToJson(subscription));
    } else
    {
      editor.putString("JSON", null);
    }
    editor.apply();
  }

  public void setActiveSubscription(Subscription subscription)
  {
    setActiveSubscription(subscription, true);
  }

  public void setAvailablePurchases(List list)
  {
    availablePurchasesHolder.set(list);
  }

  public void setIsInitialized(boolean flag)
  {
    initialized.set(flag);
  }

  public void setPurchaseInventory(Inventory inventory)
  {
    purchaseInventoryHolder.set(inventory);
  }

  public void setQueryInventoryFinished(boolean flag)
  {
    queryInventoryFinished.set(flag);
  }

  public void setSetupFinished(boolean flag)
  {
    setupFinished.set(flag);
  }

  public void setupOpenIAB(FeatureManagerCallback featuremanagercallback)
  {
    try
    {
      openIABOptions = setupOpenIABOptions();
      openIab = setupOpenIABHelper(openIABOptions, featuremanagercallback);
      return;
    }
    // Misplaced declaration of an exception variable
    catch(Exception e)
    {
      Log.e("FeatureManager", "OpenIAB failed to setup.", e);
    }
  }

  public void subscribe(final String featureId, boolean flag, final FeatureActionListener listener)
  {
    if(!checkSubscription(featureId, listener))
    {
      return;
    }
    String s;
    if(flag)
    {
      s = "subs";
    } else
    {
      s = "inapp";
    }
    buyFeature(featureId, s, new FeatureActionListener(){
      public void onComplete()
      {
        listener.onComplete();
      }

      public void onFailure(FeatureManagerException featuremanagerexception)
      {
        listener.onFailure(featuremanagerexception);
      }

      public void onProgressChange(double d, int i)
      {
        listener.onProgressChange(d, i);
      }

      public void onSuccess(Object obj)
      {
        Purchase purchase = (Purchase)obj;
        InAppPurchase inapppurchase = getInAppPurchase(featureId);
        setActiveSubscription(purchase, inapppurchase);
        listener.onSuccess();
        listener.onSuccess(obj);
      }
    });
  }

  public void tryFetchAvailableFeatures(final AsyncOperationListener listener)
  {
    if(inAppPurchaseService != null)
    {
      inAppPurchaseService.fetchAvailableInAppPurchases(new Observer(){
        public void update(Observable observable, Object obj)
        {
          boolean flag;
          flag = ((Boolean)obj).booleanValue();
          if(!flag)
            return;
          setAvailablePurchases(inAppPurchaseService.availableInAppPurchases());
          notifyPurchasesAvailable(inAppPurchaseService.availableInAppPurchases());
          Log.d("FeatureManager", "Querying inventory with known SKUs.");
          queryInventory();
          if(listener != null)
          {
            listener.onCompleted(obj, flag);
          }
          return;
        }
      });
    }

  }

  public void unlockFeature(String s, String s1, FeatureActionListener featureactionlistener)
  {
    try {
      doUnlockFeature(s, s1);
      featureactionlistener.onSuccess();
      featureactionlistener.onComplete();
      return;
    }catch (Exception e){
      e.printStackTrace();
    }
  }

  public void unregisterObserver(FeatureManagerObserver featuremanagerobserver)
  {
    observers.remove(featuremanagerobserver);
  }

  public void unsubscribe(FeatureActionListener featureactionlistener)
  {
    if(hasActiveSubscription())
    {
      unsubscribe(getActiveSubscription().getIdentifier(), featureactionlistener);
    }
  }

  public void unsubscribe(String s, final FeatureActionListener listener)
  {

  }

  public void upgradeAutorenewableSubscription(final String featureId, final FeatureActionListener listener)
  {
    if(!checkSubscription(featureId, listener))
    {
      return;
    }
    if(!isSubscriptionUpgradeAvailable())
    {
      listener.onFailure(new FeatureManagerException("Current App Store does not support subscription upgrade/downgrade"));
      return;
    } else {
      String s = generateDeveloperPayloadForFeature(featureId);
      ArrayList arraylist = new ArrayList();
      arraylist.add(getActiveSubscription().getIdentifier());
      //openIab.launchSubscriptionUpgradePurchaseFlow(config.getActivity(), arraylist, featureId, 10001, new IABPurchaseFinishedHandler(this, new _cls9()), s);
      return;
    }
  }

  public boolean wasOpenIABCorrectlySetup()
  {
    return openIab != null;
  }

}
