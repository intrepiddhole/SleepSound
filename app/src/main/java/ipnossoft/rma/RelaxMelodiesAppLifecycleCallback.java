package ipnossoft.rma;

import android.app.Activity;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import com.ipnossoft.api.featuremanager.FeatureManager;
import com.ipnossoft.api.soundlibrary.SoundLibrary;
import ipnossoft.rma.feature.RelaxFeatureManager;
import ipnossoft.rma.feature.RelaxFeatureManagerCallback;
import ipnossoft.rma.media.SoundManager;
import ipnossoft.rma.media.SoundService;
import ipnossoft.rma.media.SoundServiceConnection;
import ipnossoft.rma.util.Analytics;
import ipnossoft.rma.util.RelaxAnalytics;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

class RelaxMelodiesAppLifecycleCallback implements ActivityLifecycleCallbacks {
  static IntentFilter connectivityChangeIntentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
  static ConnectivityChangeReceiver connectivityChangeReceiver = new ConnectivityChangeReceiver();
  private Set<RelaxMelodiesAppLifecycleCallback.AppDelegateObserver> appDelegateObservers = new HashSet();
  private Context applicationContext;
  private boolean connectivityChangeReceiverRegistered = false;
  private boolean ignoreNextDestroyedActivity = false;
  private boolean isForeground = false;
  private boolean isScreenLocked = false;
  private boolean isServiceActive = false;
  private boolean isShowingBackgroundSoundNotification = false;
  private Activity lastStartedActivity;
  private SoundServiceConnection soundServiceConnection = new SoundServiceConnection();

  RelaxMelodiesAppLifecycleCallback() {
  }

  private void connectSoundManagerService() {
    if(!this.isServiceActive) {
      this.applicationContext = RelaxMelodiesApp.getInstance().getApplicationContext();
      Intent var1 = new Intent(this.applicationContext, SoundService.class);
      this.applicationContext.startService(var1);
      this.applicationContext.bindService(var1, this.soundServiceConnection, 1);
      this.isServiceActive = true;
    }

  }

  private void initServicesIfNecessary(Activity var1) {
    if(!(var1 instanceof SplashScreenActivity) && !RelaxMelodiesApp.areServiceInitialized) {
      RelaxMelodiesApp.areServiceInitialized = true;
      Context var2 = RelaxMelodiesApp.getInstance().getApplicationContext();
      RelaxAnalytics.initialize(var2);
      RelaxFeatureManager.configureRelaxFeatureManager(var1, new RelaxFeatureManagerCallback());
      SoundLibrary.getInstance().configureSoundLibrary(var2);
      FeatureManager.getInstance().fetchAvailableFeatures();
    }

  }

  private boolean isScreenOn(Activity var1) {
    return ((PowerManager)var1.getSystemService("power")).isScreenOn();
  }

  private boolean isSwitchingActivity(Activity var1) {
    return var1 != this.lastStartedActivity;
  }

  private void onEnterBackground() {
    this.tryUnregisteringConnectivityListener();
    this.isForeground = false;
    Analytics.logEnteredBackground();
    Analytics.endSession();
    Iterator var1 = this.appDelegateObservers.iterator();

    while(var1.hasNext()) {
      ((RelaxMelodiesAppLifecycleCallback.AppDelegateObserver)var1.next()).onEnterBackground();
    }

  }

  private void onEnterForeground() {
    this.isForeground = true;
    RelaxMelodiesApp.getInstance().incrementOrResetCounters();
    Analytics.startSession();
    Analytics.logEnteredForeground();
    Iterator var1 = this.appDelegateObservers.iterator();

    while(var1.hasNext()) {
      ((RelaxMelodiesAppLifecycleCallback.AppDelegateObserver)var1.next()).onEnterForeground();
    }

  }

  private void onScreenLocked() {
    this.isScreenLocked = true;
    Analytics.logScreenLocked();
    Analytics.endSession();
    Iterator var1 = this.appDelegateObservers.iterator();

    while(var1.hasNext()) {
      ((RelaxMelodiesAppLifecycleCallback.AppDelegateObserver)var1.next()).onScreenLocked();
    }

  }

