package ipnossoft.rma.media;

import android.app.Activity;
import android.view.View;
import android.widget.SeekBar;

import ipnossoft.rma.free.R;
import ipnossoft.rma.util.RelaxAnalytics;

public class GuidedMeditationVolumeManager extends VolumeManager {
  public GuidedMeditationVolumeManager(Activity var1, View var2) {
    super(var1, var2);
    this.seekBar.setOnSeekBarChangeListener(this);
    this.seekBar.setMax(100);
    this.setupVolumeBar(var2);
  }

  void handleSubVolumeHint() {
    super.handleSubVolumeHint();
    this.hintView.setVisibility(View.GONE);
  }

  public void hideVolumeLayoutInstant() {
    if(this.fadeInAnimator != null && this.fadeInAnimator.isRunning()) {
      this.fadeInAnimator.cancel();
      this.layoutVolume.setVisibility(View.GONE);
      this.layoutVolume.setAlpha(0.0F);
    }

  }

  public void onStartTrackingTouch(SeekBar var1) {
  }

  public void onStopTrackingTouch(SeekBar var1) {
    super.onStopTrackingTouch(var1);
    RelaxAnalytics.logGuidedMeditationSubVolumeChanged(this.getSound(), var1.getProgress());
  }

  protected void prepareVolumeLayout(SoundTrack var1) {
    this.track = var1;
    this.setSubvolumeText();
    this.seekBar.setProgress(this.computeProgressFromScalar(var1.getVolume()));
    if(this.fadeOutAnimator != null && this.fadeOutAnimator.isRunning()) {
      this.fadeOutAnimator.cancel();
      this.layoutVolume.setVisibility(View.VISIBLE);
      this.layoutVolume.setAlpha(1.0F);
    }

  }

  public void setSubvolumeText() {
    this.textView.setText(this.activity.getString(R.string.playing_guided_meditation_sub_volume));
  }

  boolean shouldShowHint() {
    return false;
  }

  public void showVolumeLayoutInstant(SoundTrack var1) {
    this.prepareVolumeLayout(var1);
    this.layoutVolume.setVisibility(View.VISIBLE);
    this.layoutVolume.setAlpha(1.0F);
  }

  public void updateProgress() {
    if(this.track != null) {
      this.seekBar.setProgress(this.computeProgressFromScalar(this.track.getVolume()));
    }

  }
}
