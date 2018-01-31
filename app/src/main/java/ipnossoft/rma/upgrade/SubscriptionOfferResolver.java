package ipnossoft.rma.upgrade;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Cache.Entry;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.ipnossoft.api.dynamiccontent.model.InAppPurchase;
import ipnossoft.rma.RelaxMelodiesApp;
import ipnossoft.rma.util.CountryUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

public class SubscriptionOfferResolver {
  private static final String DEFAULT_COUNTRY = "default";
  private static String TAG = "SubscriptionResolver";
  private static InAppPurchase button1Subscription;
  private static InAppPurchase button2Subscription;
  private static InAppPurchase button3Subscription;
  private static Context context;
  private static int layout = 5;
  private static int openingsUntilSpecialOfferPopup = 2;
  private static int openingsUntilTrialPopup = 3;
  private static Uri proButtonImageURL;
  private static String replacedSubscriptionIdentifier;
  private static List<SubscriptionOfferResolverListener> subscriptionOfferResolverListeners;

  public SubscriptionOfferResolver() {
  }

  private static InAppPurchase buildDefaultButton1Subscription() {
    return buildDefaultSubscription(1, 2, 1);
  }

  private static InAppPurchase buildDefaultButton2Subscription() {
    return buildDefaultSubscription(2, 2, 3);
  }

  private static InAppPurchase buildDefaultButton3Subscription() {
    return buildDefaultSubscription(3, -1, -1);
  }

  private static InAppPurchase buildDefaultSubscription(int var0, int var1, int var2) {
    InAppPurchase var4 = new InAppPurchase();
    StringBuilder var5 = (new StringBuilder()).append("ipnossoft.rma.");
    String var3;
    if(RelaxMelodiesApp.isFreeVersion()) {
      var3 = "free";
    } else {
      var3 = "premium";
    }

    var4.setIdentifier(var5.append(var3).append(".subscription.tier").append(var0).toString());
    var4.setSubscription(true);
    var4.setSubscriptionAutoRenewable(false);
    var4.setSubscriptionDurationUnit(var1);
    var4.setSubscriptionDuration(Integer.valueOf(var2));
    return var4;
  }

  private static InAppPurchase extractButton1FromConfig(JSONObject var0) throws JSONException {
    InAppPurchase var1 = extractButtonSubscriptionFromConfig(1, var0);
    InAppPurchase var2 = var1;
    if(var1 == null) {
      var2 = buildDefaultButton1Subscription();
    }

    return var2;
  }

  private static InAppPurchase extractButton2FromConfig(JSONObject var0) throws JSONException {
    InAppPurchase var1 = extractButtonSubscriptionFromConfig(2, var0);
    InAppPurchase var2 = var1;
    if(var1 == null) {
      var2 = buildDefaultButton2Subscription();
    }

    return var2;
  }

  private static InAppPurchase extractButton3FromConfig(JSONObject var0) throws JSONException {
    InAppPurchase var1 = extractButtonSubscriptionFromConfig(3, var0);
    InAppPurchase var2 = var1;
    if(var1 == null) {
      var2 = buildDefaultButton3Subscription();
    }

    return var2;
  }

  private static InAppPurchase extractButtonSubscriptionFromConfig(int var0, JSONObject var1) throws JSONException {
    if(var1.has("button" + var0)) {
      var1 = var1.getJSONObject("button" + var0);
      if(var1.has("identifier") && var1.has("durationUnit")) {
        InAppPurchase var2 = new InAppPurchase();
        var2.setIdentifier(var1.getString("identifier"));
        var2.setSubscriptionDurationUnit(var1.getString("durationUnit"));
        if(var1.has("trialDuration")) {
          var2.setSubscriptionTrialDuration(var1.getInt("trialDuration"));
        }

        if(var1.has("duration")) {
          var2.setSubscriptionDuration(Integer.valueOf(var1.getInt("duration")));
        } else {
          var2.setSubscriptionDuration(Integer.valueOf(1));
        }

        if(var2.getSubscriptionDurationUnit() == -1) {
          var2.setSubscriptionDuration(Integer.valueOf(-1));
          var2.setSubscriptionAutoRenewable(false);
          return var2;
        }

        var2.setSubscriptionAutoRenewable(true);
        return var2;
      }
    }

    return null;
  }

  private static Uri extractImageProButtonFromConfig(JSONObject var0) throws JSONException {
    return var0.has("proButton")?Uri.parse(var0.getString("proButton")):null;
  }

  private static String extractReplacedSubscriptionFromConfig(JSONObject var0) throws JSONException {
    if(var0.has("button1")) {
      var0 = var0.getJSONObject("button1");
      if(var0.has("replacesIdentifier")) {
        return var0.getString("replacesIdentifier");
      }
    }

    return null;
  }

