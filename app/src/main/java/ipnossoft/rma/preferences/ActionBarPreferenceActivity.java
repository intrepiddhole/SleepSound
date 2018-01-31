package ipnossoft.rma.preferences;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v4.preference.PreferenceFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import com.ipnossoft.api.dynamiccontent.model.InAppPurchase;
import com.ipnossoft.api.featuremanager.FeatureManager;
import com.ipnossoft.api.featuremanager.FeatureManagerObserver;
import com.ipnossoft.api.featuremanager.data.Subscription;
import ipnossoft.rma.AudioFocusManager;
import ipnossoft.rma.DefaultServiceConnection;
import ipnossoft.rma.NavigationHelper;
import ipnossoft.rma.PersistedDataManager;
import ipnossoft.rma.RelaxMelodiesApp;
import ipnossoft.rma.free.R;
import ipnossoft.rma.media.SoundManager;
import ipnossoft.rma.media.SoundVolumeManager;
import ipnossoft.rma.upgrade.SubscriptionBuilderUtils;
import ipnossoft.rma.upgrade.SubscriptionOfferResolver;
import ipnossoft.rma.util.RelaxAnalytics;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.onepf.oms.appstore.googleUtils.Purchase;
import org.onepf.oms.appstore.googleUtils.SkuDetails;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ActionBarPreferenceActivity extends AppCompatActivity {
  private static final String LINE = "_________________________";
  private static final String PREF_PROMOTION_REDEEM_FAIL_COUNT = "promotion_redeem_fail_count";
  private final DefaultServiceConnection conn = new DefaultServiceConnection();
  private boolean firstOnResume = true;
  private RelaxMelodiesApp relaxMelodiesApp;

  public ActionBarPreferenceActivity() {
  }

  private String buildMailText() {
    String var1 = this.relaxMelodiesApp.getAppName();
    String var2 = String.format("%s %s (%s)", new Object[]{Build.MANUFACTURER, Build.MODEL, Build.PRODUCT});
    int var4 = VERSION.SDK_INT;
    String var3 = this.relaxMelodiesApp.getMarketCustomParam().name().toLowerCase(Locale.getDefault());
    return String.format(Locale.getDefault(), "\n\n%s\nProduct: %s\nVersion: %s\nModel: %s\nSystem: %d\nMarket: %s\n%s", new Object[]{"_________________________", var1, this.getVersion(), var2, Integer.valueOf(var4), var3, "_________________________"});
  }

  private String getVersion() {
    try {
      PackageInfo var1 = this.getPackageManager().getPackageInfo(this.getApplicationInfo().packageName, 0);
      String var3 = String.format(Locale.getDefault(), "%s (%d)", new Object[]{var1.versionName, Integer.valueOf(var1.versionCode)});
      return var3;
    } catch (NameNotFoundException var2) {
      return null;
    }
  }

  private void sendMail(String var1, String var2, String... var3) {
    Intent var4 = new Intent("android.intent.action.SEND");
    var4.setType("plain/text");
    var4.putExtra("android.intent.extra.SUBJECT", var1);
    var4.putExtra("android.intent.extra.TEXT", var2);
    if(var3 != null) {
      var4.putExtra("android.intent.extra.EMAIL", var3);
    }

    this.startActivity(Intent.createChooser(var4, this.getString(R.string.web_activity_mail_intent_title)));
  }

  private void sendSupportMail() {
    String var1 = this.buildMailText();
    this.sendMail(String.format("%s %s Android %s", new Object[]{this.getString(R.string.mail_support_title), this.relaxMelodiesApp.getAppName(), this.getVersion()}), var1, new String[]{this.getString(R.string.mail_support_address)});
  }

  protected void attachBaseContext(Context var1) {
    super.attachBaseContext(CalligraphyContextWrapper.wrap(var1));
  }

  public void onCorrectCode() {
    PersistedDataManager.saveBoolean("is_promotion_premium", true, this.getApplicationContext());
    this.finish();
  }

  protected void onCreate(Bundle var1) {
    super.onCreate(var1);
    this.setTitle(R.string.web_button_label_settings);
    ActionBar var2 = this.getSupportActionBar();
    if(var2 != null) {
      var2.setHomeButtonEnabled(true);
      var2.setDisplayHomeAsUpEnabled(true);
    }
    this.relaxMelodiesApp = (RelaxMelodiesApp)this.getApplicationContext();
    ActionBarPreferenceActivity.PreferencesFragment var3 = new ActionBarPreferenceActivity.PreferencesFragment();
    this.getSupportFragmentManager().beginTransaction().replace(android.R.id.content, var3).commit();
  }

  public boolean onOptionsItemSelected(MenuItem var1) {
    if(var1.getItemId() == 16908332) {
      this.finish();
      return true;
    } else {
      return super.onOptionsItemSelected(var1);
    }
  }

  protected void onResume() {
    super.onResume();
    RelaxAnalytics.logScreenSettings();
    if(!this.firstOnResume) {
      FeatureManager.getInstance().queryInventory();
    }

    this.firstOnResume = false;
  }

  protected void onStart() {
    super.onStart();
    this.conn.connect(this);
  }

  protected void onStop() {
    this.conn.disconnect(this);
    super.onStop();
  }

  public void onWrongCode() {
    int var3 = PersistedDataManager.getInteger("promotion_redeem_fail_count", 0, this) + 1;
    int var2 = var3;
    if(var3 > 2) {
      var2 = 0;
      Builder var1 = new Builder(this);
      var1.setTitle(R.string.web_button_label_contact);
      var1.setNeutralButton(R.string.cancel, new OnClickListener() {
        public void onClick(DialogInterface var1, int var2) {
        }
      });
      var1.setNegativeButton(R.string.activation_preference_contact_us, new OnClickListener() {
        public void onClick(DialogInterface var1, int var2) {
          ActionBarPreferenceActivity.this.sendSupportMail();
        }
      });
      var1.setMessage(R.string.activation_preference_error_too_many_wrong_activation_code);
      var1.show();
    }

    PersistedDataManager.saveInteger("promotion_redeem_fail_count", var2, this);
  }

  public static class PreferencesFragment extends PreferenceFragment implements FeatureManagerObserver {
    public boolean showActivation = true;

    public PreferencesFragment() {
    }

    private void addCurrentSubscriptionInfo() {
      Subscription var1 = FeatureManager.getInstance().getActiveSubscription();
      if(var1 != null) {
        InAppPurchase var2 = FeatureManager.getInstance().getInAppPurchase(var1.getIdentifier());
        SkuDetails var3 = FeatureManager.getInstance().getPurchaseDetails(var1.getIdentifier());
        if(var2 != null && var3 != null) {
          String var4;
          if(var2.getSubscriptionDuration().intValue() == -1) {
            var4 = " - ";
          } else {
            var4 = "/";
          }

          //var4 = this.get.getString(R.string.current_subscription_label) + " " + var3.getPrice() + var4 + SubscriptionBuilderUtils.getDurationUnitText(this.getContext(), var2);
          var4 = getActivity().getApplicationContext().getString(R.string.current_subscription_label) + " " + var3.getPrice() + var4 + SubscriptionBuilderUtils.getDurationUnitText(getActivity().getApplicationContext(), var2);
          PreferenceManager var5 = this.getPreferenceManager();
          if(var5 != null) {
            Preference var6 = var5.findPreference("upgradeSubscription");
            if(var6 != null) {
              var6.setSummary(var4);
              return;
            }
          }
        }
      }

    }

    private void removeActivationPreference() {
      this.removePreference("activationCode");
    }

    private void removeAudioFocusPreference() {
      this.removePreference("useAudioFocus");
    }

    private void removePreference(String var1) {
      PreferenceManager var2 = this.getPreferenceManager();
      if(var2 != null) {
        Preference var3 = var2.findPreference(var1);
        if(var3 != null) {
          this.getPreferenceScreen().removePreference(var3);
          return;
        }
      }

    }

    private void removeUpgradeSubscriptionPreference() {
      this.removePreference("upgradeSubscription");
    }

    private void setupAudioFocusRealValue() {
      String var1 = AudioFocusManager.getAudioFocusValue();
      if(!this.getPreferenceManager().getSharedPreferences().getString("useAudioFocus", "1").equals(var1)) {
        Editor var2 = this.getPreferenceManager().getSharedPreferences().edit();
        var2.putString("useAudioFocus", var1);
        var2.apply();
      }

    }

    private void setupPreferenceListeners() {
      this.findPreference("showWalkthrough").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
        public boolean onPreferenceClick(Preference var1) {
          RelaxAnalytics.logShowWalkthroughFromSettings();
          SoundManager.getInstance().clearAll();
          SoundVolumeManager.getInstance().resetHint();
          NavigationHelper.showTutorialForced(PreferencesFragment.this.getActivity());
          return true;
        }
      });
      this.findPreference("useNativePlayer").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
        public boolean onPreferenceChange(Preference var1, Object var2) {
          RelaxAnalytics.logLoopCorrectionModeChanged(var2.toString());
          return true;
        }
      });
      this.findPreference("useAudioFocus").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
        public boolean onPreferenceChange(Preference var1, Object var2) {
          AudioFocusManager.setAudioFocusEnabled(var2.toString());
          SoundManager.getInstance().pauseAll(true);
          return true;
        }
      });
    }

    private boolean shouldShowAudiFocus() {
      return RelaxMelodiesApp.getInstance().isBackgroundSoundEnabled();
    }

    private boolean shouldShowSubscriptionUpgrades() {
      if(FeatureManager.getInstance().isSubscriptionUpgradeAvailable()) {
        Subscription var1 = FeatureManager.getInstance().getActiveSubscription();
        if(var1 != null && var1.isAutoRenewable()) {
          Iterator var2 = SubscriptionOfferResolver.getThreeButtonSubscriptions().iterator();

          InAppPurchase var3;
          do {
            do {
              if(!var2.hasNext()) {
                return false;
              }

              var3 = (InAppPurchase)var2.next();
            } while(!var3.isSubscriptionAutoRenewable() && var3.getSubscriptionDuration().intValue() != -1);
          } while(var3.getIdentifier().equals(var1.getIdentifier()));

          return true;
        }
      }

      return false;
    }

    public void onCreate(Bundle var1) {
      super.onCreate(var1);
      boolean var2 = this.shouldShowAudiFocus();
      if(var2) {
        this.setupAudioFocusRealValue();
      }

      this.addPreferencesFromResource(R.xml.settings);
      this.setupPreferenceListeners();
      if(!var2) {
        this.removeAudioFocusPreference();
      }

      if(!this.showActivation) {
        this.removeActivationPreference();
      }

      if(!this.shouldShowSubscriptionUpgrades()) {
        this.removeUpgradeSubscriptionPreference();
      } else {
        this.addCurrentSubscriptionInfo();
        FeatureManager.getInstance().registerObserver(this);
      }
    }

    public void onDestroy() {
      super.onDestroy();
      FeatureManager.getInstance().unregisterObserver(this);
    }

    public void onFeatureDownloaded(InAppPurchase var1, String[] var2) {
    }

    public void onFeatureManagerSetupFinished() {
    }

    public void onFeatureUnlocked(InAppPurchase var1, String var2) {
    }

    public void onPurchaseCompleted(InAppPurchase var1, Purchase var2, Date var3) {
    }

    public void onPurchasesAvailable(List<InAppPurchase> var1) {
    }

    public void onSubscriptionChanged(Subscription var1, boolean var2) {
      if(!this.shouldShowSubscriptionUpgrades()) {
        this.removeUpgradeSubscriptionPreference();
      } else {
        this.addCurrentSubscriptionInfo();
      }
    }

    public void onUnresolvedPurchases(List<String> var1) {
    }
  }
}
