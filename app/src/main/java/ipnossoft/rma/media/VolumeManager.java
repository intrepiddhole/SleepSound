package ipnossoft.rma.media;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.Animator.AnimatorListener;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;
import com.ipnossoft.api.soundlibrary.Sound;

import ipnossoft.rma.free.R;

public abstract class VolumeManager implements OnSeekBarChangeListener {
  private static final double FACTOR = Math.pow(100.0D, -1.0D);
  static final int MAX_PROGRESS = 100;
  private static final String PREF_FILE = "ipnossoft.rma.persisted.data";
  private static final String PREF_SUBVOLUME_CHANGED_ONCE = "subvolume_changed_at_least_once";
  private static final int VOLUME_BAR_FADE_ANIMATION_DURATION = 700;
  Activity activity;
  private Runnable animateDropDownHint = new Runnable() {
    public void run() {
      VolumeManager.this.hintAlreadyVisible = true;
      VolumeManager.this.hintView.setVisibility(View.VISIBLE);
      VolumeManager.this.hintView.startAnimation(VolumeManager.this.scale);
    }
  };
  private Handler animationHandler = new Handler();
  ObjectAnimator fadeInAnimator = null;
  ObjectAnimator fadeOutAnimator = null;
  private boolean hintAlreadyVisible = false;
  private TextView hintTextView;
  RelativeLayout hintView;
  private boolean isFadeOutEnabled = true;
  View layoutVolume;
  int numberOfSecondsTillTimeoutSubvolumeFadeOut = 4;
  private SharedPreferences prefs;
  private Runnable resetDropDownHint = new Runnable() {
    public void run() {
      VolumeManager.this.hintAlreadyVisible = false;
      VolumeManager.this.scaleInverse.setDuration(0L);
      VolumeManager.this.hintView.setAlpha(1.0F);
      VolumeManager.this.hintView.startAnimation(VolumeManager.this.scaleInverse);
    }
  };
  private ScaleAnimation scale = new ScaleAnimation(1.0F, 1.0F, 0.0F, 1.0F);
  private ScaleAnimation scaleInverse = new ScaleAnimation(1.0F, 1.0F, 1.0F, 0.0F);
  SeekBar seekBar;
  protected boolean shouldShowHint = false;
  TextView textView;
  SoundTrack track;

  VolumeManager() {
  }

  VolumeManager(Activity var1, View var2) {
    this.activity = var1;
    this.layoutVolume = var2;
    this.textView = (TextView)var2.findViewById(R.id.label_sound_volume);
    this.seekBar = (SeekBar)var2.findViewById(R.id.seekbar_sound_volume);
    this.handleSubVolumeHint();
  }

  private void animateDropdownHint() {
    this.animationHandler.removeCallbacks(this.animateDropDownHint);
    this.animationHandler.postDelayed(this.animateDropDownHint, 500L);
  }

  private void animateHidingOfHintTextView() {
    this.hintView.animate().alpha(0.0F).setDuration(1000L).setListener(new AnimatorListenerAdapter() {
      public void onAnimationEnd(Animator var1) {
        super.onAnimationEnd(var1);
        VolumeManager.this.hintView.setVisibility(View.GONE);
        VolumeManager.this.hintAlreadyVisible = false;
      }
    });
  }

  private float computeScalarFromProgress(int var1) {
    return (float)((double)var1 * FACTOR);
  }

  private void createFadeInAnimator() {
    this.fadeInAnimator = ObjectAnimator.ofFloat(this.layoutVolume, View.ALPHA, new float[]{0.0F, 1.0F});
    this.fadeInAnimator.setDuration(700L);
  }

  private void createFadeOutAnimator() {
    this.fadeOutAnimator = ObjectAnimator.ofFloat(this.layoutVolume, View.ALPHA, new float[]{1.0F, 0.0F});
    this.fadeOutAnimator.setDuration(700L);
    this.fadeOutAnimator.addListener(new AnimatorListener() {
      public void onAnimationCancel(Animator var1) {
      }

      public void onAnimationEnd(Animator var1) {
        VolumeManager.this.hintAlreadyVisible = false;
        VolumeManager.this.layoutVolume.setVisibility(View.GONE);
      }

      public void onAnimationRepeat(Animator var1) {
      }

      public void onAnimationStart(Animator var1) {
      }
    });
  }

  private void doFadeInHintIfNeeded() {
    if(this.shouldShowHint) {
      this.hintTextView.setAlpha(0.0F);
      this.hintView.setVisibility(View.GONE);
      this.animateDropdownHint();
      this.fadeInHintText();
    }

  }

  private void doShowVolumeBar() {
    if(!this.isVolumeBarVisible()) {
      if(this.fadeOutAnimator != null && this.fadeOutAnimator.isRunning()) {
        this.fadeOutAnimator.cancel();
      }

      if(this.fadeInAnimator != null && !this.fadeInAnimator.isRunning()) {
        this.layoutVolume.setVisibility(View.VISIBLE);
        this.fadeInAnimator.start();
      }
    }

  }

