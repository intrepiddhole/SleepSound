package ipnossoft.rma.feature;

import android.app.Activity;
import android.content.pm.PackageManager.NameNotFoundException;
import android.support.annotation.NonNull;
import com.ipnossoft.api.featuremanager.FeatureManager;
import com.ipnossoft.api.featuremanager.FeatureManagerCallback;
import com.ipnossoft.api.featuremanager.FeatureManagerConfig;
import ipnossoft.rma.RelaxMelodiesApp;
import ipnossoft.rma.RelaxPropertyHandler;
import ipnossoft.rma.free.R;
import ipnossoft.rma.upgrade.SubscriptionOfferResolver;

public class RelaxFeatureManager extends FeatureManager {
  private static String relaxServerUrlCache = null;

  public RelaxFeatureManager() {
  }

  public static void configureRelaxFeatureManager(Activity var0, FeatureManagerCallback var1) {
    FeatureManager var3 = FeatureManager.getInstance();
    FeatureManagerConfig var4 = initFeatureManagerConfiguration(var0);
    boolean var2;
    if(RelaxMelodiesApp.isFreeVersion() && RelaxMelodiesApp.isPremium().booleanValue()) {
      var2 = true;
    } else {
      var2 = false;
    }

    var3.configureFeatureManager(var4, var1, var2);
    if(!FeatureManager.getInstance().hasActiveSubscription()) {
      String var5;
      if(RelaxMelodiesApp.isFreeVersion()) {
        var5 = getFreeConfigFileUrl();
      } else {
        var5 = getPremiumConfigFileUrl();
      }

      SubscriptionOfferResolver.fetchConfiguration(var0.getApplicationContext(), var5);
    }

  }

  @NonNull
  private static String getFreeConfigFileUrl() {
    String var0 = getMarketSuffix();
    String var1 = getRootConfigFileUrl();
    var1 = var1 + "/config/rma/inapps/inapps-v6.1-free";
    return var1 + var0 + ".json";
  }

  private static String getMarketSuffix() {
    String var0 = "";
    if(isAmazonMarket()) {
      var0 = "-amazon";
    } else if(isSamsungMarket()) {
      return "-samsung";
    }

    return var0;
  }

  @NonNull
  private static String getPremiumConfigFileUrl() {
    String var0 = getMarketSuffix();
    String var1 = getRootConfigFileUrl();
    var1 = var1 + "/config/rma/inapps/inapps-v6.1-premium";
    return var1 + var0 + ".json";
  }

  private static String getRelaxServerURL() {
    if(relaxServerUrlCache == null) {
      relaxServerUrlCache = RelaxPropertyHandler.getInstance().getProperties().getProperty("RELAX_SERVER_URL");
    }

    return relaxServerUrlCache;
  }

  @NonNull
  private static String getRootConfigFileUrl() {
    return "http://cdn1.ipnoscloud.com";
  }

  private static FeatureManagerConfig initFeatureManagerConfiguration(Activity var0) {
    FeatureManagerConfig var1 = new FeatureManagerConfig(getRelaxServerURL(), RelaxPropertyHandler.getInstance().getProperties().getProperty("RELAX_SERVER_USERNAME"), RelaxPropertyHandler.getInstance().getProperties().getProperty("RELAX_SERVER_API_KEY"), RelaxPropertyHandler.getInstance().getProperties().getProperty("RELAX_APP_CODE"), RelaxPropertyHandler.getInstance().getProperties().getProperty("RELAX_SERVER_ZIP_PASSWORDS"), var0);

    try {
      var1.setAppVersion(var0.getPackageManager().getPackageInfo(var0.getPackageName(), 0).versionName);
      return var1;
    } catch (NameNotFoundException var2) {
      var2.printStackTrace();
      return var1;
    }
  }

  private static boolean isAmazonMarket() {
    return RelaxMelodiesApp.getInstance().getResources().getString(R.string.custom_market).equals("AMAZON");
  }

  private static boolean isSamsungMarket() {
    return RelaxMelodiesApp.getInstance().getResources().getString(R.string.custom_market).equals("SAMSUNG");
  }
}
