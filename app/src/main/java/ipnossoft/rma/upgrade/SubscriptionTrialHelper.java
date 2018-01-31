package ipnossoft.rma.upgrade;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import com.ipnossoft.api.featuremanager.FeatureManager;
import ipnossoft.rma.PersistedDataManager;
import ipnossoft.rma.RelaxMelodiesApp;

public class SubscriptionTrialHelper {
  private static String PREFERENCES_TRIAL_POPUP_SHOWN = "PREFERENCES_TRIAL_POPUP_SHOWN";

  public SubscriptionTrialHelper() {
  }

  private static boolean hasEnoughOpenings() {
    boolean var1 = false;
    Context var2 = RelaxMelodiesApp.getInstance().getApplicationContext();
    boolean var0 = var1;
    if(!PersistedDataManager.getBoolean(PREFERENCES_TRIAL_POPUP_SHOWN, false, var2).booleanValue()) {
      var0 = var1;
      if(PersistedDataManager.getInteger("app_startup_counter", 0, var2) + 1 >= openingsUntilPrompt()) {
        var0 = true;
      }
    }

    return var0;
  }

  private static int openingsUntilPrompt() {
    return SubscriptionOfferResolver.getOpeningsUntilTrialPopup();
  }

  public static boolean shouldShowTrial() {
    return FeatureManager.getInstance().isSetupFinished() && !FeatureManager.getInstance().hasActiveSubscription() && SubscriptionOfferResolver.getButton1Subscription().getSubscriptionTrialDuration() > 0 && hasEnoughOpenings();
  }

  public static void showTrial(Activity var0) {
    var0.startActivity(new Intent(var0, SubscriptionTrialActivity.class));
    PersistedDataManager.saveBoolean(PREFERENCES_TRIAL_POPUP_SHOWN, true, RelaxMelodiesApp.getInstance().getApplicationContext());
  }
}
