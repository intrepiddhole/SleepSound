package ipnossoft.rma.ui.button;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import java.util.Random;

import ipnossoft.rma.free.R;

public abstract class AnimatedButton extends FeatureButton {
  private static final String ANIMATOR_PROPERTY = "rotation";
  private static final int LOOP_ANIMATION_DURATION = 1750;
  private static final int START_MAX_ANIMATION_DURATION = 1575;
  private static final int START_MIN_ANIMATION_DURATION = 875;
  private boolean animated;
  private View animatedView;
  private Animation animationLoop;
  private int animationLoopId;
  private Animation animationStart;
  private int animationStartId;
  private ObjectAnimator animatorLoop;
  private ObjectAnimator animatorStart;
  private ObjectAnimator animatorStop;
  private int randomStartAnimationDuration = 875;
  private volatile boolean startAnimationCanceled = false;

  public AnimatedButton(Context var1) {
    super(var1);
  }

  AnimatedButton(Context var1, int var2, int var3, int var4) {
    super(var1, var2);
    if(var3 == R.anim.rotate_sound_button_1 && this.getRandomNumberBetweenNumbers(0, 2) == 1) {
      this.animationStartId = R.anim.rotate_sound_button_inverted_1;
    } else {
      this.animationStartId = var3;
    }

    this.animationLoopId = var4;
    this.randomStartAnimationDuration = this.getRandomNumberBetweenNumbers(875, 1575);
  }

  public AnimatedButton(Context var1, AttributeSet var2) {
    super(var1, var2);
    this.initAnimatedButtonFromXml(var1, var2);
  }

  public AnimatedButton(Context var1, AttributeSet var2, int var3) {
    super(var1, var2, var3);
    this.initAnimatedButtonFromXml(var1, var2);
  }

  private ObjectAnimator createLoopAnimator() {
    ObjectAnimator var1 = new ObjectAnimator();
    var1.setTarget(this.getAnimatedView());
    var1.setFloatValues(new float[]{this.getToDegrees(), -this.getToDegrees()});
    var1.setPropertyName("rotation");
    var1.setInterpolator(new AccelerateDecelerateInterpolator());
    var1.setRepeatMode(ValueAnimator.REVERSE);
    var1.setRepeatCount(-1);
    var1.setDuration(1750L);
    return var1;
  }

  private ObjectAnimator createStartAnimator() {
    ObjectAnimator var1 = new ObjectAnimator();
    var1.setTarget(this.getAnimatedView());
    var1.setFloatValues(new float[]{this.getToDegrees()});
    var1.setPropertyName("rotation");
    var1.setInterpolator(new DecelerateInterpolator());
    var1.setDuration((long)this.randomStartAnimationDuration);
    return var1;
  }

  private int getRandomNumberBetweenNumbers(int var1, int var2) {
    return (new Random()).nextInt(var2 - var1) + var1;
  }

  private float getToDegrees() {
    return this.animationStartId != R.anim.rotate_beat_button_1 && this.animationStartId != R.anim.rotate_beat_button_2?(this.animationStartId == R.anim.rotate_sound_button_inverted_1?-8.0F:7.0F):4.0F;
  }

  private void initAnimationNew() {
    if(this.animatorStart != null) {
      this.stopAnimation();
    }

    this.animatorStart = this.createStartAnimator();
    this.animatorStart.setTarget(this.getAnimatedView());
    this.animatorStart.addListener(new AnimatorListener() {
      public void onAnimationCancel(Animator var1) {
        AnimatedButton.this.startAnimationCanceled = true;
        AnimatedButton.this.runStopAnimation();
      }

      public void onAnimationEnd(Animator var1) {
        if(!AnimatedButton.this.startAnimationCanceled) {
          AnimatedButton.this.animatorLoop.start();
        }

      }

      public void onAnimationRepeat(Animator var1) {
      }

      public void onAnimationStart(Animator var1) {
      }
    });
    this.animatorLoop = this.createLoopAnimator();
    this.animatorLoop.setTarget(this.animatorStart.getTarget());
    this.animatorLoop.addListener(new AnimatorListener() {
      public void onAnimationCancel(Animator var1) {
        AnimatedButton.this.runStopAnimation();
      }

      public void onAnimationEnd(Animator var1) {
      }

      public void onAnimationRepeat(Animator var1) {
      }

      public void onAnimationStart(Animator var1) {
      }
    });
  }

  private boolean isAnimated() {
    return this.animated;
  }

  private boolean isAnimationInitialized() {
    return this.animatorStart != null;
  }

  private void pauseAnimation() {
    boolean var1 = this.isAnimated();
    this.stopAnimation();
    this.animated = var1;
  }

  private void runStopAnimation() {
    this.animatorStop = new ObjectAnimator();
    this.animatorStop.setTarget(this.animatorLoop.getTarget());
    this.animatorStop.setFloatValues(new float[]{0.0F});
    this.animatorStop.setPropertyName("rotation");
    this.animatorStop.setInterpolator(new DecelerateInterpolator());
    this.animatorStop.setDuration((long)this.randomStartAnimationDuration);
    this.animatorStop.start();
  }

  private void startAnimation() {
    this.stopAnimation();
    this.animated = true;
    this.getAnimatedView().bringToFront();
    this.startAnimationCanceled = false;
    this.animatorStart.start();
  }

  private void stopAnimation() {
    this.animated = false;
    if(this.animatorStart != null && this.animatorStart.isRunning()) {
      this.animatorStart.cancel();
    }

    if(this.animatorLoop != null && this.animatorLoop.isRunning()) {
      this.animatorLoop.cancel();
    }

    this.getAnimatedView().clearAnimation();
  }

  public View getAnimatedView() {
    Object var1 = this;
    if(this.animatedView != null) {
      var1 = this.animatedView;
    }

    return (View)var1;
  }

  void initAnimatedButtonFromXml(Context var1, AttributeSet var2) {
    TypedArray var5 = var1.getTheme().obtainStyledAttributes(var2, R.styleable.AnimatedButton, 0, 0);

    try {
      this.animationStartId = var5.getResourceId(R.styleable.AnimatedButton_animationStartId, -1);
      if(this.animationStartId == R.anim.rotate_sound_button_1 && this.getRandomNumberBetweenNumbers(0, 2) == 1) {
        this.animationStartId = R.anim.rotate_sound_button_inverted_1;
      }

      this.animationLoopId = var5.getResourceId(R.styleable.AnimatedButton_animationLoopId, -1);
      this.randomStartAnimationDuration = this.getRandomNumberBetweenNumbers(875, 1575);
    } finally {
      var5.recycle();
    }

  }

  void initAnimation() {
    if(!this.isAnimationInitialized()) {
      this.initAnimationNew();
    }

  }

  public void removeFromContainer() {
    super.removeFromContainer();
    this.pauseAnimation();
  }

  public void setAnimated(boolean var1) {
    this.initAnimation();
    if(var1) {
      this.startAnimation();
    } else {
      this.stopAnimation();
    }
  }

  public void setAnimatedView(View var1) {
    this.animatedView = var1;
    if(this.animatorStart != null) {
      this.animatorStart.setTarget(var1);
    }

    if(this.animatorLoop != null) {
      this.animatorLoop.setTarget(var1);
    }

  }
}