  private void fadeInHintText() {
    (new Handler()).postDelayed(new Runnable() {
      public void run() {
        VolumeManager.this.hintTextView.animate().alpha(1.0F).setDuration(300L);
      }
    }, 900L);
  }

  private void removeHint() {
    if(this.hintView.getVisibility() == View.VISIBLE) {
      this.animateHidingOfHintTextView();
      Editor var1 = this.activity.getSharedPreferences("ipnossoft.rma.persisted.data", 0).edit();
      var1.putBoolean("subvolume_changed_at_least_once", true);
      var1.apply();
      this.numberOfSecondsTillTimeoutSubvolumeFadeOut = 4;
    }

  }

  int computeProgressFromScalar(float var1) {
    return (int)(100.0F * var1);
  }

  public void enableSubVolumeDismissal() {
    this.isFadeOutEnabled = true;
  }

  Sound getSound() {
    return this.track != null?this.track.getSound():null;
  }

  @NonNull
  String getSoundLabel(SoundTrack var1) {
    String var2 = var1.getSound().getName();
    String var3 = var2;
    if(var2.length() > 63) {
      var3 = var2.substring(0, 60) + "...";
    }

    return var3;
  }

  void handleSubVolumeHint() {
    this.hintView = (RelativeLayout)this.layoutVolume.findViewById(R.id.subvolume_hint_layout);
    this.hintTextView = (TextView)this.layoutVolume.findViewById(R.id.subvolume_hint_textview);
    this.prefs = this.activity.getSharedPreferences("ipnossoft.rma.persisted.data", 0);
    this.scale.setDuration(400L);
    this.shouldShowHint = this.shouldShowHint();
    if(this.shouldShowHint) {
      this.numberOfSecondsTillTimeoutSubvolumeFadeOut = 10;
    }

  }

  public void hideVolumeLayout() {
    this.hideVolumeLayoutInternal();
  }

  void hideVolumeLayoutInternal() {
    if(this.isVolumeBarVisible()) {
      if(this.fadeInAnimator != null && this.fadeInAnimator.isRunning()) {
        this.fadeInAnimator.cancel();
      }

      if(this.fadeOutAnimator != null && !this.fadeOutAnimator.isRunning() && this.isFadeOutEnabled) {
        this.fadeOutAnimator.start();
      }
    }

  }

  public boolean isVolumeBarVisible() {
    return this.layoutVolume.getAlpha() > 0.0F;
  }

  public void onProgressChanged(SeekBar var1, int var2, boolean var3) {
    if(this.track != null) {
      this.track.setVolume(this.computeScalarFromProgress(var2));
    }

  }

  public void onStartTrackingTouch(SeekBar var1) {
  }

  public void onStopTrackingTouch(SeekBar var1) {
    this.removeHint();
  }

  protected abstract void prepareVolumeLayout(SoundTrack var1);

  public void preventSubVolumeDismissal() {
    this.isFadeOutEnabled = false;
  }

  public void refreshHintStatus() {
    if(this.hintView.getVisibility() == View.VISIBLE && this.prefs.getBoolean("subvolume_changed_at_least_once", false)) {
      this.hintView.setVisibility(View.GONE);
      this.shouldShowHint = false;
      this.numberOfSecondsTillTimeoutSubvolumeFadeOut = 4;
      this.layoutVolume.requestLayout();
    } else if(this.hintView.getVisibility() == View.VISIBLE && this.shouldShowHint()) {
      this.hintTextView.setAlpha(1.0F);
      return;
    }

  }

  public void resetHint() {
    Editor var1 = this.activity.getSharedPreferences("ipnossoft.rma.persisted.data", 0).edit();
    var1.putBoolean("subvolume_changed_at_least_once", false);
    var1.apply();
    this.shouldShowHint = true;
    this.numberOfSecondsTillTimeoutSubvolumeFadeOut = 10;
    this.animationHandler.post(this.resetDropDownHint);
  }

  void setupVolumeBar(View var1) {
    var1.setAlpha(0.0F);
    var1.setVisibility(View.GONE);
    this.createFadeInAnimator();
    this.createFadeOutAnimator();
  }

  boolean shouldShowHint() {
    boolean var1 = false;
    if(!this.prefs.getBoolean("subvolume_changed_at_least_once", false)) {
      var1 = true;
    }

    return var1;
  }

  public void showVolumeLayout(SoundTrack var1) {
    this.refreshHintStatus();
    this.prepareVolumeLayout(var1);
    this.doShowVolumeBar();
    if(!this.hintAlreadyVisible) {
      this.doFadeInHintIfNeeded();
    }

  }
}
