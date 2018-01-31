package ipnossoft.rma.upgrade;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.ScrollView;
import com.bumptech.glide.Glide;
import com.ipnossoft.api.dynamiccontent.model.InAppPurchase;
import com.ipnossoft.api.featuremanager.FeatureManager;
import com.ipnossoft.api.featuremanager.FeatureManagerObserver;
import com.ipnossoft.api.featuremanager.exceptions.BillingException;
import com.viewpagerindicator.CirclePageIndicator;
import ipnossoft.rma.DefaultServiceConnection;
import ipnossoft.rma.free.R;
import ipnossoft.rma.upgrade.Subscription.SubscriptionCallback;
import ipnossoft.rma.util.RelaxAnalytics;
import ipnossoft.rma.util.Utils;
import java.util.Date;
import java.util.List;
import org.onepf.oms.appstore.googleUtils.Purchase;
import org.onepf.oms.appstore.googleUtils.SkuDetails;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SubscriptionActivity extends AppCompatActivity implements SubscriptionCallback, FeatureManagerObserver, OnPageChangeListener {
  public static final int CAROUSEL_AUTO_CHANGE_DELAY = 3000;
  public static final int CAROUSEL_RESTART_AUTO_CHANGE_DELAY = 8000;
  public static final int INT = 1;
  private static final int LAYOUT_CAROUSEL_WITH_DURATION_BUTTONS = 3;
  private static final int LAYOUT_CAROUSEL_WITH_PRICE_PER_MONTH_BUTTONS = 2;
  private static final int LAYOUT_CAROUSEL_WITH_SLASHED_PRICE_BUTTONS = 1;
  private static final int LAYOUT_CAROUSEL_WITH_TEXTUAL_BUTTONS = 4;
  private static final int LAYOUT_LIST_WITH_MEDITATING_WOMAN = 7;
  private static final int LAYOUT_LIST_WITH_RED_BLUE_YELLOW_BUTTONS = 6;
  private static final int LAYOUT_LIST_WITH_RED_YELLOW_GREEN_BUTTONS = 5;
  private static final String TAG = "SubscriptionActivity";
  private static final String TRANSLATION_Y = "translationY";
  public static final String UPGRADE_FROM_NOTIFICATION = "upgrade_from_notification";
  private boolean alreadyLoggedSubscriptionSuccessAnalytics;
  private SubscriptionAnimatedImages animatedImages;
  private View buttonLayout;
  private Runnable carouselChanger;
  private Handler carouselRepeater;
  private boolean firstOnPostResume = true;
  private boolean firstOnResume = true;
  private int inProgressSubscriptionTier = 0;
  private ScrollView mountainScrollView;
  private float mountainWidthInPixels;
  private CirclePageIndicator pageIndicator;
  private int previousViewPagerState;
  private final DefaultServiceConnection soundPlayerService = new DefaultServiceConnection();
  private Subscription subscriptionBuilder;
  private SubscriptionViewPagerAdapter viewPagerAdapter;
  private boolean wasPageChangedAutomatically = false;

  public SubscriptionActivity() {
  }

  private View buildButtonsLayout() {
    ViewStub var1 = (ViewStub)this.findViewById(R.id.subscribe_tier_buttons_layout);
    switch(SubscriptionOfferResolver.getLayout()) {
      case 1:
        var1.setLayoutResource(R.layout.subscribe_buttons_slashed_price);
        break;
      case 2:
        var1.setLayoutResource(R.layout.subscribe_buttons_price_per_month);
        break;
      case 3:
        var1.setLayoutResource(R.layout.subscribe_buttons_duration);
        break;
      default:
        var1.setLayoutResource(R.layout.subscribe_buttons_textual);
    }

    return var1.inflate();
  }

  private void setupParallax() {
    this.mountainWidthInPixels = this.getResources().getDimension(R.dimen.parallax_mountain_width);
    this.mountainScrollView = (ScrollView)this.findViewById(R.id.subscription_mountains_overlay_scroll_view);
  }

  private void setupViewPager() {
    ViewPager var1 = (ViewPager)this.findViewById(R.id.subscription_view_pager);
    this.viewPagerAdapter = new SubscriptionViewPagerAdapter(this.getSupportFragmentManager(), this);
    if(var1 != null) {
      var1.setAdapter(this.viewPagerAdapter);
      var1.addOnPageChangeListener(this);
      this.pageIndicator = (CirclePageIndicator)this.findViewById(R.id.subscription_page_indicator);
      if(this.pageIndicator != null && this.viewPagerAdapter.getCount() > 1) {
        this.pageIndicator.setViewPager(var1);
      }
    }

    this.animatedImages = (SubscriptionAnimatedImages)this.findViewById(R.id.subscription_animated_images);
    this.carouselRepeater = new Handler();
    this.startAutomaticallyChangingPages();
  }

  protected void attachBaseContext(Context var1) {
    super.attachBaseContext(CalligraphyContextWrapper.wrap(var1));
  }

  protected void onCreate(Bundle var1) {
    boolean var2 = false;
    super.onCreate(var1);
    int var3 = SubscriptionOfferResolver.getLayout();
    if(var3 <= 4) {
      this.setContentView(R.layout.subscribe);
      this.subscriptionBuilder = new SubscriptionBuilder(this.findViewById(R.id.subscribe_root_view), this, this);
    } else if(var3 != 5 && var3 != 6) {
      this.setContentView(R.layout.subscribe_meditating_woman);
      this.subscriptionBuilder = new SubscriptionUniversalBuilder(this.findViewById(R.id.subscribe_root_view), this, this, false);
    } else {
      this.setContentView(R.layout.subscribe_feature_list);
      View var4 = this.findViewById(R.id.subscribe_root_view);
      if(var3 == 6) {
        var2 = true;
      }

      this.subscriptionBuilder = new SubscriptionUniversalBuilder(var4, this, this, var2);
    }

    if(var3 != 7) {
      ImageView var5 = (ImageView)this.findViewById(R.id.subscription_mountains_overlay_image_view);
      Glide.with(this).load(Integer.valueOf(R.drawable.second_level_parallax_mountains)).placeholder(R.drawable.second_level_parallax_mountains).dontAnimate().into(var5);
      var5 = (ImageView)this.findViewById(R.id.subscription_stars_background);
      Glide.with(this).load(Integer.valueOf(R.drawable.bg_main)).centerCrop().into(var5);
    }

    if(var3 <= 4) {
      this.buttonLayout = this.buildButtonsLayout();
      this.setupParallax();
      this.setupViewPager();
    }

    this.subscriptionBuilder.build();
    if(this.getSupportActionBar() != null) {
      this.getSupportActionBar().setTitle(R.string.activity_title_upgrade);
      this.getSupportActionBar().setHomeButtonEnabled(true);
      this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    var1 = this.getIntent().getExtras();
    if(var1 != null && var1.getBoolean("upgrade_from_notification")) {
      RelaxAnalytics.logUpgradeFromNotification();
    }

    RelaxAnalytics.logScreenUpgrade();
  }

  public void onFeatureDownloaded(InAppPurchase var1, String[] var2) {
  }

  public void onFeatureManagerSetupFinished() {
  }

  public void onFeatureUnlocked(InAppPurchase var1, String var2) {
  }

  public boolean onOptionsItemSelected(MenuItem var1) {
    if(var1.getItemId() == 16908332) {
      this.finish();
      return true;
    } else {
      return super.onOptionsItemSelected(var1);
    }
  }

  public void onPageScrollStateChanged(int var1) {
    if(var1 == 1) {
      this.pauseAutomaticallyChangingPages();
    }

    this.previousViewPagerState = var1;
  }

  public void onPageScrolled(int var1, float var2, int var3) {
    double var4 = (double)(((float)var1 + var2) / (float)this.viewPagerAdapter.getCount());
    this.mountainScrollView.scrollTo((int)((double)(this.mountainWidthInPixels - (float)this.mountainScrollView.getMeasuredWidth()) * var4), 0);
  }

  public void onPageSelected(int var1) {
    this.animatedImages.setCurrentImage(var1);
  }

  protected void onPostResume() {
    super.onPostResume();
    if(this.firstOnPostResume && this.animatedImages != null) {
      this.animatedImages.initializeAnimations();
    }

    this.firstOnPostResume = false;
  }

  public void onPurchaseCompleted(InAppPurchase var1, Purchase var2, Date var3) {
  }

  public void onPurchasesAvailable(List<InAppPurchase> var1) {
  }

  public void onRestorePurchaseClick(View var1) {
    this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("http://support.ipnossoft.com/hc/en-us/articles/214394208")));
  }

  protected void onResume() {
    super.onResume();
    if(!this.firstOnResume) {
      FeatureManager.getInstance().queryInventory();
      FeatureManager.getInstance().registerObserver(this);
    }

    this.firstOnResume = false;
  }

  protected void onStart() {
    super.onStart();
    this.soundPlayerService.connect(this);
  }

  protected void onStop() {
    this.stopAutomaticallyChangingPages();
    this.soundPlayerService.disconnect(this);
    FeatureManager.getInstance().unregisterObserver(this);
    super.onStop();
  }

  public void onSubscriptionCancel() {
    this.finish();
  }

  public void onSubscriptionChanged(com.ipnossoft.api.featuremanager.data.Subscription var1, boolean var2) {
    if(var2) {
      if(!this.alreadyLoggedSubscriptionSuccessAnalytics) {
        this.alreadyLoggedSubscriptionSuccessAnalytics = true;
        SkuDetails var3 = this.subscriptionBuilder.getSkuDetailsForSubscription(var1.getIdentifier());
        RelaxAnalytics.logSubscriptionProcessSucceed(var1.getIdentifier(), this.inProgressSubscriptionTier, var3);
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
    this.inProgressSubscriptionTier = var2;
    RelaxAnalytics.logSubscriptionProcessTriggered(var1, var2);
  }

  public void onSubscriptionSuccess(String var1, int var2) {
    if(!this.alreadyLoggedSubscriptionSuccessAnalytics) {
      this.alreadyLoggedSubscriptionSuccessAnalytics = true;
      RelaxAnalytics.logSubscriptionProcessSucceed(var1, var2, this.subscriptionBuilder.getSkuDetailsForSubscription(var1));
    }

    this.finish();
  }

  public void onUnresolvedPurchases(List<String> var1) {
  }

  void pauseAutomaticallyChangingPages() {
    if(this.carouselRepeater != null) {
      this.carouselRepeater.removeCallbacks(this.carouselChanger);
      this.carouselRepeater.postDelayed(this.carouselChanger, 8000L);
    }

  }

  void startAutomaticallyChangingPages() {
    if(this.carouselRepeater != null) {
      this.carouselRepeater.postDelayed(this.carouselChanger, 3000L);
    }

  }

  void stopAutomaticallyChangingPages() {
    if(this.carouselRepeater != null) {
      this.carouselRepeater.removeCallbacks(this.carouselChanger);
    }

  }
}