  public static void fetchConfiguration(Context var0, String var1) {
    context = var0;
    RequestQueue var3 = Volley.newRequestQueue(context);
    JsonObjectRequest var4 = new JsonObjectRequest(0, var1, (JSONObject)null, new Listener<JSONObject>() {
      public void onResponse(JSONObject var1) {
        try {
          SubscriptionOfferResolver.handleConfigurationResponse(var1);
        } catch (Exception var2) {
          Log.e(SubscriptionOfferResolver.TAG, "Failed parsing subscriptions configuration response", var2);
        }
      }
    }, new ErrorListener() {
      public void onErrorResponse(VolleyError var1) {
        Log.e(SubscriptionOfferResolver.TAG, "Failed fetching subscriptions configuration", var1);
      }
    });
    var4.setShouldCache(true);
    Entry var2 = var3.getCache().get(var4.getCacheKey());
    if(var2 != null && System.currentTimeMillis() - var2.serverDate > (long)21600000) {
      var3.getCache().remove(var4.getCacheKey());
    }

    var3.add(var4);
  }

  static InAppPurchase getButton1Subscription() {
    return button1Subscription != null?button1Subscription:buildDefaultButton1Subscription();
  }

  static InAppPurchase getButton2Subscription() {
    return button2Subscription != null?button2Subscription:buildDefaultButton2Subscription();
  }

  static InAppPurchase getButton3Subscription() {
    return button3Subscription != null?button3Subscription:buildDefaultButton3Subscription();
  }

  public static int getLayout() {
    return layout;
  }

  static int getOpeningsUntilSpecialOfferPopup() {
    return openingsUntilSpecialOfferPopup;
  }

  static int getOpeningsUntilTrialPopup() {
    return openingsUntilTrialPopup;
  }

  public static Uri getProButtonImageURL() {
    return proButtonImageURL;
  }

  public static String getReplacedSubscriptionIdentifier() {
    return replacedSubscriptionIdentifier;
  }

  public static List<InAppPurchase> getThreeButtonSubscriptions() {
    boolean var1;
    if(RelaxMelodiesApp.isPremium().booleanValue() && RelaxMelodiesApp.isFreeVersion()) {
      var1 = true;
    } else {
      var1 = false;
    }

    InAppPurchase var0;
    if(var1) {
      var0 = new InAppPurchase();
      var0.setIdentifier("ipnossoft.rma.free.subscription.premium.tier3");
      var0.setSubscription(true);
      var0.setSubscriptionAutoRenewable(false);
      var0.setSubscriptionDurationUnit(-1);
      var0.setSubscriptionDuration(Integer.valueOf(-1));
    } else {
      var0 = getButton1Subscription();
    }

    return Arrays.asList(new InAppPurchase[]{var0, getButton2Subscription(), getButton3Subscription()});
  }

  private static void handleConfigurationResponse(JSONObject var0) throws JSONException {
    if(var0 != null) {
      String var1 = CountryUtils.getCountry(context);
      if(var0.has(var1)) {
        var0 = var0.getJSONObject(var1);
      } else {
        var0 = var0.getJSONObject("default");
      }

      button1Subscription = extractButton1FromConfig(var0);
      button2Subscription = extractButton2FromConfig(var0);
      button3Subscription = extractButton3FromConfig(var0);
      replacedSubscriptionIdentifier = extractReplacedSubscriptionFromConfig(var0);
      if(var0.has("layout")) {
        layout = var0.getInt("layout");
      }

      if(var0.has("openingsUntilTrialPopup")) {
        openingsUntilTrialPopup = var0.getInt("openingsUntilTrialPopup");
      }

      if(var0.has("openingsUntilSpecialOfferPopup")) {
        openingsUntilSpecialOfferPopup = var0.getInt("openingsUntilSpecialOfferPopup");
      }

      proButtonImageURL = extractImageProButtonFromConfig(var0);
      Log.d(TAG, "Resolving Subscriptions for country: " + var1 + "\n tier1:" + button1Subscription.getIdentifier() + "\ntier2:" + button2Subscription.getIdentifier() + "\ntier3:" + button3Subscription.getIdentifier());
      notifyConfigurationLoaded();
    }
  }

  private static void notifyConfigurationLoaded() {
    if(subscriptionOfferResolverListeners != null && subscriptionOfferResolverListeners.size() > 0) {
      Iterator var0 = subscriptionOfferResolverListeners.iterator();

      while(var0.hasNext()) {
        ((SubscriptionOfferResolverListener)var0.next()).configurationsLoaded();
      }
    }

  }

  public static void registerListener(SubscriptionOfferResolverListener var0) {
    if(subscriptionOfferResolverListeners == null) {
      subscriptionOfferResolverListeners = new ArrayList();
    }

    subscriptionOfferResolverListeners.add(var0);
  }

  public static void removeListener(SubscriptionOfferResolverListener var0) {
    subscriptionOfferResolverListeners.remove(var0);
  }
}
