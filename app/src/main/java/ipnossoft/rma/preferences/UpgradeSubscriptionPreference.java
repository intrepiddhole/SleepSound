package ipnossoft.rma.preferences;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.preference.ListPreference;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import com.ipnossoft.api.dynamiccontent.model.InAppPurchase;
import com.ipnossoft.api.featuremanager.FeatureActionListener;
import com.ipnossoft.api.featuremanager.FeatureManager;
import com.ipnossoft.api.featuremanager.data.Subscription;
import com.ipnossoft.api.featuremanager.exceptions.BillingException;
import com.ipnossoft.api.featuremanager.exceptions.FeatureManagerException;

import ipnossoft.rma.free.R;
import ipnossoft.rma.upgrade.SubscriptionBuilderUtils;
import ipnossoft.rma.upgrade.SubscriptionOfferResolver;
import ipnossoft.rma.util.RelaxAnalytics;
import ipnossoft.rma.util.Utils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.onepf.oms.appstore.googleUtils.SkuDetails;

public class UpgradeSubscriptionPreference extends ListPreference {
  public static final String TAG = "UpgradeSubscription";
  private List<InAppPurchase> inApps;
  private int selectedIndex;
  private List<SkuDetails> skus;

  public UpgradeSubscriptionPreference(Context var1) {
    super(var1);
  }

  public UpgradeSubscriptionPreference(Context var1, AttributeSet var2) {
    super(var1, var2);
  }

  private void commonInit() {
    RelaxAnalytics.logShowUpgradeSubscriptionFromSettings();
    this.selectedIndex = 0;
    this.inApps = this.getInApps();
    this.skus = this.getSkus(this.inApps);
    this.setEntries(this.generateEntries(this.inApps, this.skus));
    this.setEntryValues(this.generateEntryValues(this.skus));
  }

  @NonNull
  private FeatureActionListener createListener(InAppPurchase var1, final SkuDetails var2) {
    final int var3 = SubscriptionOfferResolver.getThreeButtonSubscriptions().indexOf(var1);
    final String var4 = var1.getIdentifier();
    RelaxAnalytics.logSubscriptionUpgradeProcessTriggered(var4, var3);
    return new FeatureActionListener() {
      public void onFailure(FeatureManagerException var1) {
        super.onFailure(var1);
        if(!(var1 instanceof BillingException))
          Utils.alert(UpgradeSubscriptionPreference.this.getContext(), UpgradeSubscriptionPreference.this.getContext().getString(R.string.service_error_title), var1.getMessage());

        if(var4 != null && var3 > 0) {
          RelaxAnalytics.logSubscriptionUpgradeProcessFailed(var4, var3);
        }

      }

      public void onSuccess() {
        super.onSuccess();
        if(var4 != null && var3 > 0) {
          RelaxAnalytics.logSubscriptionUpgradeProcessSucceed(var4, var3, var2);
        }

      }
    };
  }

  private CharSequence[] generateEntries(List<InAppPurchase> var1, List<SkuDetails> var2) {
    CharSequence[] var3 = new CharSequence[var2.size()];

    for(int var6 = 0; var6 < var2.size(); ++var6) {
      SkuDetails var4 = (SkuDetails)var2.get(var6);
      InAppPurchase var5 = (InAppPurchase)var1.get(var6);
      var3[var6] = var4.getPrice() + " / " + SubscriptionBuilderUtils.getDurationUnitText(this.getContext(), var5);
    }

    return var3;
  }

  private CharSequence[] generateEntryValues(List<SkuDetails> var1) {
    CharSequence[] var2 = new CharSequence[var1.size()];

    for(int var3 = 0; var3 < var1.size(); ++var3) {
      var2[var3] = ((SkuDetails)var1.get(var3)).getSku();
    }

    return var2;
  }

  private List<SkuDetails> getSkus(List<InAppPurchase> var1) {
    this.skus = new ArrayList();
    Iterator var3 = var1.iterator();

    while(var3.hasNext()) {
      InAppPurchase var2 = (InAppPurchase)var3.next();
      SkuDetails var4 = FeatureManager.getInstance().getPurchaseDetails(var2.getIdentifier());
      if(var4 != null) {
        this.skus.add(var4);
      } else {
        var3.remove();
      }
    }

    return this.skus;
  }

  private void upgradeToInApp(int var1) {
    if(var1 >= 0 && !this.inApps.isEmpty()) {
      InAppPurchase var2 = (InAppPurchase)this.inApps.get(var1);
      FeatureActionListener var3 = this.createListener(var2, (SkuDetails)this.skus.get(var1));
      if(!var2.isSubscriptionAutoRenewable()) {
        FeatureManager.getInstance().subscribe(var2.getIdentifier(), false, var3);
        return;
      }

      FeatureManager.getInstance().upgradeAutorenewableSubscription(var2.getIdentifier(), var3);
    }

  }

  public List<InAppPurchase> getInApps() {
    this.inApps = new ArrayList();
    Subscription var1 = FeatureManager.getInstance().getActiveSubscription();
    if(var1 != null && var1.isAutoRenewable()) {
      Iterator var2 = SubscriptionOfferResolver.getThreeButtonSubscriptions().iterator();

      while(var2.hasNext()) {
        InAppPurchase var3 = (InAppPurchase)var2.next();
        if(var3.isSubscriptionAutoRenewable() && !var3.getIdentifier().equals(var1.getIdentifier())) {
          this.inApps.add(var3);
        }
      }
    }

    return this.inApps;
  }

  protected void onPrepareDialogBuilder(Builder var1) {
    this.commonInit();
    super.onPrepareDialogBuilder(var1);
    var1.setSingleChoiceItems(this.getEntries(), 0, new OnClickListener() {
      public void onClick(DialogInterface var1, int var2) {
        UpgradeSubscriptionPreference.this.selectedIndex = var2;
      }
    });
    var1.setPositiveButton(R.string.activity_title_upgrade, new OnClickListener() {
      public void onClick(DialogInterface var1, int var2) {
        var1.dismiss();
        UpgradeSubscriptionPreference.this.upgradeToInApp(UpgradeSubscriptionPreference.this.selectedIndex);
        Log.d("UpgradeSubscription", "Try upgrading index" + UpgradeSubscriptionPreference.this.selectedIndex);
      }
    });
    var1.setNegativeButton(R.string.cancel, (OnClickListener)null);
  }
}
