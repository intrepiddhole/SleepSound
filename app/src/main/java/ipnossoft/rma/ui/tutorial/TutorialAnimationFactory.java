package ipnossoft.rma.ui.tutorial;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.Animator.AnimatorListener;
import android.app.Activity;
import android.graphics.Point;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.view.Display;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import ipnossoft.rma.RelaxMelodiesApp;
import ipnossoft.rma.animation.ElasticInterpolator;
import ipnossoft.rma.animation.ViewPagerInOutTransitionAnimations;
import ipnossoft.rma.free.R;
import ipnossoft.rma.ui.button.RoundBorderedButton;
import ipnossoft.rma.util.Utils;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TutorialAnimationFactory {
  private static final int BUTTONS_FADE_IN_FADE_OUT_DURATION = 2000;
  private static final int DARKEN_BACKGROUND_ANIMATION_DURATION = 1500;
  public static final int DELAY_BEFORE_TRANSITION_NEXT_BUTTON_ENABLED = 2000;
  private static final int ELASTIC_ANIMATION_DURATION = 2400;
  private static double ELASTIC_ANIMATION_PERIOD = 0.6D;
  private static final int FADE_IN_ANIMATION_DURATION = 1000;
  public static final float IMAGE_DROP_ELASTIC_ROPE_BUFFER_RATIO = 0.15F;
  private static final int LIGHT_RISE_ANIMATION_DURATION = 2000;
  private static final int NEXT_BUTTON_ENABLE_DISABLE_ANIMATION_DURATION = 300;
  private static final int SLIDE_0_FADE_OUT_ANIMATION_DURATION = 800;
  private static final int SOUND_IMAGE_TRANSLATION_X_DURATION = 1000;
  private static final int SOUND_STACKING_ANIMATION_DURATION = 3500;
  private static final int TITLES_FADE_OUT_ANIMATION_DURATION = 800;
  private Activity activity;
  private RoundBorderedButton createYourAmbianceButton;
  private ImageView darkenBackgroundImageView;
  private float elasticBounceDistance;
  private ImageView eternityImageView;
  private ImageView lightImageView;
  private RoundBorderedButton listenNowButton;
  private RelativeLayout nextButton;
  private ImageView nightImageView;
  private TextView plusSignTextViewLeft;
  private TextView plusSignTextViewRight;
  private int plusSignTextViewWidth = 0;
  private ImageView rainImageView;
  private int screenWidth = 0;
  private int soundImageHeight = 0;
  private int soundImageWidth = 0;
  private TextView subSubTitleTextView;
  private TextView subTitleTextView;
  private TextView titleTextView;
  private float titlesElasticBounceDistance;
  private RelativeLayout tutorialContentSlide0;
  private TutorialRainHandler tutorialRainHandler;

  public TutorialAnimationFactory(Activity var1) {
    this.activity = var1;
    this.init();
  }

  @NonNull
  private AnimatorListener createAnimationListenerForVisibility(final View... var1) {
    return new AnimatorListener() {
      public void onAnimationCancel(Animator var1x) {
      }

      public void onAnimationEnd(Animator var1x) {
      }

      public void onAnimationRepeat(Animator var1x) {
      }

      public void onAnimationStart(Animator var1x) {
        for(int var2 = 0; var2 < var1.length; ++var2) {
          var1[var2].setVisibility(View.VISIBLE);
        }

      }
    };
  }

  @NonNull
  private Animator createElasticHorizontalTranslateAnimationForImageView(View var1, int var2, int var3, int var4) {
    ObjectAnimator var5 = ObjectAnimator.ofFloat(var1, "translationY", new float[]{(float)var3});
    var5.setInterpolator(new ElasticInterpolator(ELASTIC_ANIMATION_PERIOD));
    var5.setDuration((long)var4);
    ObjectAnimator var6 = ObjectAnimator.ofFloat(var1, "translationX", new float[]{(float)var2});
    var6.setInterpolator(new ElasticInterpolator(ELASTIC_ANIMATION_PERIOD));
    var6.setDuration((long)var4);
    AnimatorSet var7 = new AnimatorSet();
    var7.playTogether(new Animator[]{var5, var6});
    var7.addListener(this.createAnimationListenerForVisibility(new View[]{var1}));
    return var7;
  }

  private void createLastStepButtonAnimations(ObjectAnimator var1, AnimatorSet var2) {
    ObjectAnimator var3 = ObjectAnimator.ofFloat(this.listenNowButton, "alpha", new float[]{0.0F, 1.0F});
    var3.setDuration(2000L);
    ObjectAnimator var4 = ObjectAnimator.ofFloat(this.createYourAmbianceButton, "alpha", new float[]{0.0F, 1.0F});
    var4.setDuration(2000L);
    AnimatorSet var5 = new AnimatorSet();
    var5.setStartDelay(2000L);
    var5.playTogether(new Animator[]{var3, var4});
    var5.addListener(new AnimatorListener() {
      public void onAnimationCancel(Animator var1) {
      }

      public void onAnimationEnd(Animator var1) {
      }

      public void onAnimationRepeat(Animator var1) {
      }

      public void onAnimationStart(Animator var1) {
        TutorialAnimationFactory.this.listenNowButton.setVisibility(View.VISIBLE);
        TutorialAnimationFactory.this.listenNowButton.setAlpha(0.0F);
        TutorialAnimationFactory.this.createYourAmbianceButton.setVisibility(View.VISIBLE);
        TutorialAnimationFactory.this.createYourAmbianceButton.setAlpha(0.0F);
        TutorialAnimationFactory.this.listenNowButton.setEnabled(true);
        TutorialAnimationFactory.this.listenNowButton.setClickable(true);
        TutorialAnimationFactory.this.createYourAmbianceButton.setEnabled(true);
        TutorialAnimationFactory.this.createYourAmbianceButton.setClickable(true);
      }
    });
    var2.playSequentially(new Animator[]{var1, var5});
  }

  private ObjectAnimator createLightRiseAnimation(int var1) {
    int var2 = (int)this.activity.getResources().getDimension(R.dimen.tutorial_light_image_view_rise_animation_height);
    ObjectAnimator var3 = ObjectAnimator.ofFloat(this.lightImageView, "translationY", new float[]{0.0F, (float)(-var2)});
    var3.setDuration((long)var1);
    var3.setInterpolator(new AccelerateDecelerateInterpolator());
    var3.addListener(this.createAnimationListenerForVisibility(new View[]{this.lightImageView}));
    return var3;
  }

  @NonNull
  private ViewPagerInOutTransitionAnimations createViewPagerInOutTransition(List<Animator> var1, List<Animator> var2) {
    AnimatorSet var3 = new AnimatorSet();
    var3.playTogether(var1);
    AnimatorSet var4 = new AnimatorSet();
    var4.playTogether(var2);
    return new ViewPagerInOutTransitionAnimations(var3, var4);
  }

  @NonNull
  private ViewPagerInOutTransitionAnimations createViewPagerInTransitionOnly(List<Animator> var1) {
    AnimatorSet var2 = new AnimatorSet();
    var2.playTogether(var1);
    return new ViewPagerInOutTransitionAnimations(var2, (AnimatorSet)null);
  }

  private void findAllTutorialViews() {
    this.darkenBackgroundImageView = (ImageView)this.activity.findViewById(R.id.tutorial_dark_overlay_image_view);
    this.tutorialContentSlide0 = (RelativeLayout)this.activity.findViewById(R.id.tutorial_content_slide0);
    this.nightImageView = (ImageView)this.activity.findViewById(R.id.tutorial_night_image_view);
    this.rainImageView = (ImageView)this.activity.findViewById(R.id.tutorial_rain_cloud_image_view);
    this.eternityImageView = (ImageView)this.activity.findViewById(R.id.tutorial_eternity_image_view);
    this.titleTextView = (TextView)this.activity.findViewById(R.id.tutorial_title);
    this.subTitleTextView = (TextView)this.activity.findViewById(R.id.tutorial_sub_title);
    this.subSubTitleTextView = (TextView)this.activity.findViewById(R.id.tutorial_sub_sub_title);
    this.plusSignTextViewLeft = (TextView)this.activity.findViewById(R.id.tutorial_plus_rain);
    this.plusSignTextViewRight = (TextView)this.activity.findViewById(R.id.tutorial_plus_eternity);
    this.lightImageView = (ImageView)this.activity.findViewById(R.id.walkthrough_light_image_view);
    this.listenNowButton = (RoundBorderedButton)this.activity.findViewById(R.id.tutorial_listen_now_button);
    this.createYourAmbianceButton = (RoundBorderedButton)this.activity.findViewById(R.id.tutorial_create_your_ambiance_button);
    this.nextButton = (RelativeLayout)this.activity.findViewById(R.id.tutorial_next_button);
    this.nextButton.setEnabled(false);
    this.nextButton.setClickable(false);
  }

  private void init() {
    Display var1 = this.activity.getWindowManager().getDefaultDisplay();
    Point var2 = new Point();
    var1.getSize(var2);
    this.screenWidth = var2.x;
    this.soundImageWidth = (int)this.activity.getResources().getDimension(R.dimen.tutorial_sounds_image_width);
    this.soundImageHeight = (int)this.activity.getResources().getDimension(R.dimen.tutorial_sounds_image_height);
    this.plusSignTextViewWidth = (int)this.activity.getResources().getDimension(R.dimen.tutorial_plus_width);
    this.elasticBounceDistance = this.activity.getResources().getDimension(R.dimen.tutorial_sound_image_drop_animation_amount);
    this.titlesElasticBounceDistance = this.activity.getResources().getDimension(R.dimen.tutorial_titles_elastic_bounce_drop_distance);
    this.findAllTutorialViews();
    this.tutorialRainHandler = new TutorialRainHandler(this.activity);
  }

  private List<ObjectAnimator> slide0AnimationsIn() {
    ObjectAnimator var1 = ObjectAnimator.ofFloat(this.tutorialContentSlide0, "translationY", new float[]{this.elasticBounceDistance, 0.0F});
    var1.setInterpolator(new ElasticInterpolator(ELASTIC_ANIMATION_PERIOD));
    var1.setDuration(2400L);
    var1.addListener(this.createAnimationListenerForVisibility(new View[]{this.tutorialContentSlide0}));
    ObjectAnimator var2 = ObjectAnimator.ofFloat(this.tutorialContentSlide0, "alpha", new float[]{0.0F, 1.0F});
    var2.setDuration(1000L);
    ArrayList var3 = new ArrayList();
    var3.add(var1);
    var3.add(var2);
    return var3;
  }

  private List<ObjectAnimator> slide0AnimationsOut() {
    ObjectAnimator var1 = ObjectAnimator.ofFloat(this.tutorialContentSlide0, "alpha", new float[]{1.0F, 0.0F});
    var1.setDuration(800L);
    ArrayList var2 = new ArrayList();
    var2.add(var1);
    return var2;
  }

  private List<ObjectAnimator> slide1AnimationsIn() {
    ArrayList var2 = new ArrayList();
    ObjectAnimator var3;
    if(VERSION.SDK_INT > 16) {
      float var1 = this.activity.getResources().getDimension(R.dimen.tutorial_darken_background_translation_amount);
      var3 = ObjectAnimator.ofFloat(this.darkenBackgroundImageView, "translationY", new float[]{0.0F, var1});
      var3.setDuration(1500L);
      var2.add(var3);
    }

    var3 = ObjectAnimator.ofFloat(this.nightImageView, "translationY", new float[]{(float)(-this.soundImageHeight), (float)(-this.soundImageHeight) * 0.15F});
    var3.setInterpolator(new ElasticInterpolator(ELASTIC_ANIMATION_PERIOD));
    var3.setDuration(2400L);
    var3.addListener(this.createAnimationListenerForVisibility(new View[]{this.nightImageView}));
    int var4 = this.screenWidth / 2 - this.soundImageWidth / 2;
    var2.add(ObjectAnimator.ofFloat(this.nightImageView, "translationX", new float[]{(float)var4, (float)var4}));
    var2.add(var3);
    return var2;
  }

  private List<ObjectAnimator> slide1AnimationsOut() {
    ObjectAnimator var1 = ObjectAnimator.ofFloat(this.nightImageView, "translationX", new float[]{(float)(this.screenWidth / 2 - this.soundImageWidth / 2), (float)(this.screenWidth / 2 - this.soundImageWidth - this.plusSignTextViewWidth / 2)});
    var1.setDuration(1000L);
    var1.setInterpolator(new AccelerateDecelerateInterpolator());
    ArrayList var2 = new ArrayList();
    var2.add(var1);
    return var2;
  }

  private List<ObjectAnimator> slide2AnimationsIn() {
    ObjectAnimator var1 = ObjectAnimator.ofFloat(this.rainImageView, "translationY", new float[]{(float)(-this.soundImageHeight), (float)(-this.soundImageHeight) * 0.15F});
    var1.setInterpolator(new ElasticInterpolator(ELASTIC_ANIMATION_PERIOD));
    var1.setDuration(2400L);
    var1.addListener(new AnimatorListener() {
      public void onAnimationCancel(Animator var1) {
      }

      public void onAnimationEnd(Animator var1) {
      }

      public void onAnimationRepeat(Animator var1) {
      }

      public void onAnimationStart(Animator var1) {
        TutorialAnimationFactory.this.rainImageView.setVisibility(View.VISIBLE);
        TutorialAnimationFactory.this.plusSignTextViewLeft.setVisibility(View.VISIBLE);
        TutorialAnimationFactory.this.tutorialRainHandler.makeItRain();
      }
    });
    int var4 = this.screenWidth / 2 - this.plusSignTextViewWidth / 2;
    int var5 = var4 + this.plusSignTextViewWidth;
    this.plusSignTextViewLeft.setX((float)var4);
    this.plusSignTextViewLeft.setY(-0.15F * (float)this.soundImageHeight);
    ObjectAnimator var2 = ObjectAnimator.ofFloat(this.rainImageView, "translationX", new float[]{(float)var5, (float)var5});
    ArrayList var3 = new ArrayList();
    var3.add(var2);
    var3.add(var1);
    return var3;
  }

  private List<ObjectAnimator> slide2AnimationsOut() {
    ObjectAnimator var1 = ObjectAnimator.ofFloat(this.nightImageView, "translationX", new float[]{(float)(this.screenWidth / 2) - (float)this.soundImageWidth * 1.5F - (float)this.plusSignTextViewWidth});
    var1.setInterpolator(new AccelerateDecelerateInterpolator());
    var1.setDuration(1000L);
    ObjectAnimator var2 = ObjectAnimator.ofFloat(this.rainImageView, "translationX", new float[]{(float)(this.screenWidth / 2 - this.soundImageWidth / 2)});
    var2.setInterpolator(new AccelerateDecelerateInterpolator());
    var2.setDuration(1000L);
    ObjectAnimator var3 = ObjectAnimator.ofFloat(this.plusSignTextViewLeft, "translationX", new float[]{(float)(this.screenWidth / 2 - this.soundImageWidth / 2 - this.plusSignTextViewWidth)});
    var3.setInterpolator(new AccelerateDecelerateInterpolator());
    var3.setDuration(1000L);
    ArrayList var4 = new ArrayList();
    var4.add(var1);
    var4.add(var2);
    var4.add(var3);
    return var4;
  }

  private List<ObjectAnimator> slide3AnimationsIn() {
    ObjectAnimator var3 = ObjectAnimator.ofFloat(this.eternityImageView, "translationY", new float[]{(float)(-this.soundImageHeight), (float)(-this.soundImageHeight) * 0.15F});
    var3.setInterpolator(new ElasticInterpolator(ELASTIC_ANIMATION_PERIOD));
    var3.setDuration(2400L);
    var3.addListener(this.createAnimationListenerForVisibility(new View[]{this.eternityImageView, this.plusSignTextViewRight}));
    int var1 = this.screenWidth / 2 + this.soundImageWidth / 2;
    int var2 = var1 + this.plusSignTextViewWidth;
    this.plusSignTextViewRight.setX((float)var1);
    this.plusSignTextViewRight.setY(-0.15F * (float)this.soundImageHeight);
    ObjectAnimator var4 = ObjectAnimator.ofFloat(this.eternityImageView, "translationX", new float[]{(float)var2, (float)var2});
    ArrayList var5 = new ArrayList();
    var5.add(var4);
    var5.add(var3);
    return var5;
  }

  private List<Animator> slide3AnimationsOut() {
    ObjectAnimator var1 = ObjectAnimator.ofFloat(this.plusSignTextViewLeft, "alpha", new float[]{0.0F});
    var1.setDuration(800L);
    ObjectAnimator var2 = ObjectAnimator.ofFloat(this.plusSignTextViewRight, "alpha", new float[]{0.0F});
    var2.setDuration(800L);
    ArrayList var3 = new ArrayList();
    var3.add(var1);
    var3.add(var2);
    return var3;
  }

  private List<Animator> slide4AnimationsIn() {
    ArrayList var1 = new ArrayList();
    ObjectAnimator var2;
    if(VERSION.SDK_INT > 16) {
      var2 = ObjectAnimator.ofFloat(this.darkenBackgroundImageView, "translationY", new float[]{0.0F});
      var2.setDuration(2000L);
      var1.add(var2);
    }

    var2 = this.createLightRiseAnimation(2000);
    int var6 = this.activity.getResources().getDimensionPixelSize(R.dimen.tutorial_slide_4_sounds_offset);
    Animator var3 = this.createElasticHorizontalTranslateAnimationForImageView(this.nightImageView, (int)((double)this.screenWidth * 0.5D - (double)this.soundImageWidth * 0.5D + (double)this.soundImageWidth * 0.1D), (int)((double)this.soundImageWidth * 0.1D) + var6, 3500);
    Animator var4 = this.createElasticHorizontalTranslateAnimationForImageView(this.rainImageView, (int)((double)this.screenWidth * 0.5D - (double)this.soundImageWidth * 0.5D), var6, 3500);
    Animator var5 = this.createElasticHorizontalTranslateAnimationForImageView(this.eternityImageView, (int)((double)this.screenWidth * 0.5D - (double)this.soundImageWidth * 0.5D - (double)this.soundImageWidth * 0.1D), (int)((double)(-this.soundImageWidth) * 0.1D) + var6, 3500);
    var3.addListener(new AnimatorListener() {
      public void onAnimationCancel(Animator var1) {
      }

      public void onAnimationEnd(Animator var1) {
      }

      public void onAnimationRepeat(Animator var1) {
      }

      public void onAnimationStart(Animator var1) {
        TutorialAnimationFactory.this.plusSignTextViewLeft.setVisibility(View.GONE);
        TutorialAnimationFactory.this.plusSignTextViewRight.setVisibility(View.GONE);
      }
    });
    var1.add(var3);
    var1.add(var4);
    var1.add(var5);
    var1.add(var2);
    return var1;
  }

  private List<ObjectAnimator> subSubTitleAnimationsIn() {
    ObjectAnimator var1 = ObjectAnimator.ofFloat(this.subSubTitleTextView, "translationY", new float[]{this.titlesElasticBounceDistance, 0.0F});
    var1.setInterpolator(new ElasticInterpolator(ELASTIC_ANIMATION_PERIOD));
    var1.setDuration(2400L);
    var1.addListener(this.createAnimationListenerForVisibility(new View[]{this.subSubTitleTextView}));
    ObjectAnimator var2 = ObjectAnimator.ofFloat(this.subSubTitleTextView, "alpha", new float[]{0.0F, 1.0F});
    var2.setDuration(1000L);
    ArrayList var3 = new ArrayList();
    var3.add(var1);
    var3.add(var2);
    return var3;
  }

  private List<ObjectAnimator> subTitleAnimationsIn() {
    ObjectAnimator var1 = ObjectAnimator.ofFloat(this.subTitleTextView, "translationY", new float[]{this.titlesElasticBounceDistance, 0.0F});
    var1.setInterpolator(new ElasticInterpolator(ELASTIC_ANIMATION_PERIOD));
    var1.setDuration(2400L);
    var1.addListener(this.createAnimationListenerForVisibility(new View[]{this.subTitleTextView}));
    ObjectAnimator var2 = ObjectAnimator.ofFloat(this.subTitleTextView, "alpha", new float[]{0.0F, 1.0F});
    var2.setDuration(1000L);
    ArrayList var3 = new ArrayList();
    var3.add(var1);
    var3.add(var2);
    return var3;
  }

  private List<ObjectAnimator> subTitleAnimationsOut() {
    ObjectAnimator var1 = ObjectAnimator.ofFloat((TextView)this.activity.findViewById(R.id.tutorial_sub_title), "alpha", new float[]{1.0F, 0.0F});
    var1.setDuration(800L);
    ArrayList var2 = new ArrayList();
    var2.add(var1);
    return var2;
  }

  private List<ObjectAnimator> titleAnimationsIn() {
    ObjectAnimator var1 = ObjectAnimator.ofFloat(this.titleTextView, "translationY", new float[]{this.titlesElasticBounceDistance, 0.0F});
    var1.setInterpolator(new ElasticInterpolator(ELASTIC_ANIMATION_PERIOD));
    var1.setDuration(2400L);
    var1.addListener(this.createAnimationListenerForVisibility(new View[]{this.titleTextView}));
    ObjectAnimator var2 = ObjectAnimator.ofFloat(this.titleTextView, "alpha", new float[]{0.0F, 1.0F});
    var2.setDuration(1000L);
    ArrayList var3 = new ArrayList();
    var3.add(var1);
    var3.add(var2);
    return var3;
  }

  private List<ObjectAnimator> titleAnimationsOut() {
    ObjectAnimator var1 = ObjectAnimator.ofFloat(this.titleTextView, "alpha", new float[]{1.0F, 0.0F});
    var1.setDuration(800L);
    ArrayList var2 = new ArrayList();
    var2.add(var1);
    return var2;
  }

  public AnimatorSet createLastSlideButtonAnimations() {
    ObjectAnimator var1 = ObjectAnimator.ofFloat(this.nextButton, "alpha", new float[]{1.0F, 0.0F});
    var1.setDuration(2000L);
    var1.addListener(new AnimatorListener() {
      public void onAnimationCancel(Animator var1) {
      }

      public void onAnimationEnd(Animator var1) {
      }

      public void onAnimationRepeat(Animator var1) {
      }

      public void onAnimationStart(Animator var1) {
        TutorialAnimationFactory.this.nextButton.setEnabled(false);
        TutorialAnimationFactory.this.nextButton.setClickable(false);
      }
    });
    AnimatorSet var2 = new AnimatorSet();
    this.createLastStepButtonAnimations(var1, var2);
    return var2;
  }

  public Animator createNextButtonDisableAnimation() {
    ObjectAnimator var1 = ObjectAnimator.ofFloat(this.nextButton, "alpha", new float[]{0.4F});
    var1.setDuration(300L);
    var1.addListener(new AnimatorListener() {
      public void onAnimationCancel(Animator var1) {
      }

      public void onAnimationEnd(Animator var1) {
      }

      public void onAnimationRepeat(Animator var1) {
      }

      public void onAnimationStart(Animator var1) {
        TutorialAnimationFactory.this.nextButton.setEnabled(false);
        TutorialAnimationFactory.this.nextButton.setClickable(false);
      }
    });
    return var1;
  }

  public Animator createNextButtonEnableAnimation() {
    ObjectAnimator var1 = ObjectAnimator.ofFloat(this.nextButton, "alpha", new float[]{1.0F});
    var1.setDuration(300L);
    var1.addListener(new AnimatorListener() {
      public void onAnimationCancel(Animator var1) {
      }

      public void onAnimationEnd(Animator var1) {
        TutorialAnimationFactory.this.nextButton.setEnabled(true);
        TutorialAnimationFactory.this.nextButton.setClickable(true);
      }

      public void onAnimationRepeat(Animator var1) {
      }

      public void onAnimationStart(Animator var1) {
      }
    });
    return var1;
  }

  public ViewPagerInOutTransitionAnimations createSlide0Animations() {
    ArrayList var1 = new ArrayList();
    var1.addAll(this.titleAnimationsIn());
    var1.addAll(this.subTitleAnimationsIn());
    var1.addAll(this.slide0AnimationsIn());
    ArrayList var2 = new ArrayList();
    var2.addAll(this.titleAnimationsOut());
    var2.addAll(this.subTitleAnimationsOut());
    var2.addAll(this.slide0AnimationsOut());
    return this.createViewPagerInOutTransition(var1, var2);
  }

  public ViewPagerInOutTransitionAnimations createSlide1Animations() {
    ArrayList var1 = new ArrayList();
    var1.addAll(this.titleAnimationsIn());
    var1.addAll(this.subTitleAnimationsIn());
    var1.addAll(this.slide1AnimationsIn());
    ArrayList var2 = new ArrayList();
    var2.addAll(this.titleAnimationsOut());
    var2.addAll(this.subTitleAnimationsOut());
    var2.addAll(this.slide1AnimationsOut());
    return this.createViewPagerInOutTransition(var1, var2);
  }

  public ViewPagerInOutTransitionAnimations createSlide2Animations() {
    ArrayList var1 = new ArrayList();
    var1.addAll(this.titleAnimationsIn());
    var1.addAll(this.subTitleAnimationsIn());
    var1.addAll(this.slide2AnimationsIn());
    ArrayList var2 = new ArrayList();
    var2.addAll(this.titleAnimationsOut());
    var2.addAll(this.subTitleAnimationsOut());
    var2.addAll(this.slide2AnimationsOut());
    return this.createViewPagerInOutTransition(var1, var2);
  }

  public ViewPagerInOutTransitionAnimations createSlide3Animations() {
    ArrayList var1 = new ArrayList();
    var1.addAll(this.titleAnimationsIn());
    var1.addAll(this.subTitleAnimationsIn());
    var1.addAll(this.slide3AnimationsIn());
    ArrayList var2 = new ArrayList();
    var2.addAll(this.titleAnimationsOut());
    var2.addAll(this.subTitleAnimationsOut());
    var2.addAll(this.slide3AnimationsOut());
    return this.createViewPagerInOutTransition(var1, var2);
  }

  public ViewPagerInOutTransitionAnimations createSlide4Animations() {
    ArrayList var1 = new ArrayList();
    var1.addAll(this.titleAnimationsIn());
    var1.addAll(this.subTitleAnimationsIn());
    if(Utils.getCurrentLanguageLocale(RelaxMelodiesApp.getInstance().getApplicationContext()).equals(Locale.ENGLISH.getLanguage())) {
      var1.addAll(this.subSubTitleAnimationsIn());
    }

    var1.addAll(this.slide4AnimationsIn());
    return this.createViewPagerInTransitionOnly(var1);
  }
}
