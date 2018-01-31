package ipnossoft.rma.ui.tutorial;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import ipnossoft.rma.PersistedDataManager;
import ipnossoft.rma.RelaxMelodiesApp;
import ipnossoft.rma.animation.ViewPagerTransitionAnimator;
import ipnossoft.rma.animation.ViewPagerTransitionAnimator.PageTransitionCallback;
import ipnossoft.rma.free.R;
import ipnossoft.rma.media.SoundManager;
import ipnossoft.rma.ui.scrollview.DisableTouchScrollView;
import ipnossoft.rma.util.RelaxAnalytics;
import ipnossoft.rma.util.Utils;
import java.util.Locale;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class TutorialActivity extends AppCompatActivity implements OnPageChangeListener, PageTransitionCallback {
  private static final String ETERNITY_SOUND_ID = "ipnossoft.rma.sounds.sound113";
  private static final String INTRO_MEDITATION_SOUND_ID = "ipnossoft.rma.guided.introduction";
  public static final String NIGHT_SOUND_ID = "ipnossoft.rma.sounds.sound16";
  private static final String RAIN_SOUND_ID = "ipnossoft.rma.sounds.sound4";
  public static final int THIRTY_MINUTES_IN_MILLISECONDS = 1800000;
  public static String TUTORIAL_EXTRA_FORCED = "tutorialExtraForced";
  private boolean isPlayingSound;
  public boolean isSwipeEnabled = false;
  private AnimatorSet lastSlideButtonAnimation;
  private DisableTouchScrollView mountainScrollView;
  private float mountainWidth;
  private Animator nextButtonDisableAnimation;
  private Animator nextButtonEnableAnimation;
  private int previousPosition = 0;
  private DisableTouchScrollView starsScrollView;
  private float starsWidth;
  private TextView subSubTitleTextView;
  private TextView subTitleTextView;
  private TextView titleTextView;
  private TutorialAnimationFactory tutorialAnimationFactory;
  private TutorialPageAdapter tutorialPageAdapter;
  private TutorialViewPager viewPager;
  private ViewPagerTransitionAnimator viewPagerTransitionAnimator;
  private boolean wasTutorialForced = false;

  public TutorialActivity() {
  }

  private void enableNextButtonAfterDelay() {
    this.isSwipeEnabled = false;
    (new Handler()).postDelayed(new Runnable() {
      public void run() {
        TutorialActivity.this.nextButtonEnableAnimation.start();
        TutorialActivity.this.isSwipeEnabled = true;
      }
    }, 2000L);
  }

  private void endTutorial(boolean var1) {
    PersistedDataManager.saveBoolean("did_show_tutorial", true, this);
    PersistedDataManager.saveBoolean("is_showing_tutorial", false, this);
    this.overridePendingTransition(0, R.anim.abc_fade_out);
    if(var1) {
      this.setResult(-1);
    } else {
      this.setResult(0);
    }

    this.finish();
  }

  private void playAppropriateSoundForPage(int var1) {
    switch(var1) {
      case 1:
        this.playNightSound();
        return;
      case 2:
        this.playRainSound();
        return;
      case 3:
        this.playEternitySound();
        return;
      default:
    }
  }

  private void playEternitySound() {
    SoundManager.getInstance().play("ipnossoft.rma.sounds.sound113", 0.63F, this, false);
  }

  private void playNightSound() {
    this.isPlayingSound = true;
    SoundManager.getInstance().clearAll();
    SoundManager.getInstance().play("ipnossoft.rma.sounds.sound16", 0.06F, this, false);
  }

  private void playRainSound() {
    SoundManager.getInstance().play("ipnossoft.rma.sounds.sound4", 0.24F, this, false);
  }

  private void setupAllPageAnimations() {
    this.tutorialAnimationFactory = new TutorialAnimationFactory(this);
    this.viewPagerTransitionAnimator.addPageAnimation(0, this.tutorialAnimationFactory.createSlide0Animations());
    this.viewPagerTransitionAnimator.addPageAnimation(1, this.tutorialAnimationFactory.createSlide1Animations());
    this.viewPagerTransitionAnimator.addPageAnimation(2, this.tutorialAnimationFactory.createSlide2Animations());
    this.viewPagerTransitionAnimator.addPageAnimation(3, this.tutorialAnimationFactory.createSlide3Animations());
    this.viewPagerTransitionAnimator.addPageAnimation(4, this.tutorialAnimationFactory.createSlide4Animations());
  }

  private void setupButtonAnimations() {
    this.nextButtonDisableAnimation = this.tutorialAnimationFactory.createNextButtonDisableAnimation();
    this.nextButtonEnableAnimation = this.tutorialAnimationFactory.createNextButtonEnableAnimation();
    this.lastSlideButtonAnimation = this.tutorialAnimationFactory.createLastSlideButtonAnimations();
  }

  private void setupPageButtons(int var1) {
    switch(var1) {
      case 1:
        this.nextButtonDisableAnimation.start();
        this.enableNextButtonAfterDelay();
        return;
      case 2:
        this.nextButtonDisableAnimation.start();
        this.enableNextButtonAfterDelay();
        return;
      case 3:
        this.nextButtonDisableAnimation.start();
        this.enableNextButtonAfterDelay();
        return;
      case 4:
        this.lastSlideButtonAnimation.start();
        return;
      default:
    }
  }

  private void setupTextFragment(int var1) {
    switch(var1) {
      case 0:
        this.subTitleTextView.setText(this.getString(R.string.tutorial_slide0_sub_title));
        this.titleTextView.setText(this.getString(R.string.tutorial_slide0_title));
        return;
      case 1:
        this.subTitleTextView.setText(this.getString(R.string.tutorial_slide1_sub_title));
        this.titleTextView.setText(this.getString(R.string.tutorial_slide1_title));
        return;
      case 2:
        this.subTitleTextView.setText(this.getString(R.string.tutorial_slide2_sub_title));
        this.titleTextView.setText(this.getString(R.string.tutorial_slide2_title));
        return;
      case 3:
        this.subTitleTextView.setText(this.getString(R.string.tutorial_slide3_sub_title));
        this.titleTextView.setText(this.getString(R.string.tutorial_slide3_title));
        return;
      case 4:
        if(!Utils.getCurrentLanguageLocale(RelaxMelodiesApp.getInstance().getApplicationContext()).equals(Locale.ENGLISH.getLanguage())) {
          this.subTitleTextView.setText(this.getString(R.string.tutorial_slide4_sub_title_non_english));
          this.titleTextView.setText(this.getString(R.string.tutorial_slide4_title_non_english));
          return;
        }

        this.subTitleTextView.setText(this.getString(R.string.tutorial_slide4_sub_title));
        this.titleTextView.setText(this.getString(R.string.tutorial_slide4_title));
        this.subSubTitleTextView.setText(this.getString(R.string.tutorial_slide4_sub_sub_title));
        return;
      default:
    }
  }

  private void startIntroMeditation() {
    SoundManager.getInstance().play("ipnossoft.rma.guided.introduction", 0.33F, this, false);
  }

  protected void attachBaseContext(Context var1) {
    super.attachBaseContext(CalligraphyContextWrapper.wrap(var1));
  }

  public void onBackPressed() {
    if(this.wasTutorialForced) {
      PersistedDataManager.saveBoolean("is_showing_tutorial", false, this);
      super.onBackPressed();
    }

  }

  public void onButtonClick(View var1) {
    if((float)Math.round(var1.getAlpha() * 100.0F) == 100.0F && this.viewPager.getCurrentItem() < this.tutorialPageAdapter.getCount() - 1) {
      this.viewPager.setCurrentItem(this.viewPager.getCurrentItem() + 1);
    }
  }

  protected void onCreate(Bundle var1) {
    boolean var2 = true;
    super.onCreate(var1);
    this.setContentView(R.layout.tutorial);
    PersistedDataManager.saveBoolean("is_showing_tutorial", true, this);
    this.viewPagerTransitionAnimator = new ViewPagerTransitionAnimator(this);
    var1 = this.getIntent().getExtras();
    if(var1 == null || !var1.getBoolean(TUTORIAL_EXTRA_FORCED)) {
      var2 = false;
    }

    this.wasTutorialForced = var2;
    this.viewPager = (TutorialViewPager)this.findViewById(R.id.tutorial_view_pager);
    this.tutorialPageAdapter = new TutorialPageAdapter(this.getSupportFragmentManager());
    this.viewPager.setTutorialPageAdapter(this.tutorialPageAdapter);
    this.viewPager.addOnPageChangeListener(this);
    this.mountainScrollView = (DisableTouchScrollView)this.findViewById(R.id.tutorial_seperator_image_scroll_view);
    this.starsScrollView = (DisableTouchScrollView)this.findViewById(R.id.background_stars_scroll_view);
    this.titleTextView = (TextView)this.findViewById(R.id.tutorial_title);
    this.subTitleTextView = (TextView)this.findViewById(R.id.tutorial_sub_title);
    this.subSubTitleTextView = (TextView)this.findViewById(R.id.tutorial_sub_sub_title);
    ImageView var3 = (ImageView)this.findViewById(R.id.walkthrough_front_mountains_image_view);
    this.mountainWidth = this.getResources().getDimension(R.dimen.parallax_mountain_width);
    Glide.with(this).load(Integer.valueOf(R.drawable.second_level_parallax_mountains)).placeholder(R.drawable.second_level_parallax_mountains).dontAnimate().into(var3);
    var3 = (ImageView)this.findViewById(R.id.walkthrough_light_image_view);
    Glide.with(this).load(Integer.valueOf(R.drawable.bg_moon)).into(var3);
    var3 = (ImageView)this.findViewById(R.id.background_stars_image_view);
    this.starsWidth = this.getResources().getDimension(R.dimen.tutorial_star_background_width);
    Glide.with(this).load(Integer.valueOf(R.drawable.bg_main)).into(var3);
    RelaxAnalytics.logWalkthroughPageShownAtIndex(0);
    RelaxAnalytics.logScreenWalkthroughAtIndex(0);
    this.setupAllPageAnimations();
    this.setupButtonAnimations();
    this.viewPagerTransitionAnimator.showFirstPage();
    this.enableNextButtonAfterDelay();
  }

  public void onCreateYourAmbianceClicked(View var1) {
    if(var1.getAlpha() > 0.0F) {
      SoundManager.getInstance().clearAll();
      this.endTutorial(false);
    }

  }

  public void onListenNowClicked(View var1) {
    if(var1.getAlpha() > 0.0F) {
      if(Utils.getCurrentLanguageLocale(RelaxMelodiesApp.getInstance().getApplicationContext()).equals(Locale.ENGLISH.getLanguage())) {
        this.startIntroMeditation();
      }

      RelaxMelodiesApp.getInstance().startTimer(this, 1800000L, false);
      this.endTutorial(true);
    }

  }

  public void onPageScrollStateChanged(int var1) {
  }

  public void onPageScrolled(int var1, float var2, int var3) {
    double var4 = (double)(((float)var1 + var2) / (float)this.viewPager.getAdapter().getCount());
    var2 = (float)this.mountainScrollView.getMeasuredWidth();
    this.mountainScrollView.scrollTo((int)((double)(this.mountainWidth - var2) * var4), 0);
    this.starsScrollView.scrollTo((int)((double)(this.starsWidth - var2) * var4), 0);
  }

  public void onPageSelected(int var1) {
    RelaxAnalytics.logWalkthroughPageShownAtIndex(var1);
    RelaxAnalytics.logScreenWalkthroughAtIndex(var1);
    this.viewPagerTransitionAnimator.pageChanged(this.previousPosition, var1);
    this.previousPosition = var1;
    this.setupPageButtons(var1);
  }

  protected void onResume() {
    super.onResume();
    if(this.isPlayingSound && SoundManager.getInstance().isPaused()) {
      SoundManager.getInstance().playAll();
    }

  }

  public void pageTransitionStarted(int var1) {
    this.setupTextFragment(var1);
    this.playAppropriateSoundForPage(var1);
  }
}
