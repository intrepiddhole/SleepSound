package ipnossoft.rma.upgrade;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.ipnossoft.api.dynamiccontent.model.InAppPurchase;
import com.ipnossoft.api.featuremanager.FeatureActionListener;
import com.ipnossoft.api.featuremanager.FeatureManager;
import com.ipnossoft.api.featuremanager.exceptions.FeatureManagerException;
import ipnossoft.rma.PersistedDataManager;
import ipnossoft.rma.RelaxMelodiesApp;
import ipnossoft.rma.free.R;
import ipnossoft.rma.ui.dialog.RelaxDialog.Builder;
import ipnossoft.rma.ui.dialog.RelaxDialog.RelaxDialogButtonOrientation;
import ipnossoft.rma.util.RelaxAnalytics;
import org.onepf.oms.appstore.googleUtils.SkuDetails;

public class SpecialOfferPopupHelper {
  private static String SPECIAL_OFFER_POPUP_SHOWN = "SPECIAL_OFFER_POPUP_SHOWN";

  public SpecialOfferPopupHelper() {
  }

  private static void applyPercentageToView(Activity var0, View var1, int var2) {
    TextView var3 = (TextView)var1.findViewById(R.id.special_offer_title);
    TextView var4 = (TextView)var1.findViewById(R.id.special_offer_description);
    var3.setText(var0.getString(R.string.special_offer_percentage_off_now, new Object[]{Integer.valueOf(var2)}));
    var4.setText(var0.getString(R.string.special_offer_description, new Object[]{Integer.valueOf(var2)}));
  }

  @NonNull
  private static FeatureActionListener createFeatureListener(final SkuDetails var0) {
    return new FeatureActionListener() {
      private boolean alreadyLoggedSubscriptionSuccessAnalytics;

      public void onFailure(FeatureManagerException var1) {
        RelaxAnalytics.logSubscriptionProcessFailed(var0.getSku(), 0);
      }

      public void onSuccess() {
        if(!this.alreadyLoggedSubscriptionSuccessAnalytics) {
          this.alreadyLoggedSubscriptionSuccessAnalytics = true;
          RelaxAnalytics.logSubscriptionProcessSucceed(var0.getSku(), 0, var0);
        }

      }
    };
  }

  private static InAppPurchase getMainButtonSubscription() {
    return SubscriptionOfferResolver.getLayout() > 4?SubscriptionOfferResolver.getButton1Subscription():SubscriptionOfferResolver.getButton3Subscription();
  }

  private static boolean hasEnoughOpenings() {
    boolean var1 = false;
    Context var2 = RelaxMelodiesApp.getInstance().getApplicationContext();
    boolean var0 = var1;
    if(!PersistedDataManager.getBoolean(SPECIAL_OFFER_POPUP_SHOWN, false, var2).booleanValue()) {
      var0 = var1;
      if(PersistedDataManager.getInteger("app_startup_counter", 0, var2) + 1 >= openingsUntilPrompt()) {
        var0 = true;
      }
    }

    return var0;
  }

  private static int openingsUntilPrompt() {
    return SubscriptionOfferResolver.getOpeningsUntilSpecialOfferPopup();
  }

  public static boolean shouldShowSpecialOffer() {
    return RelaxMelodiesApp.getInstance().getApplicationContext().getString(R.string.app_lang).equals("en") && FeatureManager.getInstance().isSetupFinished() && !FeatureManager.getInstance().hasActiveSubscription() && SubscriptionOfferResolver.getReplacedSubscriptionIdentifier() != null && hasEnoughOpenings();
  }

  public static void showSpecialOffer(Activity var0) {
    InAppPurchase var5 = getMainButtonSubscription();
    if(var5 != null) {
      final boolean var8 = var5.isSubscriptionAutoRenewable();
      final SkuDetails var9 = FeatureManager.getInstance().getPurchaseDetails(var5.getIdentifier());
      String var6 = SubscriptionOfferResolver.getReplacedSubscriptionIdentifier();
      SkuDetails var11 = FeatureManager.getInstance().getPurchaseDetails(var6);
      if(var9 != null && var11 != null) {
        double var1 = SubscriptionBuilderUtils.getPriceValue(var9);
        double var3 = SubscriptionBuilderUtils.getPriceValue(var11);
        if(var1 != 0.0D && var3 != 0.0D && var1 < var3) {
          int var7 = (int)Math.round((1.0D - var1 / var3) * 100.0D);
          Builder var12 = new Builder(var0, RelaxDialogButtonOrientation.VERTICAL);
          var12.setCancelable(false);
          var12.setPositiveButton(var0.getString(R.string.subscribe_save_value, new Object[]{var7 + "%"}), new OnClickListener() {
            public void onClick(View var1) {
              RelaxAnalytics.logShowedSpecialOfferPopup(var9.getSku(), true);
              RelaxAnalytics.logSubscriptionProcessTriggered(var9.getSku(), 0);
              FeatureManager.getInstance().subscribe(var9.getSku(), var8, SpecialOfferPopupHelper.createFeatureListener(var9));
            }
          });
          var12.setNegativeButton(R.string.rating_dialog_no, new OnClickListener() {
            public void onClick(View var1) {
              RelaxAnalytics.logShowedSpecialOfferPopup(var9.getSku(), false);
            }
          });
          View var10 = LayoutInflater.from(var0).inflate(R.layout.special_offer, (ViewGroup)null, false);
          applyPercentageToView(var0, var10, var7);
          var12.setCustomContentView(var10);
          var12.show();
          PersistedDataManager.saveBoolean(SPECIAL_OFFER_POPUP_SHOWN, true, RelaxMelodiesApp.getInstance().getApplicationContext());
          return;
        }
      }
    }

  }
}
