package ipnossoft.rma;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences.Editor;
import android.os.IBinder;
import android.support.multidex.MultiDexApplication;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.applinks.AppLinkData;
import com.facebook.applinks.AppLinkData.CompletionHandler;
import com.ipnossoft.api.featuremanager.FeatureManager;
import com.ipnossoft.api.newsservice.NewsService;
import com.ipnossoft.api.newsservice.NewsServiceListener;
import com.ipnossoft.api.newsservice.impl.NewsServiceImpl;
import ipnossoft.rma.KillBackgroundSoundNotificationService.KillBinder;
import ipnossoft.rma.RelaxMelodiesAppLifecycleCallback.AppDelegateObserver;
import ipnossoft.rma.free.R;
import ipnossoft.rma.media.CustomHeadsetBroadcastReceiver;
import ipnossoft.rma.media.SoundManager;
import ipnossoft.rma.media.SoundSelection;
import ipnossoft.rma.timer.TimerTask;
import ipnossoft.rma.ui.ReviewDialog;
import ipnossoft.rma.upgrade.SubscriptionActivity;
import ipnossoft.rma.util.Analytics;
import ipnossoft.rma.util.Utils;
import java.util.Properties;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public abstract class RelaxMelodiesApp extends MultiDexApplication {
  public static final String DID_SHOW_TUTORIAL = "did_show_tutorial";
  private static int FORCE_WALKTHROUGH_STARTING_FROM_VERSION = 79;
  public static final String IS_SHOWING_TUTORIAL = "is_showing_tutorial";
  public static int NOTIFICATION_BACKGROUND_SOUND_ID = 2;
  private static final long NO_TIMER = -1L;
  public static final String PREF_FILE_FAVORITES = "ipnossoft.rma.favorites";
  private static final String PREF_FILE_SELECTED = "ipnossoft.rma.soundmanager.selected";
  private static final String PREF_IS_PREMIUM = "is_premium";
  public static final String PREF_IS_PROMOTION_PREMIUM = "is_promotion_premium";
  private static final String PREF_KEY_APP_CURRENT_VERSION = "app_current_version";
  public static final String PREF_KEY_APP_STARTUP_COUNTER = "app_startup_counter";
  public static final String PREF_KEY_APP_STARTUP_REVIEW_PROCESS_COUNTER = "app_startup_review_counter";
  public static final String PREF_KEY_REVIEW_SOUNDS_GIFTED = "free_review_sounds_added";
  private static final String PREF_KEY_TIMER_REMAINING = "timer_remaining";
  private static final String PREF_KEY_TIMER_STOP_APP = "timer_stop_app";
  private static final String PREF_SELECTED_SOUNDS_JSON = "app_selected_sounds";
  public static boolean areServiceInitialized = false;
  private static RelaxMelodiesApp instance;
  private static String relaxServerUrlCache = null;
  private RelaxMelodiesAppLifecycleCallback activityLifeCycleCallback;
  private boolean appStarted;
  private CustomHeadsetBroadcastReceiver customHeadsetBroadcastReceiver;
  private GlobalVolumeManager globalVolumeManager;
  private MarketCustomParam market;
  private NewsService newsService;
  private TimerTask timerTask;

  public RelaxMelodiesApp() {
  }

  private Notification buildBackgroundSoundNotification() {
    Builder var1 = new Builder(this);
    var1.setSmallIcon(R.drawable.ic_stat_notify).setContentTitle(this.getResources().getString(R.string.notification_title)).setContentText(this.getResources().getString(R.string.notification_background_sound_text));
    Intent var2 = new Intent(this, SubscriptionActivity.class);
    var2.putExtra("upgrade_from_notification", true);
    var1.setContentIntent(PendingIntent.getActivity(this, 0, var2, PendingIntent.FLAG_UPDATE_CURRENT));
    return var1.build();
  }

  private int getCurrentMajorVersion() {
    return PersistedDataManager.getInteger("app_current_version", -1, this);
  }

  public static RelaxMelodiesApp getInstance() {
    return instance;
  }

  private int getPackageMajorVersion() {
    return Integer.parseInt(Utils.getPackageInfo(this).versionName.split("\\.")[0]);
  }

  public static String getRelaxServerURL() {
    if(relaxServerUrlCache == null) {
      relaxServerUrlCache = RelaxPropertyHandler.getInstance().getProperties().getProperty("RELAX_SERVER_URL");
    }

    return relaxServerUrlCache;
  }

  private void initFacebook() {
    FacebookSdk.sdkInitialize(this);
    AppEventsLogger.activateApp(this);
    AppLinkData.fetchDeferredAppLinkData(this, new CompletionHandler() {
      public void onDeferredAppLinkDataFetched(AppLinkData var1) {
        if(var1 != null) {
          Analytics.attributeInstall(var1.getTargetUri().getQueryParameter("referrer"), RelaxMelodiesApp.this.getApplicationContext());
        }

      }
    });
  }

  public static boolean isFreeVersion() {
    return Boolean.parseBoolean(RelaxPropertyHandler.getInstance().getProperties().getProperty("IS_FREE_BUILD"));
  }

  public static Boolean isPremium() {
    boolean var0 = false;
    if(PersistedDataManager.getBoolean("is_premium", false, getInstance().getApplicationContext()).booleanValue() || PersistedDataManager.getBoolean("is_promotion_premium", false, getInstance().getApplicationContext()).booleanValue() || !isFreeVersion()) {
      var0 = true;
    }

    return Boolean.valueOf(var0);
  }

  private void saveNewCurrentVersion() {
    PersistedDataManager.saveInteger("app_current_version", this.getPackageMajorVersion(), this);
  }

  private void setupHeadsetReceiver() {
    IntentFilter var1 = new IntentFilter();
    var1.addAction("android.media.AUDIO_BECOMING_NOISY");
    var1.addAction("android.intent.action.HEADSET_PLUG");
    this.customHeadsetBroadcastReceiver = new CustomHeadsetBroadcastReceiver();
    this.registerReceiver(this.customHeadsetBroadcastReceiver, var1);
  }

  private boolean updateVersion() {
    int var3 = this.getPackageMajorVersion();
    int var2 = this.getCurrentMajorVersion();
    int var1;
    if(var2 >= 99) {
      var1 = 6;
    } else {
      var1 = var2;
      if(var2 > var3) {
        var1 = 5;
      }
    }

    return var1 != var3;
  }

  public boolean areAdsEnabled() {
    return !FeatureManager.getInstance().hasActiveSubscription() && !isPremium().booleanValue();
  }

  public void completeTapJoyRewardAction() {
  }

  public String getAppName() {
    return this.getString(R.string.app_name);
  }

  public String getFlappyMonsterProductLink() {
    switch(getMarketCustomParam()) {
      case AMAZON:
        return this.getString(R.string.product_link_flappy_monster_amazon);
      case SAMSUNG:
        return this.getString(R.string.product_link_flappy_monster_samsung);
      default:
        return this.getString(R.string.product_link_flappy_monster);
    }
  }

  public abstract Class<? extends MainActivity> getMainActivityClass();

  public MarketCustomParam getMarketCustomParam() {
    if(this.market == null) {
      this.market = MarketCustomParam.fromString(this.getString(R.string.custom_market));
    }

    return this.market;
  }

  public String getMarketLink() {
    switch(getMarketCustomParam()) {
      case AMAZON:
        return this.getString(R.string.market_link_amazon);
      case SAMSUNG:
        return this.getString(R.string.market_link_samsung);
      case BLACKBERRY:
        return this.getString(R.string.market_link_blackberry);
      case MOBIROO:
        return this.getString(R.string.market_link_mobiroo);
      case SNAPPCLOUD:
        return this.getString(R.string.market_link_snappcloud);
      case TSTORE:
        return this.getString(R.string.market_link_tstore);
      default:
        return this.getString(R.string.market_link);
    }
  }

  public String getMarketName() {
    return this.getMarketCustomParam().name().toLowerCase();
  }

  public NewsService getNewsService() {
    return this.newsService;
  }

  public String getRelaxMeditationProductLink(String var1) {
    switch(getMarketCustomParam()) {
      case AMAZON:
        return this.getString(R.string.product_link_relax_meditation_amazon);
      case SAMSUNG:
        return this.getString(R.string.product_link_relax_meditation_samsung);
      default:
        StringBuilder var2 = (new StringBuilder()).append(this.getString(R.string.product_link_relax_meditation));
        if(var1 != null) {
          var1 = "&referrer=utm_source%relax_melodies%26utm_medium%3D" + var1;
        } else {
          var1 = "";
        }

        return var2.append(var1).toString();
    }
  }

  public TimerTask getTimerTask() {
    return this.timerTask;
  }

  public String getWebMarketLink(String var1) {
    switch(getMarketCustomParam()) {
      case AMAZON:
        return this.getString(R.string.web_market_link_amazon);
      case SAMSUNG:
        return this.getString(R.string.web_market_link_samsung);
      default:
        StringBuilder var2 = (new StringBuilder()).append(this.getString(R.string.web_market_link));
        if(var1 != null) {
          var1 = "&referrer=utm_source%relax_melodies%26utm_medium%3D" + var1;
        } else {
          var1 = "";
        }

        return var2.append(var1).toString();
    }
  }

  public void incrementOrResetCounters() {
    if(this.updateVersion()) {
      int var1 = this.getCurrentMajorVersion();
      if(var1 <= FORCE_WALKTHROUGH_STARTING_FROM_VERSION) {
        PersistedDataManager.saveBoolean("did_show_tutorial", false, this);
      }

      if(var1 > 0 && !AudioFocusManager.hasAudioFocusSetting()) {
        AudioFocusManager.setAudioFocusEnabled("0");
      }

      this.saveNewCurrentVersion();
      PersistedDataManager.saveInteger("app_startup_counter", 0, this);
      if(!ReviewDialog.isUserNeutral(this)) {
        PersistedDataManager.saveInteger("app_startup_review_counter", 0, this);
        ReviewDialog.resetRatingPreference(this);
      }

    } else {
      PersistedDataManager.incrementCounter("app_startup_counter", this);
      PersistedDataManager.incrementCounter("app_startup_review_counter", this);
    }
  }

  public boolean isAppStarted() {
    return this.appStarted;
  }

  public boolean isBackgroundSoundEnabled() {
    return FeatureManager.getInstance().hasActiveSubscription() || isPremium().booleanValue();
  }

  public boolean isDebug() {
    return (this.getApplicationInfo().flags & 2) != 0;
  }

  public boolean isForceBrowser() {
    return this.getResources().getBoolean(R.bool.custom_force_browser);
  }

  public void onActivityStarted(Activity var1) {
  }

  public void onActivityStopped(Activity var1) {
  }

  public void onCreate() {
    super.onCreate();
    instance = this;
    CalligraphyConfig.initDefault((new uk.co.chrisjenx.calligraphy.CalligraphyConfig.Builder()).setDefaultFontPath("fonts/metric_regular.ttf").build());
    this.initFacebook();
    this.activityLifeCycleCallback = new RelaxMelodiesAppLifecycleCallback();
    this.registerActivityLifecycleCallbacks(this.activityLifeCycleCallback);
    this.setupHeadsetReceiver();
  }

  public void onTerminate() {
    super.onTerminate();
    if(this.customHeadsetBroadcastReceiver != null) {
      this.unregisterReceiver(this.customHeadsetBroadcastReceiver);
    }

    FeatureManager.getInstance().onDestroy();
  }

  public void registerAppDelegateObserver(AppDelegateObserver var1) {
    if(this.activityLifeCycleCallback != null) {
      this.activityLifeCycleCallback.registerAppDelegateObserver(var1);
    }

  }

  public void removeBackgroundSoundNotification() {
    ((NotificationManager)this.getSystemService(NOTIFICATION_SERVICE)).cancel(NOTIFICATION_BACKGROUND_SOUND_ID);
  }

  public void restoreSelectedSounds(Activity var1) {
    String var2 = this.getSharedPreferences("ipnossoft.rma.soundmanager.selected", 0).getString("app_selected_sounds", (String)null);
    if(var2 != null) {
      SoundSelection var3 = (SoundSelection)Utils.jsonToObject(var2, SoundSelection.class);
      if(var3 != null) {
        SoundManager.getInstance().restoreSoundSelection(var3, var1);
      }
    }

  }

  public void restoreTimer(Activity var1) {
    if(this.timerTask == null) {
      long var3 = PersistedDataManager.getLong("timer_remaining", -1L, this);
      if(var3 != -1L) {
        boolean var2 = PersistedDataManager.getBoolean("timer_stop_app", false, this).booleanValue();
        this.timerTask = new TimerTask(var3, SoundManager.getInstance(), var2, var1);
        Utils.executeTask(this.timerTask, new Void[0]);
        return;
      }
    }

  }

  public void retrieveNewsCounter(NewsServiceListener var1) {
    if(this.newsService == null) {
      Properties var2 = RelaxPropertyHandler.getInstance().getProperties();
      this.newsService = new NewsServiceImpl(var2.getProperty(RelaxPropertyHandler.RELAX_SERVER_URL), var2.getProperty(RelaxPropertyHandler.RELAX_SERVER_USERNAME), var2.getProperty(RelaxPropertyHandler.RELAX_SERVER_API_KEY), var2.getProperty(RelaxPropertyHandler.RELAX_APP_CODE), this.getApplicationContext());
      this.newsService.addListener(var1);
    }

    this.newsService.fetchNews();
  }

  public void saveSelectedSounds() {
    Editor var1 = this.getSharedPreferences("ipnossoft.rma.soundmanager.selected", 0).edit();
    var1.clear();
    String var2 = Utils.objectToJson(SoundManager.getInstance().getSoundSelection());
    Log.d("RelaxMelodiesApp", "Saved sound selection: " + var2);
    var1.putString("app_selected_sounds", var2);
    var1.apply();
  }

  public void setAppStarted(boolean var1) {
    this.appStarted = var1;
  }

  public void setGlobalVolumeManager(GlobalVolumeManager var1) {
    this.globalVolumeManager = var1;
  }

  public void setTimerTask(TimerTask var1) {
    this.timerTask = var1;
  }

  public void showBackgroundSoundNotification(final Activity var1) {
    ServiceConnection var2 = new ServiceConnection() {
      public void onServiceConnected(ComponentName var1x, IBinder var2) {
        ((KillBinder)var2).service.startService(new Intent(var1, KillBackgroundSoundNotificationService.class));
        ((NotificationManager)RelaxMelodiesApp.this.getSystemService(NOTIFICATION_SERVICE)).notify(RelaxMelodiesApp.NOTIFICATION_BACKGROUND_SOUND_ID, RelaxMelodiesApp.this.buildBackgroundSoundNotification());
      }

      public void onServiceDisconnected(ComponentName var1x) {
      }
    };
    this.bindService(new Intent(var1, KillBackgroundSoundNotificationService.class), var2, BIND_AUTO_CREATE);
  }

  public void startTimer(Activity var1, long var2, boolean var4) {
    if(this.timerTask == null) {
      this.timerTask = new TimerTask(var2, SoundManager.getInstance(), var4, var1);
      Utils.executeTask(this.timerTask, new Void[0]);
    }
  }

  public void stopAndSaveTimerTask() {
    if(this.timerTask != null && !this.timerTask.isFinished()) {
      PersistedDataManager.saveLong("timer_remaining", this.timerTask.computeRemaining(), this);
      PersistedDataManager.saveBoolean("timer_stop_app", this.timerTask.isStopApp(), this);
      this.timerTask.cancel(true);
      this.timerTask.waitIsInterrupted();
    } else {
      PersistedDataManager.saveLong("timer_remaining", -1L, this);
    }

    this.timerTask = null;
  }

  public void unregisterAppDelegateObserver(AppDelegateObserver var1) {
    if(this.activityLifeCycleCallback != null) {
      this.activityLifeCycleCallback.unregisterAppDelegateObserver(var1);
    }

  }

  public void updateVolumeBar() {
    if(this.globalVolumeManager != null) {
      this.globalVolumeManager.updateSeekBar();
    }

  }
}
