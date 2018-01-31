package ipnossoft.rma;

import android.animation.Animator.AnimatorListener;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Build.VERSION;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.TouchDelegate;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ipnossoft.api.dynamiccontent.model.InAppPurchase;
import com.ipnossoft.api.featuremanager.FeatureManager;
import com.ipnossoft.api.featuremanager.FeatureManagerObserver;
import com.ipnossoft.api.featuremanager.data.Subscription;
import com.ipnossoft.api.newsservice.NewsServiceListener;
import com.ipnossoft.api.newsservice.model.News;
import com.ipnossoft.api.soundlibrary.Sound;
import com.ipnossoft.api.soundlibrary.SoundLibrary;
import com.ipnossoft.api.soundlibrary.sounds.BinauralSound;
import com.ipnossoft.api.soundlibrary.sounds.GuidedMeditationSound;
import com.ipnossoft.api.soundlibrary.sounds.IsochronicSound;
import com.ipnossoft.api.soundlibrary.sounds.SoundState;
import ipnossoft.rma.MainFragment.OnMainFragmentReadyListener;
import ipnossoft.rma.RelaxMelodiesAppLifecycleCallback.AppDelegateObserver;
import ipnossoft.rma.animation.ShootingStarAnimator.ShootingStarEnabler;
import ipnossoft.rma.exceptions.SoundLimitReachedException;
import ipnossoft.rma.favorites.FavoriteFragment;
import ipnossoft.rma.feature.RelaxFeatureManager;
import ipnossoft.rma.feature.RelaxFeatureManagerCallback;
import ipnossoft.rma.free.R;
import ipnossoft.rma.guidedmeditations.GuidedMeditationFragment;
import ipnossoft.rma.guidedmeditations.GuidedMeditationStatus;
import ipnossoft.rma.guidedmeditations.GuidedMeditationStatusListener;
import ipnossoft.rma.media.SoundManager;
import ipnossoft.rma.media.SoundManagerObserver;
import ipnossoft.rma.media.SoundService;
import ipnossoft.rma.media.SoundVolumeManager;
import ipnossoft.rma.media.NativeMediaPlayerMonitor.Mode;
import ipnossoft.rma.media.SoundVolumeManager.VolumeBarEnabler;
import ipnossoft.rma.nook.NookWarningDialog;
import ipnossoft.rma.review.ReviewProcess;
import ipnossoft.rma.timer.TimerFragment;
import ipnossoft.rma.timer.TimerListener;
import ipnossoft.rma.timer.TimerTask;
import ipnossoft.rma.tstore.TstoreManager;
import ipnossoft.rma.ui.button.SoundButton;
import ipnossoft.rma.ui.button.SoundButtonGestureListener;
import ipnossoft.rma.ui.dialog.RelaxDialog.Builder;
import ipnossoft.rma.ui.dialog.RelaxDialog.RelaxDialogButtonOrientation;
import ipnossoft.rma.ui.navigation.NavigationTabBarHandler;
import ipnossoft.rma.ui.parallax.RelaxScrollViewGraphicsAnimator;
import ipnossoft.rma.ui.scroller.RelaxScrollEventHandler;
import ipnossoft.rma.ui.scroller.RelaxScrollView;
import ipnossoft.rma.ui.scrollview.DisableTouchScrollView;
import ipnossoft.rma.ui.shootingstar.ShootingStarHelper;
import ipnossoft.rma.ui.tutorial.TutorialActivity;
import ipnossoft.rma.upgrade.SpecialOfferPopupHelper;
import ipnossoft.rma.upgrade.SubscriptionOfferResolver;
import ipnossoft.rma.upgrade.SubscriptionOfferResolverListener;
import ipnossoft.rma.upgrade.SubscriptionTrialHelper;
import ipnossoft.rma.util.RelaxAnalytics;
import ipnossoft.rma.util.Utils;
import ipnossoft.rma.web.MoreFragment;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.jetbrains.annotations.NotNull;
import org.onepf.oms.appstore.googleUtils.Purchase;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public abstract class MainActivity extends AppCompatActivity implements SoundButtonGestureListener, TimerListener, OnClickListener, OnMainFragmentReadyListener, SoundManagerObserver, FeatureManagerObserver, NewsServiceListener, RelaxScrollEventHandler, ShootingStarEnabler, AppDelegateObserver, VolumeBarEnabler, GuidedMeditationStatusListener, SubscriptionOfferResolverListener {
  private static final int DEFAULT_OPENINGS_UNTIL_NEW_PRODUCT_POPUP = 6;
  private static final int HIT_BACK_AGAIN_TO_LEAVE_TIMEOUT = 3;
  private static final long NETWORK_REFRESH_BUFFER_TIME_SECONDS = 3600L;
  private static final String PREF_LAST_OS_USED = "PREF_LAST_OS_USED";
  private static final String PREF_NEW_PRODUCT_START_COUNTER = "new_product_start_counter";
  private static final String PREF_SHOW_NEW_PRODUCT = "show_new_product";
  private static final String PREF_SHOW_NOOK_WARNING = "show_nook_warning";
  private static final int SHOW_TUTORIAL_RETURN_REQUEST_CODE = 1001;
  static final String TAG = "MainActivity";
  private static final String UTM_MEDIUM = "popup";
  private static boolean isActivityDestroyed = false;
  private boolean aDialogHasBeenShown = false;
  boolean activationCheckRequired = false;
  int activeNavButton;
  private boolean applicationReady = false;
  protected View bottomMenu;
  private ImageButton buttonClear;
  private RelativeLayout buttonClearLayout;
  private ImageButton buttonPlay;
  private TextView buttonPlayTextView;
  View buttonProBackgroundCloudMenu;
  Button buttonProCloudMenu;
  private ImageView buttonProImageCloudMenu;
  Button currentNavigationButton;
  private boolean didCheckForTrialPopup = false;
  private boolean dontRestoreSounds;
  private FavoriteFragment favoriteFragment;
  FragmentSwitcher fragmentSwitcher;
  private GlobalVolumeManager globalVolumeManager;
  private GuidedMeditationFragment guidedMeditationFragment;
  private Runnable handlePopupsRunnable;
  private boolean handledPopupsDuringThisSession = false;
  private Handler handler = new Handler();
  private boolean hasEnteredForeground = true;
  private View homeButton;
  private boolean isActivityRunning = false;
  private View layoutVolume;
  private MarketCustomParam market;
  protected MoreFragment moreFragment;
  private boolean movingToMeditationTabOnStart;
  HorizontalScrollView navigationHorizontalScrollView;
  private NavigationTabBarHandler navigationTabBarHandler;
  private boolean onEnterForegroundOrScreenUnlockedCalledOnce = false;
  private boolean onPostResumeBringToGuidedMeditationScreen = false;
  private RelativeLayout proButtonLayout;
  private int proButtonRightMargin;
  protected RelaxScrollViewGraphicsAnimator relaxScrollViewGraphicsAnimator;
  private boolean resumeOnAudioFocus;
  private boolean safeToCommitTransactions = false;
  private RelativeLayout scrollContent;
  private boolean soundsFadeAnimationStarted = false;
  RelativeLayout splashScreenLayout;
  private long timeStampSinceLastNetworkRequest;
  private RelativeLayout timerContainer;
  private TimerFragment timerFragment;
  private TextView timerLabel;
  private TimerTask timerTask;
  protected boolean uiIsReady = false;
  private boolean wasShowingTutorial;

  public MainActivity() {
  }

  private void backToAndroidHomeScreen() {
    if(SoundManager.getInstance().isPlaying()) {
      this.moveTaskToBack(true);
    } else {
      this.finish();
    }
  }

  private boolean bufferTimeForApiCallsHasPassed() {
    if(System.currentTimeMillis() / 1000L > this.timeStampSinceLastNetworkRequest + 3600L) {
      this.timeStampSinceLastNetworkRequest = System.currentTimeMillis() / 1000L;
      return true;
    } else {
      return false;
    }
  }

  private void checkNewProductDisplay(int var1) {
    if(PersistedDataManager.getBoolean("show_new_product", true, this).booleanValue()) {
      int var2 = PersistedDataManager.getInteger("new_product_start_counter", -1, this);
      if(var2 <= -1) {
        PersistedDataManager.saveInteger("new_product_start_counter", var1, this);
        return;
      }

      this.showNewProductDialogIfRequired(var1, var2);
    }

  }

  private void displayNookWarning() {
    if(this.market == MarketCustomParam.NOOK && PersistedDataManager.getBoolean("show_nook_warning", true, this).booleanValue()) {
      NookWarningDialog.newInstance().show(this.getSupportFragmentManager(), "nook-dialog");
      PersistedDataManager.saveBoolean("show_nook_warning", false, this);
    }
  }

  private void doRefreshUIForSelection() {
    int var2 = SoundManager.getInstance().getNbSelectedSounds();
    boolean var3;
    if(var2 > 0 && SoundManager.getInstance().isPlaying()) {
      var3 = true;
    } else {
      var3 = false;
    }

    AppState.setSoundSelection(SoundManager.getInstance().getSelectedSounds());
    AppState.setPlaying(var3);
    if(this.buttonPlay != null) {
      boolean var4;
      if(var2 > 0) {
        var4 = true;
      } else {
        var4 = false;
      }

      this.setBottomMenuButtonEnabled(var4);
      this.buttonPlay.setSelected(var3);
      TextView var1 = this.buttonPlayTextView;
      if(var3) {
        var2 = R.string.pause;
      } else {
        var2 = R.string.play;
      }

      var1.setText(var2);
    }

    if(var3) {
      AudioFocusManager.requestAudioFocus();
    } else {
      AudioFocusManager.cancelAudioFocus();
    }

    if(SoundManager.getInstance().getNbSelectedSounds() > 0) {
      if(!this.wasShowingTutorial) {
        this.showNotification();
      }

    } else {
      this.removeNotification();
    }
  }

  private void flagSeenMeditationIfRelevant(InAppPurchase var1) {
    Sound var2 = (Sound)SoundLibrary.getInstance().getSound(var1.getIdentifier());
    if(var2 != null && var2 instanceof GuidedMeditationSound && var2.isFree()) {
      GuidedMeditationStatus.getInstance().flagMeditationAsSeen((GuidedMeditationSound)var2);
    }

  }

  private void forceRefreshOfScrollViewLayout() {
    if(this.getRelaxScrollView() != null) {
      this.getRelaxScrollView().forceOnMesure();
      this.getRelaxScrollView().requestLayout();
    }

  }

  private View getHomeButton() {
    if(this.homeButton == null) {
      this.homeButton = this.findViewById(R.id.button_nav_home);
    }

    return this.homeButton;
  }

  private int getNavigationButtonID(int var1) {
    switch(var1) {
      case 1:
        return R.id.button_nav_guided_meditation;
      case 2:
        return R.id.button_nav_timer;
      case 3:
        return R.id.button_nav_favorite;
      case 4:
        return R.id.button_nav_more;
      default:
        return R.id.button_nav_home;
    }
  }

  private int getOpeningsUntilNewProductPopup() {
    String var1 = RelaxPropertyHandler.getInstance().getProperties().getProperty("other.app.openings.until.prompt", String.valueOf(6));

    try {
      int var2 = Integer.parseInt(var1);
      return var2;
    } catch (NumberFormatException var3) {
      return 6;
    }
  }

  private void handlePopups() {
    this.handlePopupsRunnable = new MainActivity$5(this);
    this.handler.removeCallbacks(this.handlePopupsRunnable);
    this.handler.postDelayed(this.handlePopupsRunnable, 2000L);
  }

  private void increaseButtonArea(View var1) {
    Rect var2 = new Rect();
    var1.getHitRect(var2);
    var2.left -= 100;
    var2.bottom += 100;
    TouchDelegate var3 = new TouchDelegate(var2, var1);
    if(View.class.isInstance(var1.getParent())) {
      ((View)var1.getParent()).setTouchDelegate(var3);
    }

  }

  private void increaseControlButtonsArea() {
    this.findViewById(R.id.layoutControlButtonsLayout).post(new MainActivity$2(this));
  }

  private void initControlButtons() {
    this.buttonPlay = (ImageButton)this.findViewById(R.id.button_play);
    this.buttonPlay.setOnClickListener(this);
    boolean var1;
    if(SoundManager.getInstance().getNbSelectedSounds() > 0) {
      var1 = true;
    } else {
      var1 = false;
    }

    this.buttonPlayTextView = (TextView)this.findViewById(R.id.button_play_pause_text);
    this.buttonPlayTextView.setText(string.play);
    this.buttonClear = (ImageButton)this.findViewById(R.id.button_clear);
    this.buttonClear.setOnClickListener(this);
    this.buttonProCloudMenu = (Button)this.findViewById(R.id.button_pro_cloud_menu);
    this.buttonProBackgroundCloudMenu = this.findViewById(R.id.button_pro_background_cloud_menu);
    this.buttonProImageCloudMenu = (ImageView)this.findViewById(R.id.button_pro_image_cloud_menu);
    this.updateProButtons();
    this.proButtonLayout = (RelativeLayout)this.findViewById(R.id.button_pro_layout);
    this.proButtonLayout.setOnClickListener(this);
    this.buttonClearLayout = (RelativeLayout)this.findViewById(R.id.button_clear_layout);
    this.buttonClearLayout.setOnClickListener(this);
    this.navigationHorizontalScrollView = (HorizontalScrollView)this.findViewById(R.id.navigation_scroll);
    this.timerLabel = (TextView)this.findViewById(R.id.main_page_timer_label);
    this.timerContainer = (RelativeLayout)this.findViewById(R.id.main_page_timer_container);
    this.layoutVolume = this.findViewById(R.id.layout_sound_subvolume);
    this.setBottomMenuButtonEnabled(var1);
    SoundVolumeManager.getInstance().configureSoundVolumeManager(this.layoutVolume, this, this);
  }

  private boolean isMainActivityAvailable() {
    boolean var1 = true;
    if(VERSION.SDK_INT >= 17) {
      if(this.isDestroyed() || this.isFinishing() || isActivityDestroyed) {
        var1 = false;
      }
    } else if(this.isFinishing() || isActivityDestroyed) {
      return false;
    }

    return var1;
  }

  private void logMeditationCompletionIfNecessary(String var1, SoundState var2, float var3) {
    if(SoundLibrary.getInstance().getSound(var1) instanceof GuidedMeditationSound && var2 == SoundState.STOPPED && var3 > 0.0F || var2 == SoundState.PAUSED && (double)var3 == 1.0D) {
      RelaxAnalytics.logGuidedMeditationCompleted(var1, (int)(100.0F * var3));
    }

  }

  private void logScreenForCurrentFragment() {
    switch(this.activeNavButton) {
      case 1:
        RelaxAnalytics.logScreenMeditation();
        return;
      case 2:
        RelaxAnalytics.logScreenTimer();
        return;
      case 3:
        RelaxAnalytics.logScreenFavorites();
        return;
      case 4:
        RelaxAnalytics.logScreenMore();
        return;
      default:
        RelaxAnalytics.logScreenSounds();
    }
  }

  @NotNull
  private Fragment navigateFavorites() {
    RelaxAnalytics.logFavoritesShown();
    RelaxAnalytics.logScreenFavorites();
    if(this.favoriteFragment == null) {
      this.favoriteFragment = new FavoriteFragment();
    }

    DisplayMetrics var1 = new DisplayMetrics();
    this.getWindowManager().getDefaultDisplay().getMetrics(var1);
    this.navigationHorizontalScrollView.smoothScrollTo(var1.widthPixels, 0);
    this.activeNavButton = 3;
    return this.favoriteFragment;
  }

  private Fragment navigateGuidedMeditation() {
    RelaxAnalytics.logMeditationsShown();
    RelaxAnalytics.logScreenMeditation();
    if(this.guidedMeditationFragment == null) {
      this.guidedMeditationFragment = new GuidedMeditationFragment();
    }

    this.navigationHorizontalScrollView.smoothScrollTo(0, 0);
    this.guidedMeditationFragment.refreshSubVolumeHint();
    this.activeNavButton = 1;
    return this.guidedMeditationFragment;
  }

  private void navigateHome() {
    RelaxAnalytics.logSoundsShown();
    RelaxAnalytics.logScreenSounds();
    this.getRelaxScrollView().forceOnMesure();
    this.fragmentSwitcher.switchHome();
    this.navigationHorizontalScrollView.smoothScrollTo(0, 0);
    this.activeNavButton = 0;
  }

  @NonNull
  private Fragment navigateMore() {
    RelaxAnalytics.logMoreSectionShown();
    RelaxAnalytics.logScreenMore();
    if(this.moreFragment == null) {
      this.moreFragment = this.createMoreFragment();
    }

    DisplayMetrics var1 = new DisplayMetrics();
    this.getWindowManager().getDefaultDisplay().getMetrics(var1);
    this.navigationHorizontalScrollView.smoothScrollTo(var1.widthPixels, 0);
    this.activationCheckRequired = true;
    this.activeNavButton = 4;
    return this.moreFragment;
  }

  @NotNull
  private Fragment navigateTimer() {
    RelaxAnalytics.logTimerShown();
    RelaxAnalytics.logScreenTimer();
    if(this.timerFragment == null) {
      this.timerFragment = new TimerFragment();
    }

    this.navigationHorizontalScrollView.smoothScrollTo(0, 0);
    this.activeNavButton = 2;
    return this.timerFragment;
  }

  private void refreshUIForSelection() {
    this.runOnUiThread(new MainActivity$4(this));
  }

  private void removeNotification() {
    SoundService.getInstance().removeNotification();
  }

  private void resetFavoriteAndStaffPickSelection() {
    if(!PersistedDataManager.getString("selectedStaffPick", "", RelaxMelodiesApp.getInstance().getApplicationContext()).isEmpty()) {
      PersistedDataManager.saveString("selectedStaffPick", "", RelaxMelodiesApp.getInstance().getApplicationContext());
    }

    if(PersistedDataManager.getLong("selectedFavorite", -1L, RelaxMelodiesApp.getInstance().getApplicationContext()) != -1L) {
      PersistedDataManager.saveLong("selectedFavorite", -1L, RelaxMelodiesApp.getInstance().getApplicationContext());
    }

    if(this.favoriteFragment != null) {
      this.favoriteFragment.clearSelection();
    }

  }

  private void resetSessionFlags() {
    this.handledPopupsDuringThisSession = false;
    this.aDialogHasBeenShown = false;
    this.didCheckForTrialPopup = false;
  }

  private void restoreWithCurrentNavigationFragment(int var1, boolean var2) {
    Button var3 = (Button)this.findViewById(this.getNavigationButtonID(var1));
    if(var1 > 0) {
      this.onNavigationButton(var3, var2);
    } else {
      this.setCurrentNavigationButton(var3);
      this.fragmentSwitcher.switchHome();
    }
  }

  private void setBottomMenuButtonEnabled(boolean var1) {
    this.buttonPlay.setEnabled(var1);
    this.buttonClear.setEnabled(var1);
    if(VERSION.SDK_INT < 21) {
      float var2;
      if(var1) {
        var2 = 1.0F;
      } else {
        var2 = 0.44F;
      }

      this.buttonPlay.setAlpha(var2);
      this.buttonClear.setAlpha(var2);
    }

  }

  private void setNumberOfNewNews(int var1) {
    View var2 = this.findViewById(R.id.navigation_button_badge);
    if(var1 > 0) {
      ((TextView)this.findViewById(R.id.navigation_button_badge_label)).setText(String.valueOf(var1));
      var2.setVisibility(View.VISIBLE);
    } else {
      var2.setVisibility(View.GONE);
    }
  }

  private void setup(Bundle var1) {
    this.market = RelaxMelodiesApp.getInstance().getMarketCustomParam();
    RelaxMelodiesApp.getInstance().retrieveNewsCounter(this);
    this.timeStampSinceLastNetworkRequest = System.currentTimeMillis() / 1000L;
    FeatureManager.getInstance().registerObserver(this);
    FavoriteFragment.preloadFavorites(this);
    this.startTstoreVerification();
    this.displayNookWarning();
    this.fragmentSwitcher = new FragmentSwitcher(this);
    this.resumeOnAudioFocus = false;
    if(var1 == null) {
      this.resumeOnAudioFocus = false;
      this.activeNavButton = 0;
    } else {
      this.resumeOnAudioFocus = var1.getBoolean("resumeOnAudioFocus");
      this.activeNavButton = var1.getInt("activeNavButton");
    }

    if(var1 != null) {
      this.reinitServices();
    }

    if(SoundLibrary.getInstance().isEmpty()) {
      Log.w("MainActivity", "Sound Library was empty, Reloading Sound Library!");
      this.reloadSoundLibrary();
    }

    this.proButtonRightMargin = (int)this.getResources().getDimension(R.dimen.main_layout_control_inter_widget_spacing);
    this.onNewIntent(this.getIntent());
    if(this.findViewById(R.id.top_fragment) != null && var1 == null) {
      MainFragment var2 = new MainFragment();
      var2.setArguments(this.getIntent().getExtras());
      this.getSupportFragmentManager().beginTransaction().add(R.id.top_fragment, var2).commit();
    }
  }

  private void setupTimerTaskAndListener() {
    this.timerTask = RelaxMelodiesApp.getInstance().getTimerTask();
    if(this.timerTask != null && !this.timerTask.isFinished()) {
      this.timerTask.addListener(this);
    } else if(this.timerContainer != null) {
      this.timerContainer.setVisibility(View.GONE);
      return;
    }

  }

  private boolean shouldShowTutorial() {
    boolean var1 = false;
    if(!PersistedDataManager.getBoolean("did_show_tutorial", false, this).booleanValue()) {
      var1 = true;
    }

    return var1;
  }

  private void showNewProductDialog() {
    Builder var1 = new Builder(this, RelaxDialogButtonOrientation.VERTICAL);
    var1.setTitle(R.string.more_product_activity_title_new_app);
    var1.setPositiveButton(R.string.more_product_activity_button_text, new MainActivity$6(this));
    var1.setNegativeButton(R.string.cancel, new MainActivity$7(this));
    var1.setCustomContentView(R.layout.more_product);
    var1.show();
  }

  private void showNewProductDialogIfRequired(int var1, int var2) {
    if(var1 - var2 > this.getOpeningsUntilNewProductPopup()) {
      PersistedDataManager.saveBoolean("show_new_product", false, this);
      this.showNewProductDialog();
      RelaxAnalytics.logNewProductDialogShown();
      this.aDialogHasBeenShown = true;
    }

  }

  private void showNotification() {
    SoundService.getInstance().showNotification(this);
  }

  private void showPromoPopupIfNeeded() {
    if(!this.didCheckForTrialPopup && !this.aDialogHasBeenShown) {
      this.didCheckForTrialPopup = true;
      if(!this.wasShowingTutorial) {
        if(SubscriptionTrialHelper.shouldShowTrial()) {
          this.aDialogHasBeenShown = true;
          SubscriptionTrialHelper.showTrial(this);
        } else if(SpecialOfferPopupHelper.shouldShowSpecialOffer()) {
          this.aDialogHasBeenShown = true;
          SpecialOfferPopupHelper.showSpecialOffer(this);
          return;
        }
      }
    }

  }

  private void showReviewOrNewProductPopupsIfNeeded() {
    this.aDialogHasBeenShown = false;
    if(ReviewProcess.getInstance().shouldStart()) {
      this.aDialogHasBeenShown = true;
      ReviewProcess.getInstance().start();
    } else if(this.market != MarketCustomParam.SAMSUNG) {
      this.checkNewProductDisplay(PersistedDataManager.getInteger("app_startup_counter", 0, this));
      return;
    }

  }

  private void showSounds() {
    this.showSounds(true, true);
  }

  private void showSounds(boolean var1, boolean var2) {
    byte var3;
    if(var1) {
      var3 = 0;
    } else {
      var3 = 8;
    }

    byte var4;
    if(var1) {
      var4 = 1;
    } else {
      var4 = 0;
    }

    short var5;
    if(var2) {
      var5 = 250;
    } else {
      var5 = 0;
    }

    if(var1) {
      this.getRelaxScrollView().setVisibility(View.VISIBLE);
    }

    if(this.soundsFadeAnimationStarted) {
      this.cancelAnimations(this.getRelaxScrollView(), var1);
      this.soundsFadeAnimationStarted = false;
    }

    this.getRelaxScrollView().animate().alpha((float)var4).setDuration((long)var5).setListener(new MainActivity$3(this, var1));
    this.findViewById(R.id.background_light_scroll_view).animate().alpha((float)var4).setDuration((long)var5);
    this.findViewById(R.id.graphics_dark_overlay_image_view).animate().alpha((float)var4).setDuration((long)var5);
    this.findViewById(R.id.mountains_overlay_scroll_view).animate().alpha((float)var4).setDuration((long)var5);
    if(SoundVolumeManager.getInstance().isVolumeBarVisible()) {
      this.layoutVolume.setVisibility(var3);
    }

    this.showTimerLabel();
  }

  private void showTutorial() {
    this.startActivityForResult(new Intent(this, TutorialActivity.class), 1001);
  }

  private void startTstoreVerification() {
    if(this.market == MarketCustomParam.TSTORE) {
      (new TstoreManager(this)).startVerification();
    }

  }

  private void updateCurrentLoopMode() {
    SharedPreferences var2 = PreferenceManager.getDefaultSharedPreferences(this);
    Editor var3 = var2.edit();
    int var1 = var2.getInt("PREF_LAST_OS_USED", 0);
    if(var1 != 0 && var1 < 21 && VERSION.SDK_INT >= 21) {
      var3.putString("useNativePlayer", Mode.MODE4.value);
    }

    var3.putInt("PREF_LAST_OS_USED", VERSION.SDK_INT);
    var3.apply();
  }

  private void updateNewGuidedMeditationBadge() {
    View var2 = this.findViewById(R.id.new_badge_guided_meditation);
    int var1 = GuidedMeditationStatus.getInstance().getNumberNewlyAddedMeditations();
    if(var1 > 0) {
      ((TextView)this.findViewById(R.id.new_badge_guided_meditation_text)).setText(String.valueOf(var1));
      var2.setVisibility(View.VISIBLE);
    } else {
      var2.setVisibility(View.GONE);
    }
  }

  private void updateProButtons() {
    if(this.buttonProCloudMenu != null && this.buttonProImageCloudMenu != null && this.buttonProBackgroundCloudMenu != null) {
      Uri var1 = SubscriptionOfferResolver.getProButtonImageURL();
      if(var1 == null) {
        this.buttonProCloudMenu.setVisibility(View.VISIBLE);
        this.buttonProCloudMenu.setOnClickListener(this);
        this.buttonProCloudMenu.setText(this.getText(R.string.main_activity_pro_badge));
        this.buttonProBackgroundCloudMenu.setVisibility(View.VISIBLE);
        this.buttonProImageCloudMenu.setVisibility(View.GONE);
        return;
      }

      this.buttonProImageCloudMenu.setVisibility(View.VISIBLE);
      this.buttonProImageCloudMenu.setOnClickListener(this);
      Glide.with(this).load(var1).listener(new MainActivity$1(this)).diskCacheStrategy(DiskCacheStrategy.ALL).dontAnimate().into(this.buttonProImageCloudMenu);
    }

  }

  public void OnMainFragmentReady() {
    this.globalVolumeManager = new GlobalVolumeManager(this);
    RelaxMelodiesApp.getInstance().setGlobalVolumeManager(this.globalVolumeManager);
    this.initControlButtons();
    this.increaseControlButtonsArea();
    this.navigationTabBarHandler = new NavigationTabBarHandler(this.findViewById(R.id.navigation_tab_indicator), (RelativeLayout)this.findViewById(R.id.navigation_tab_indicator_common_parent));
    RelaxMelodiesApp.getInstance().restoreTimer(this);
    this.setupTimerTaskAndListener();
    this.scrollContent = (RelativeLayout)this.findViewById(R.id.scroll_content);
    this.getRelaxScrollView().initialize(this.scrollContent, this);
    this.relaxScrollViewGraphicsAnimator = new RelaxScrollViewGraphicsAnimator(this, this.findViewById(android.R.id.content), this.getRelaxScrollView());
    ShootingStarHelper.animateShootingStar(this);
    this.updateNewGuidedMeditationBadge();
  }

  protected void attachBaseContext(Context var1) {
    super.attachBaseContext(CalligraphyContextWrapper.wrap(var1));
  }

  public void cancelAnimations(RelaxScrollView var1, boolean var2) {
    var1.animate().setListener((AnimatorListener)null);
    var1.animate().cancel();
    byte var4;
    if(var2) {
      var4 = 0;
    } else {
      var4 = 8;
    }

    var1.setVisibility(var4);
    float var3;
    if(var2) {
      var3 = 1.0F;
    } else {
      var3 = 0.0F;
    }

    var1.setAlpha(var3);
  }

  public void clearSelection() {
    SoundManager.getInstance().clearAll();
  }

  public void configurationsLoaded() {
    this.updateProButtons();
  }

  protected MoreFragment createMoreFragment() {
    return new MoreFragment();
  }

  RelaxScrollView getRelaxScrollView() {
    return (RelaxScrollView)this.findViewById(R.id.space);
  }

  void hideProButton() {
    if(this.proButtonLayout.getVisibility() != View.INVISIBLE) {
      this.proButtonLayout.setVisibility(View.INVISIBLE);
      int var2 = this.getResources().getDimensionPixelSize(R.dimen.hide_pro_button_right_margin_adjustment);
      LayoutParams var1 = (LayoutParams)this.buttonClearLayout.getLayoutParams();
      var1.addRule(11);
      var1.setMargins(var1.leftMargin, var1.topMargin, this.proButtonRightMargin - var2, var1.bottomMargin);
      this.buttonClearLayout.setLayoutParams(var1);
    }

  }

  void hideSounds(boolean var1) {
    this.showSounds(false, var1);
  }

  public boolean isActivityRunning() {
    return this.isActivityRunning;
  }

  public boolean isSoundVolumeBarEnabled() {
    return this.currentNavigationButton != null && this.currentNavigationButton.getId() == R.id.button_nav_home;
  }

  public void newlyAddedGuidedMeditation(boolean var1) {
    this.updateNewGuidedMeditationBadge();
  }

  public void newsServiceDidFailFetchingNews(Exception var1) {
    Log.e("NewsServiceFetch", "Failed fetching news", var1);
    this.setNumberOfNewNews(0);
  }

  public void newsServiceDidFinishFetchingNews(List<News> var1) {
    this.setNumberOfNewNews(((RelaxMelodiesApp)this.getApplicationContext()).getNewsService().unreadCount());
  }

  protected void onActivityResult(int var1, int var2, Intent var3) {
    if(var1 == 1001 && (var2 == -1 || var2 == 0)) {
      if(this.splashScreenLayout != null) {
        this.splashScreenLayout.setVisibility(View.GONE);
        this.splashScreenLayout.removeAllViews();
        this.splashScreenLayout.invalidate();
        this.splashScreenLayout = null;
      }

      if(var2 == -1) {
        this.dontRestoreSounds = true;
      }

      if(var2 == -1 && Utils.getCurrentLanguageLocale(RelaxMelodiesApp.getInstance().getApplicationContext()).equals(Locale.ENGLISH.getLanguage())) {
        this.onPostResumeBringToGuidedMeditationScreen = true;
        this.movingToMeditationTabOnStart = true;
        this.showSounds(false, false);
      } else {
        this.getRelaxScrollView().forceOnMesure();
      }

      if(var2 == 0) {
        RelaxMelodiesApp.getInstance().restoreSelectedSounds(this);
      }

      if(SoundManager.getInstance().isPlaying()) {
        this.showNotification();
      }
    }

  }

  public void onApplicationReady() {
    if(this.shouldShowTutorial()) {
      this.aDialogHasBeenShown = true;
      this.wasShowingTutorial = true;
      this.showTutorial();
      this.getRelaxScrollView().setVisibility(View.GONE);
    } else if(this.splashScreenLayout != null) {
      this.splashScreenLayout.animate().alpha(0.0F).setDuration(1000L);
      this.splashScreenLayout.removeAllViews();
      this.splashScreenLayout.invalidate();
      this.splashScreenLayout = null;
    }

    if(!this.movingToMeditationTabOnStart) {
      if(this.currentNavigationButton != null) {
        this.navigationTabBarHandler.moveTabIndicatorToTabButton(this.currentNavigationButton, false);
      } else {
        this.navigationTabBarHandler.moveTabIndicatorToTabButton((Button)this.getHomeButton(), false);
      }
    }

    if(!this.dontRestoreSounds) {
      RelaxMelodiesApp.getInstance().restoreSelectedSounds(this);
    }

    RelaxMelodiesApp.getInstance().setAppStarted(true);
    if(!this.onEnterForegroundOrScreenUnlockedCalledOnce) {
      this.onEnterForeground();
      this.onEnterForegroundOrScreenUnlockedCalledOnce = true;
    }

    this.applicationReady = true;
  }

  public void onBackPressed() {
    if(this.getSupportFragmentManager().getBackStackEntryCount() > 0) {
      super.onBackPressed();
      if(this.getSupportFragmentManager().getBackStackEntryCount() == 0) {
        this.selectHomeNavigationButton();
      }

    } else {
      this.backToAndroidHomeScreen();
    }
  }

  public void onClearSounds(List<Sound> var1) {
    AudioFocusManager.cancelAudioFocus();
    ImageButton var3 = this.buttonClear;
    boolean var2;
    if(!this.buttonClear.isSelected()) {
      var2 = true;
    } else {
      var2 = false;
    }

    var3.setSelected(var2);
    SoundVolumeManager.getInstance().hideVolumeLayout();
    this.refreshUIForSelection();
    this.resetFavoriteAndStaffPickSelection();
  }

  public void onClick(View var1) {
    boolean var3 = true;
    if(var1 == this.buttonPlay) {
      if(var1.isSelected()) {
        var3 = false;
      }

      if(var3) {
        RelaxAnalytics.logResumeAllSounds();
      } else {
        RelaxAnalytics.logPauseAllSounds();
      }

      SoundManager.getInstance().togglePlayPauseAll(var3);
      var1.setSelected(var3);
      TextView var4 = this.buttonPlayTextView;
      int var2;
      if(var3) {
        var2 = R.string.pause;
      } else {
        var2 = R.string.play;
      }

      var4.setText(var2);
    } else if(var1 != this.buttonClear && var1 != this.buttonClearLayout) {
      if(var1 != this.buttonProCloudMenu && var1 != this.proButtonLayout && var1 != this.buttonProImageCloudMenu) {
        if(var1.getId() == R.id.favorite_button_add) {
          this.favoriteFragment.addCurrentSelection(this);
        } else {
          this.onNavigationButton((Button)var1, true);
        }
      } else {
        RelaxAnalytics.logUpgradeFromCloudMenu();
        NavigationHelper.showSubscription(this);
      }
    } else {
      RelaxAnalytics.logClearAllSounds();
      this.clearSelection();
    }
  }

  protected void onCreate(Bundle var1) {
    super.onCreate(var1);
    isActivityDestroyed = false;
    SoundManager.getInstance().configureSoundManager(this);
    SoundManager.getInstance().registerObserver(this);
    GuidedMeditationStatus.getInstance().configure();
    GuidedMeditationStatus.getInstance().registerObserver(this);
    RelaxMelodiesApp.getInstance().registerAppDelegateObserver(this);
    SubscriptionOfferResolver.registerListener(this);
    this.updateCurrentLoopMode();
    this.setContentView(R.layout.fragments);
    this.setup(var1);
  }

  protected void onDestroy() {
    isActivityDestroyed = true;
    RelaxMelodiesApp.getInstance().unregisterAppDelegateObserver(this);
    if(this.timerTask != null) {
      Log.i("TimerTask", "Cancelling timer");
      this.timerTask.cancel(true);
    }

    GuidedMeditationStatus.getInstance().unregisterObserver(this);
    SubscriptionOfferResolver.removeListener(this);
    super.onDestroy();
  }

  public boolean onDoubleButtonTap(SoundButton var1) {
    return this.onSingleButtonTap(var1);
  }

  public void onEnterBackground() {
    this.hasEnteredForeground = false;
    this.resetSessionFlags();
  }

  public void onEnterForeground() {
    this.onEnterForegroundOrScreenUnlockedCalledOnce = true;
    this.hasEnteredForeground = true;
    GuidedMeditationStatus.getInstance().configure();
    GuidedMeditationStatus.getInstance().resetShowcasedMeditationScroll();
    this.handlePopups();
  }

  public void onFeatureDownloaded(InAppPurchase var1, String[] var2) {
    if(this.favoriteFragment != null) {
      this.favoriteFragment.updateData();
    }

  }

  public void onFeatureManagerSetupFinished() {
  }

  public void onFeatureUnlocked(InAppPurchase var1, String var2) {
  }

  public boolean onKeyDown(int var1, KeyEvent var2) {
    switch(var1) {
      case 24:
        this.globalVolumeManager.updateVolumeUp();
        return true;
      case 25:
        this.globalVolumeManager.updateVolumeDown();
        return true;
      default:
        return super.onKeyDown(var1, var2);
    }
  }

  public boolean onLongPress(SoundButton var1) {
    return this.onSingleButtonTap(var1);
  }

  void onNavigationButton(Button var1, boolean var2) {
    Fragment var3 = null;
    if((this.currentNavigationButton == null || var1.getId() != this.currentNavigationButton.getId()) && this.safeToCommitTransactions) {
      this.setCurrentNavigationButton(var1);
      if(this.applicationReady) {
        this.navigationTabBarHandler.moveTabIndicatorToTabButton(var1, var2);
      }

      if(var1.getId() == R.id.button_nav_home) {
        this.navigateHome();
        this.showSounds();
        return;
      }

      if(var1.getId() == R.id.button_nav_guided_meditation) {
        var3 = this.navigateGuidedMeditation();
        SoundVolumeManager.getInstance().stopVolumeBarFadeInAnimationIfNeeded();
        GuidedMeditationStatus.getInstance().userNavigatedToMeditationTab();
      } else if(var1.getId() == R.id.button_nav_timer) {
        var3 = this.navigateTimer();
        SoundVolumeManager.getInstance().stopVolumeBarFadeInAnimationIfNeeded();
      } else if(var1.getId() == R.id.button_nav_favorite) {
        var3 = this.navigateFavorites();
        SoundVolumeManager.getInstance().stopVolumeBarFadeInAnimationIfNeeded();
      } else if(var1.getId() == R.id.button_nav_more) {
        var3 = this.navigateMore();
        SoundVolumeManager.getInstance().stopVolumeBarFadeInAnimationIfNeeded();
      }

      this.hideSounds(var2);
      if(var3 != null) {
        this.fragmentSwitcher.switchFragment(var3, var2);
        return;
      }
    }

  }

  protected void onNewIntent(Intent var1) {
  }

  protected void onPause() {
    super.onPause();
    this.isActivityRunning = false;
    if(!this.wasShowingTutorial) {
      RelaxMelodiesApp.getInstance().saveSelectedSounds();
    }

    if(this.getRelaxScrollView() != null) {
      this.getRelaxScrollView().pauseAnimations();
    }

  }

  public void onPausedAllSounds() {
    this.refreshUIForSelection();
    if(this.favoriteFragment != null) {
      this.favoriteFragment.updateData();
    }

  }

  protected void onPostResume() {
    super.onPostResume();
    this.safeToCommitTransactions = true;
    if(this.currentNavigationButton == null) {
      this.restoreWithCurrentNavigationFragment(this.activeNavButton, true);
    }

    this.forceRefreshOfScrollViewLayout();
    if(this.onPostResumeBringToGuidedMeditationScreen) {
      this.restoreWithCurrentNavigationFragment(1, false);
      this.onPostResumeBringToGuidedMeditationScreen = false;
    }

  }

  public void onPurchaseCompleted(InAppPurchase var1, Purchase var2, Date var3) {
  }

  public void onPurchasesAvailable(List<InAppPurchase> var1) {
    if(this.getRelaxScrollView() != null && !this.getRelaxScrollView().isAllowedLockedButtonClick()) {
      this.getRelaxScrollView().setAllowedLockedButtonClick(true);
    }

    Iterator var2 = var1.iterator();

    while(var2.hasNext()) {
      this.flagSeenMeditationIfRelevant((InAppPurchase)var2.next());
    }

  }

  protected void onResume() {
    super.onResume();
    this.isActivityRunning = true;
    this.uiIsReady = true;
    ReviewProcess.getInstance().configure(this, this.getRelaxScrollView());
    this.updateCurrentLoopMode();
    this.setupTimerTaskAndListener();
    FeatureManager.getInstance().loadActiveSubscription();
    this.globalVolumeManager.updateSeekBar();
    if(this.bufferTimeForApiCallsHasPassed() || RelaxMelodiesApp.getInstance().getNewsService() == null) {
      RelaxMelodiesApp.getInstance().retrieveNewsCounter(this);
    }

    this.setNumberOfNewNews(RelaxMelodiesApp.getInstance().getNewsService().unreadCount());
    if(!this.shouldShowTutorial() || this.wasShowingTutorial) {
      this.logScreenForCurrentFragment();
      this.wasShowingTutorial = false;
      this.getRelaxScrollView().setVisibility(View.VISIBLE);
    }

    this.relaxScrollViewGraphicsAnimator.setBottomMenuScrollView((DisableTouchScrollView)this.bottomMenu.findViewById(R.id.bottom_menu_scroll_view));
    if(this.getRelaxScrollView() != null) {
      this.getRelaxScrollView().resumeAnimations();
    }

  }

  public void onResumedAllSounds() {
    this.refreshUIForSelection();
    if(this.favoriteFragment != null) {
      this.favoriteFragment.updateData();
    }

  }

  public void onSaveInstanceState(Bundle var1) {
    var1.putBoolean("resumeOnAudioFocus", this.resumeOnAudioFocus);
    var1.putInt("activeNavButton", this.activeNavButton);
    super.onSaveInstanceState(var1);
    this.safeToCommitTransactions = false;
  }

  public void onScreenLocked() {
    this.hasEnteredForeground = false;
    this.resetSessionFlags();
  }

  public void onScreenUnlocked() {
    this.onEnterForegroundOrScreenUnlockedCalledOnce = true;
    this.hasEnteredForeground = true;
    GuidedMeditationStatus.getInstance().configure();
    this.forceRefreshOfScrollViewLayout();
    this.handlePopups();
  }

  public void onScrollChanged(int var1, int var2, int var3, int var4) {
    this.relaxScrollViewGraphicsAnimator.handleScroll(var1, this.scrollContent.getMeasuredWidth());
  }

  public void onSelectionChanged(List<Sound> var1, List<Sound> var2) {
    this.refreshUIForSelection();
    this.resetFavoriteAndStaffPickSelection();
  }

  public boolean onSingleButtonTap(SoundButton var1) {
    if(!FeatureManager.getInstance().hasActiveSubscription() && var1.isLocked()) {
      if(var1.getSound() instanceof BinauralSound) {
        RelaxAnalytics.logUpgradeFromBinaurals();
      } else if(var1.getSound() instanceof IsochronicSound) {
        RelaxAnalytics.logUpgradeFromIsochronics();
      } else {
        RelaxAnalytics.logUpgradeFromSounds();
      }

      NavigationHelper.showSubscription(this);
      return true;
    } else {
      return false;
    }
  }

  public void onSoundManagerException(String var1, Exception var2) {
    if(this.isMainActivityAvailable() && this.isActivityRunning) {
      if(!(var2 instanceof SoundLimitReachedException)) {
        Utils.uniqueAlert(this, "Relax Melodies", var2.getMessage());
        return;
      }

      Utils.uniqueAlert(this, this.getString(R.string.main_activity_too_many_sounds_title), this.getString(R.string.main_activity_too_many_sounds));
    }

  }

  public void onSoundPlayed(Sound var1) {
    this.refreshUIForSelection();
    this.resetFavoriteAndStaffPickSelection();
  }

  public void onSoundStopped(Sound var1, float var2) {
    this.refreshUIForSelection();
    this.logMeditationCompletionIfNecessary(var1.getId(), SoundState.STOPPED, var2);
    this.resetFavoriteAndStaffPickSelection();
  }

  public void onSoundVolumeChange(String var1, float var2) {
    if(SoundManager.getInstance().isSelected(var1) && !SoundManager.getInstance().isSoundAGuidedMeditation(var1)) {
      this.resetFavoriteAndStaffPickSelection();
    }

  }

  public void onSubscriptionChanged(Subscription var1, boolean var2) {
    if(var1 != null) {
      if(!var2) {
        RelaxAnalytics.logSubscriptionDeactivated(var1.getIdentifier());
        return;
      }

      RelaxAnalytics.addPurchaseInfo(var1.getPurchaseToken(), var1.getOrderId());
      RelaxAnalytics.logSubscriptionActivated(var1.getIdentifier());
    }

  }

  public void onTimerComplete(boolean var1) {
    this.timerContainer.setVisibility(View.GONE);
  }

  public void onTimerUpdate(String var1) {
    if(var1 != null && !var1.isEmpty()) {
      if(this.timerContainer.getVisibility() == View.GONE && this.currentNavigationButton == this.getHomeButton()) {
        this.timerContainer.setVisibility(View.VISIBLE);
      }

      this.timerLabel.setText(var1);
    } else {
      this.timerContainer.setVisibility(View.GONE);
    }
  }

  protected void reinitServices() {
    if(!RelaxMelodiesApp.areServiceInitialized) {
      RelaxMelodiesApp.areServiceInitialized = true;
      RelaxAnalytics.initialize(this.getApplicationContext());
      RelaxFeatureManager.configureRelaxFeatureManager(this, new RelaxFeatureManagerCallback());
      FeatureManager.getInstance().fetchAvailableFeatures();
      this.reloadSoundLibrary();
    }

  }

  protected void reloadSoundLibrary() {
    SoundLibrary.getInstance().configureSoundLibrary(this.getApplicationContext());
  }

  void selectHomeNavigationButton() {
    if(this.currentNavigationButton != null) {
      this.currentNavigationButton.setSelected(false);
    }

    this.currentNavigationButton = (Button)this.getHomeButton();
    this.currentNavigationButton.setSelected(true);
    this.activeNavButton = 0;
  }

  void setCurrentNavigationButton(Button var1) {
    if(this.currentNavigationButton != null) {
      this.currentNavigationButton.setSelected(false);
    }

    var1.setSelected(true);
    this.currentNavigationButton = var1;
  }

  public boolean shouldShootStar() {
    return this.isActivityRunning;
  }

  void showProButton() {
    if(this.proButtonLayout.getVisibility() != View.VISIBLE) {
      this.proButtonLayout.setVisibility(View.VISIBLE);
      int var1 = this.proButtonLayout.getMeasuredWidth();
      LayoutParams var2 = (LayoutParams)this.buttonClearLayout.getLayoutParams();
      var2.addRule(11);
      var2.setMargins(var2.leftMargin, var2.topMargin, this.proButtonRightMargin + var1, var2.bottomMargin);
      this.buttonClearLayout.setLayoutParams(var2);
    }

  }

  public void showTimerLabel() {
    if(this.currentNavigationButton != null) {
      boolean var1;
      if(this.currentNavigationButton.getId() != R.id.button_nav_home && (this.currentNavigationButton.getId() != id.button_nav_guided_meditation || !SoundManager.getInstance().hasGuidedMeditationSelected())) {
        var1 = false;
      } else {
        var1 = true;
      }

      byte var2;
      if(var1) {
        var2 = 1;
      } else {
        var2 = 0;
      }

      this.timerTask = RelaxMelodiesApp.getInstance().getTimerTask();
      if(this.timerTask != null && !this.timerTask.isFinished() && this.timerContainer != null) {
        if(var2 == 1) {
          this.timerContainer.setVisibility(View.VISIBLE);
        }

        this.timerContainer.animate().alpha((float)var2).setDuration((long)250);
      }
    }

  }

  public void simulateHomeTabClick() {
    this.onNavigationButton((Button)this.getHomeButton(), true);
  }
}