  private void onScreenUnlocked() {
    this.isScreenLocked = false;
    RelaxMelodiesApp.getInstance().incrementOrResetCounters();
    Analytics.startSession();
    Analytics.logScreenUnlocked();
    Iterator var1 = this.appDelegateObservers.iterator();

    while(var1.hasNext()) {
      ((RelaxMelodiesAppLifecycleCallback.AppDelegateObserver)var1.next()).onScreenUnlocked();
    }

  }

  private void tryUnbindingService() {
    try {
      if(this.applicationContext != null) {
        this.applicationContext.unbindService(this.soundServiceConnection);
      }

      this.isServiceActive = false;
    } catch (Exception var2) {
      Log.e("RelaxAppLifecycle", "Failed to unbind soundServiceConnection", var2);
    }
  }

  private void tryUnregisteringConnectivityListener() {
    try {
      if(this.applicationContext != null && this.connectivityChangeReceiverRegistered) {
        this.applicationContext.unregisterReceiver(connectivityChangeReceiver);
        this.connectivityChangeReceiverRegistered = false;
      }

    } catch (Exception var2) {
      Log.e("RelaxAppLifecycle", "Failed to unregister connectivityChangeReceiver", var2);
    }
  }

  public void onActivityCreated(Activity var1, Bundle var2) {
    this.initServicesIfNecessary(var1);
  }

  public void onActivityDestroyed(Activity var1) {
  }

  public void onActivityPaused(Activity var1) {
    this.ignoreNextDestroyedActivity = false;
  }

  public void onActivityResumed(Activity var1) {
    this.lastStartedActivity = var1;
    this.ignoreNextDestroyedActivity = true;
    if(this.isShowingBackgroundSoundNotification) {
      RelaxMelodiesApp.getInstance().removeBackgroundSoundNotification();
      this.isShowingBackgroundSoundNotification = false;
    }

  }

  public void onActivitySaveInstanceState(Activity var1, Bundle var2) {
  }

  public void onActivityStarted(Activity var1) {
    this.connectSoundManagerService();
    if(!this.isForeground && this.applicationContext != null) {
      this.applicationContext.registerReceiver(connectivityChangeReceiver, connectivityChangeIntentFilter);
      this.connectivityChangeReceiverRegistered = true;
      this.onEnterForeground();
    } else if(this.isScreenLocked) {
      this.onScreenUnlocked();
    }

    RelaxMelodiesApp.getInstance().onActivityStarted(var1);
  }

  public void onActivityStopped(Activity var1) {
    boolean var4 = false;
    boolean var5 = this.isSwitchingActivity(var1);
    boolean var6 = this.isScreenOn(var1);
    boolean var2;
    if(var6 && !var5) {
      var2 = true;
    } else {
      var2 = false;
    }

    boolean var3 = var4;
    if(!var6) {
      var3 = var4;
      if(!var5) {
        var3 = true;
      }
    }

    if(var2 && !RelaxMelodiesApp.getInstance().isBackgroundSoundEnabled() && this.applicationContext != null) {
      var5 = SoundManager.getInstance().isPlaying();
      if(this.isServiceActive) {
        this.tryUnbindingService();
        SoundService.getInstance().stopService();
      }

      if(var5) {
        this.isShowingBackgroundSoundNotification = true;
        RelaxMelodiesApp.getInstance().showBackgroundSoundNotification(var1);
      }
    }

    if(var2) {
      this.onEnterBackground();
    } else if(var3) {
      this.onScreenLocked();
    }

    RelaxMelodiesApp.getInstance().onActivityStopped(var1);
  }

  public void registerAppDelegateObserver(RelaxMelodiesAppLifecycleCallback.AppDelegateObserver var1) {
    this.appDelegateObservers.add(var1);
  }

  public void unregisterAppDelegateObserver(RelaxMelodiesAppLifecycleCallback.AppDelegateObserver var1) {
    this.appDelegateObservers.remove(var1);
  }

  public interface AppDelegateObserver {
    void onEnterBackground();

    void onEnterForeground();

    void onScreenLocked();

    void onScreenUnlocked();
  }
}
