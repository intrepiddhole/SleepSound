package ipnossoft.rma;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;
import com.aerserv.sdk.AerServBanner;
import com.aerserv.sdk.AerServConfig;
import com.aerserv.sdk.AerServEvent;
import com.aerserv.sdk.AerServEventListener;
import com.amplitude.api.Amplitude;
import com.appsflyer.AppsFlyerLib;
import com.ipnossoft.api.featuremanager.FeatureManager;
import com.ipnossoft.api.featuremanager.FeatureManagerObserver;
import com.ipnossoft.api.featuremanager.data.Subscription;
import com.ipnossoft.api.soundlibrary.SoundLibrary;
import ipnossoft.rma.util.AppFlyerLogger;
import ipnossoft.rma.util.AppTurboUnlockTools;
import ipnossoft.rma.util.RelaxAnalytics;
import ipnossoft.rma.web.MoreFragment;
import ipnossoft.rma.web.RelaxFreeMoreFragment;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class MainActivityFree extends MainActivity implements FeatureManagerObserver, AerServEventListener, AppFlyerLogger {
  private static final int NOTIFICATION_ID = 1;
  private static String PREF_APP_TURBO_GAVE_PAID_FEATURE = "isPremiumUsingAppTurbo";
  private static final String PREF_IS_PREMIUM = "is_premium";
  private static final String PREF_PREMIUM_PRICE = "premium_price";
  private static final int RC_REQUEST = 10001;
  private static final int UPGRADE_TO_PREMIUM = 1;
  private static MainActivityFree instance;
  private View adFillerLayout;
  private AerServBanner aerServBanner = null;
  private Boolean isSetupDone = Boolean.valueOf(false);
  private boolean openingUpgradeActivity = false;

  public MainActivityFree() {
  }

  private void alignCloudMenuBottom() {
    View var1 = this.findViewById(2131689731);
    LayoutParams var2 = (LayoutParams)var1.getLayoutParams();
    var2.addRule(12);
    var1.setLayoutParams(var2);
  }

  private void alignCloudMenuToAdView() {
    View var1 = this.findViewById(2131689731);
    LayoutParams var2 = (LayoutParams)var1.getLayoutParams();
    var2.addRule(12, 0);
    var2.addRule(2, this.adFillerLayout.getId());
    var1.setLayoutParams(var2);
  }

  private void alignMainGraphicsLayoutToAdView() {
    RelativeLayout var1 = (RelativeLayout)this.findViewById(2131689728);
    LayoutParams var2 = (LayoutParams)var1.getLayoutParams();
    var2.addRule(12, 0);
    var2.addRule(2, this.adFillerLayout.getId());
    var1.setLayoutParams(var2);
  }

  private void alignMainGraphicsLayoutToBottom() {
    RelativeLayout var1 = (RelativeLayout)this.findViewById(2131689728);
    LayoutParams var2 = (LayoutParams)var1.getLayoutParams();
    var2.addRule(12);
    var1.setLayoutParams(var2);
  }

  private void checkForAppOfTheDay() {
    if(!this.getPreferences(0).getBoolean(PREF_APP_TURBO_GAVE_PAID_FEATURE, false)) {
      AppTurboUnlockTools.isAppTurboFeaturingUs(this, new MainActivityFree$1(this));
    }

  }

  private void createAndAddAerServViewToAdFiller(RelativeLayout var1) {
    this.aerServBanner = new AerServBanner(this);
    this.aerServBanner.setLayerType(1, (Paint)null);
    var1.addView(this.aerServBanner);
  }

  private void createAndAddMoPubViewToAdFiller(RelativeLayout var1) {
  }

  private String getAdProvider() {
    return RelaxPropertyHandler.getInstance().getProperties().getProperty(RelaxPropertyHandler.RELAX_AD_PRODIVDER);
  }

  public static MainActivityFree getInstance() {
    return instance;
  }

  private void giveAppOfTheDayPaidFeature() {
    Toast.makeText(this, this.getResources().getString(2131231149), 1).show();
    Editor var1 = this.getPreferences(0).edit();
    var1.putBoolean(PREF_APP_TURBO_GAVE_PAID_FEATURE, true);
    var1.apply();
    FeatureManager.getInstance().redeemLifetimeSubscription();
  }

  private void giveAwaySubscriptionIfOldPremiumUser() {
    if(RelaxMelodiesApp.isPremium().booleanValue() && !FeatureManager.getInstance().hasActiveSubscription()) {
      FeatureManager.getInstance().redeemLifetimeSubscription();
    }

  }

  private void hideFreeControls() {
    this.setFreeControlVisibility(8);
  }

  private void setFreeControlVisibility(int var1) {
    this.adFillerLayout.setVisibility(var1);
    if(var1 == 0 && RelaxMelodiesApp.getInstance().areAdsEnabled()) {
      this.alignCloudMenuToAdView();
      this.alignMainGraphicsLayoutToAdView();
    } else {
      this.alignCloudMenuBottom();
      this.alignMainGraphicsLayoutToBottom();
    }

    if(FeatureManager.getInstance().hasActiveSubscription()) {
      this.hideProButton();
    } else {
      this.showProButton();
    }
  }

  private void showFreeControls() {
    this.setFreeControlVisibility(0);
  }

  private void startAdView() {
    String var1 = this.getAdProvider();
    if(var1 != null && !var1.isEmpty()) {
      if(var1.toLowerCase().equals("mopub")) {
        this.startMoPubAdView();
      } else if(var1.toLowerCase().equals("aerserv")) {
        this.startAerServAdView();
        return;
      }
    }

  }

  private void startAerServAdView() {
    if(this.aerServBanner == null) {
      RelativeLayout var2 = (RelativeLayout)this.findViewById(2131689727);
      this.createAndAddAerServViewToAdFiller(var2);
      DisplayMetrics var1 = this.getResources().getDisplayMetrics();
      AerServConfig var3;
      if((float)var1.widthPixels / var1.density < 728.0F) {
        var3 = new AerServConfig(this, this.getString(2131231213));
      } else {
        var3 = new AerServConfig(this, this.getString(2131231214));
      }

      var3.setEventListener(this);
      this.aerServBanner.configure(var3);
      var2.setVisibility(View.VISIBLE);
      this.aerServBanner.show();
      this.aerServBanner.play();
    }

  }

  private void startMoPubAdView() {
  }

  private void stopAdView() {
    if(this.aerServBanner != null) {
      this.aerServBanner.kill();
      this.aerServBanner = null;
    }

  }

  private void updateUI() {
    this.showFreeControls();
    if(!RelaxMelodiesApp.isPremium().booleanValue()) {
      this.startAdView();
    } else {
      this.findViewById(2131689727).setVisibility(View.GONE);
      this.stopAdView();
    }
  }

  public void OnMainFragmentReady() {
    super.OnMainFragmentReady();
    this.adFillerLayout = this.findViewById(2131689727);
  }

  protected MoreFragment createMoreFragment() {
    return new RelaxFreeMoreFragment();
  }

  protected void onActivityResult(int var1, int var2, Intent var3) {
    Log.d("MainActivity", "onActivityResult() requestCode: " + var1 + " resultCode: " + var2 + " data: " + var3);
    if(FeatureManager.getInstance() != null && FeatureManager.getInstance().handlePurchaseFlowActivityResult(var1, var2, var3)) {
      Log.d("MainActivity", "onActivityResult handled by IABUtil.");
    } else {
      super.onActivityResult(var1, var2, var3);
    }
  }

  public void onAerServEvent(AerServEvent var1, List<Object> var2) {
    switch(MainActivityFree$2.$SwitchMap$com$aerserv$sdk$AerServEvent[var1.ordinal()]) {
      case 1:
        Log.d("AerServAds", "aerServ load ads");
        return;
      case 2:
        Log.d("AerServAds", "aerServ failed to load ads");
        return;
      case 3:
        RelaxAnalytics.logAdClicked();
        return;
      default:
    }
  }

  public void onClick(View var1) {
    if(var1 == this.buttonProCloudMenu) {
      RelaxAnalytics.logUpgradeFromCloudMenu();
      NavigationHelper.showSubscription(this);
    } else {
      super.onClick(var1);
    }
  }

  public void onCreate(Bundle var1) {
    super.onCreate(var1);
    instance = this;
    this.splashScreenLayout = (RelativeLayout)this.findViewById(2131689732);
    PackageInfo var4 = null;

    label24: {
      PackageInfo var2;
      try {
        var2 = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
      } catch (NameNotFoundException var3) {
        var3.printStackTrace();
        break label24;
      }

      var4 = var2;
    }

    this.checkForAppOfTheDay();
    if(var4 != null && var4.firstInstallTime > 0L && var4.firstInstallTime == var4.lastUpdateTime) {
      RelaxAnalytics.onCreate(true);
    } else {
      RelaxAnalytics.onCreate(false);
    }

    RelaxAnalytics.setAppFlyerLogger(this);
    String var5 = Amplitude.getInstance().getDeviceId();
    AppsFlyerLib.getInstance().setCustomerUserId(var5);
  }

  protected void onDestroy() {
    if(this.aerServBanner != null) {
      this.aerServBanner.kill();
      this.aerServBanner = null;
    }

    super.onDestroy();
  }

  public void onFeatureManagerSetupFinished() {
    super.onFeatureManagerSetupFinished();
    this.giveAwaySubscriptionIfOldPremiumUser();
  }

  protected void onPause() {
    super.onPause();
    this.stopAdView();
  }

  protected void onResume() {
    this.bottomMenu = this.findViewById(2131689731);
    super.onResume();
    this.openingUpgradeActivity = false;
    this.updateUI();
  }

  public void onSubscriptionChanged(Subscription var1, boolean var2) {
    super.onSubscriptionChanged(var1, var2);
    if(this.uiIsReady) {
      if(!var2 || var1 == null) {
        this.updateUI();
        return;
      }

      this.hideFreeControls();
      this.stopAdView();
    }

  }

  public void onUnresolvedPurchases(List<String> var1) {
    Iterator var4 = var1.iterator();

    while(var4.hasNext()) {
      String var3 = (String)var4.next();
      byte var2 = -1;
      switch(var3.hashCode()) {
        case 1275056211:
          if(var3.equals("ipnossoft.rma.free.premiumfeatures")) {
            var2 = 0;
          }
      }

      switch(var2) {
        case 0:
          PersistedDataManager.saveBoolean("is_premium", true, getInstance().getApplicationContext());
      }
    }

  }

  protected void reloadSoundLibrary() {
    super.reloadSoundLibrary();
    SoundLibrary.getInstance().loadBuiltInSoundsSynchronously(2131558409, 2131558405, 2131558408, 2131558407);
    if(PersistedDataManager.getBoolean("free_review_sounds_added", false, this).booleanValue()) {
      SoundLibrary.getInstance().loadGiftedSoundsSynchronously(2131558406);
    }

  }

  protected void selectHomeNavigationButton() {
    this.openingUpgradeActivity = false;
    super.selectHomeNavigationButton();
  }

  public void startActivityForResult(Intent var1, int var2) {
    Log.d("MainActivity", "startActivityForResult() intent: " + var1 + " requestCode: " + var2);
    super.startActivityForResult(var1, var2);
  }

  public void trackEvent(String var1, double var2, String var4) {
    HashMap var5 = new HashMap();
    var5.put("af_revenue", Double.valueOf(var2));
    var5.put("af_content_id", var1);
    var5.put("af_currency", var4);
    AppsFlyerLib.getInstance().trackEvent(this.getApplicationContext(), "af_purchase", var5);
  }
}
