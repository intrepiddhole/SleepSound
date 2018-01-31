package ipnossoft.rma.upgrade;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import com.ipnossoft.api.dynamiccontent.model.InAppPurchase;
import com.ipnossoft.api.featuremanager.FeatureManager;
import com.ipnossoft.api.featuremanager.FeatureManagerObserver;
import com.ipnossoft.api.featuremanager.data.Subscription;
import com.ipnossoft.api.featuremanager.exceptions.BillingException;

import ipnossoft.rma.free.R;
import ipnossoft.rma.upgrade.Subscription.SubscriptionCallback;
import ipnossoft.rma.util.RelaxAnalytics;
import ipnossoft.rma.util.Utils;
import java.util.Date;
import java.util.List;
import org.onepf.oms.appstore.googleUtils.Purchase;
import org.onepf.oms.appstore.googleUtils.SkuDetails;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SubscriptionTrialActivity extends AppCompatActivity implements OnClickListener, SubscriptionCallback, FeatureManagerObserver {
  private boolean alreadyLoggedSubscriptionSuccessAnalytics = false;
  private boolean firstOnResume = true;
  private SubscriptionTrialBuilder subscriptionTrialBuilder;

  public SubscriptionTrialActivity() {
  }

  protected void attachBaseContext(Context var1) {
    super.attachBaseContext(CalligraphyContextWrapper.wrap(var1));
  }

  public void onClick(View var1) {
    if(var1 != null) {
      if(var1.getId() != R.id.subscribe_trial_cancel && var1.getId() != R.id.subscribe_exit_button) {
        if(var1.getId() == R.id.subscribe_trial_button) {
          this.subscriptionTrialBuilder.subscribe(1);
          return;
        }
      } else {
        this.finish();
      }
    }

  }

  protected void onCreate(Bundle var1) {
    super.onCreate(var1);
    this.setContentView(R.layout.subscribe_trial);
    DisplayMetrics var2 = this.getResources().getDisplayMetrics();
    if((float)var2.heightPixels / var2.density < 640.0F) {
      ((ImageView)this.findViewById(R.id.light_image_view)).setVisibility(View.INVISIBLE);
    }

    this.subscriptionTrialBuilder = new SubscriptionTrialBuilder(this.findViewById(R.id.subscribe_root_view), this, this);
    this.subscriptionTrialBuilder.build();
    if(this.getSupportActionBar() != null) {
      this.getSupportActionBar().setTitle(R.string.activity_title_upgrade);
      this.getSupportActionBar().setHomeButtonEnabled(true);
      this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    RelaxAnalytics.logScreenTrialPopup();
  }

  public void onFeatureDownloaded(InAppPurchase var1, String[] var2) {
  }

  public void onFeatureManagerSetupFinished() {
  }

  public void onFeatureUnlocked(InAppPurchase var1, String var2) {
  }

  protected void onPause() {
    super.onPause();
    FeatureManager.getInstance().unregisterObserver(this);
  }

  public void onPurchaseCompleted(InAppPurchase var1, Purchase var2, Date var3) {
  }

  public void onPurchasesAvailable(List<InAppPurchase> var1) {
  }

  protected void onResume() {
    super.onResume();
    if(!this.firstOnResume) {
      FeatureManager.getInstance().queryInventory();
      FeatureManager.getInstance().registerObserver(this);
    }

    this.firstOnResume = false;
  }

  public void onSubscriptionCancel() {
  }

  public void onSubscriptionChanged(Subscription var1, boolean var2) {
    if(var2) {
      if(!this.alreadyLoggedSubscriptionSuccessAnalytics) {
        this.alreadyLoggedSubscriptionSuccessAnalytics = true;
        SkuDetails var3 = this.subscriptionTrialBuilder.getSkuDetailsForSubscription(var1.getIdentifier());
        RelaxAnalytics.logSubscriptionProcessSucceed(var1.getIdentifier(), 1, var3);
      }

      this.finish();
    }

  }

  public void onSubscriptionFailure(Exception var1, String var2, int var3) {
    if(!(var1 instanceof BillingException)) {
      Utils.alert(this, "Error", var1.getMessage());
    }

    if(var2 != null && var3 > 0) {
      RelaxAnalytics.logSubscriptionProcessFailed(var2, var3);
    }

  }

  public void onSubscriptionProcessTriggered(String var1, int var2) {
    RelaxAnalytics.logSubscriptionProcessTriggered(var1, var2);
  }

  public void onSubscriptionSuccess(String var1, int var2) {
    if(!this.alreadyLoggedSubscriptionSuccessAnalytics) {
      this.alreadyLoggedSubscriptionSuccessAnalytics = true;
      RelaxAnalytics.logSubscriptionProcessSucceed(var1, var2, this.subscriptionTrialBuilder.getSkuDetailsForSubscription(var1));
    }

    this.finish();
  }

  public void onUnresolvedPurchases(List<String> var1) {
  }
}
