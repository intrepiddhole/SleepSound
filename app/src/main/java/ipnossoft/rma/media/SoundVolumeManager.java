package ipnossoft.rma.media;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import com.ipnossoft.api.soundlibrary.Sound;

import ipnossoft.rma.free.R;
import ipnossoft.rma.util.RelaxAnalytics;

public class SoundVolumeManager extends VolumeManager {
  private static final String TAG = "SoundVolumeManager";
  private static volatile boolean doNotHideVolumeBar = false;
  private static Thread hiderThread;
  private static SoundVolumeManager instance;
  private static volatile int volumeBarTimeout = 0;
  private VolumeBarEnabler volumeBarEnabler;

  public SoundVolumeManager() {
  }

  public static SoundVolumeManager getInstance() {
    if(instance == null) {
      instance = new SoundVolumeManager();
    }

    return instance;
  }

  private void resetVolumeBarTimeout() {
    volumeBarTimeout = this.numberOfSecondsTillTimeoutSubvolumeFadeOut;
  }

  private void startVolumeBarHider() {
    if(hiderThread == null || !hiderThread.isAlive()) {
      hiderThread = new Thread(new Runnable() {
        public void run() {
          while(true) {
            try {
              Thread.sleep(1000L);
              if(SoundVolumeManager.volumeBarTimeout > 0 && !SoundVolumeManager.doNotHideVolumeBar) {
                SoundVolumeManager.volumeBarTimeout = SoundVolumeManager.volumeBarTimeout - 1;
                if(SoundVolumeManager.volumeBarTimeout == 0) {
                  SoundVolumeManager.this.activity.runOnUiThread(new Runnable() {
                    public void run() {
                      SoundVolumeManager.instance.hideVolumeLayoutInternal();
                    }
                  });
                }
              }
            } catch (Exception var2) {
              Log.e("SoundVolumeManager", "", var2);
              return;
            }
          }
        }
      });
      hiderThread.setDaemon(true);
      hiderThread.start();
    }
  }

  public void configureSoundVolumeManager(View var1, Activity var2, Object var3) {
    this.layoutVolume = var1;
    this.activity = var2;
    this.volumeBarEnabler = (VolumeBarEnabler)var3;
    this.seekBar = (SeekBar)var1.findViewById(R.id.seekbar_sound_volume);
    this.seekBar.setOnSeekBarChangeListener(this);
    this.seekBar.setMax(100);
    this.textView = (TextView)var1.findViewById(R.id.label_sound_volume);
    instance.handleSubVolumeHint();
    this.setupVolumeBar(var1);
    this.startVolumeBarHider();
  }

  public void hideVolumeLayout() {
    this.hideVolumeLayoutInternal();
  }

  public void hideVolumeLayoutInternal(SoundTrack var1) {
    if(var1 != null && this.track != null && var1.getSound().getId().equals(this.track.getSound().getId())) {
      this.hideVolumeLayoutInternal();
    }

  }

  public void onStartTrackingTouch(SeekBar var1) {
    doNotHideVolumeBar = true;
  }

  public void onStopTrackingTouch(SeekBar var1) {
    super.onStopTrackingTouch(var1);
    Sound var2 = this.getSound();
    if(var2 != null) {
      RelaxAnalytics.logSoundSubVolumeChanged(var2, var1.getProgress());
    }

    doNotHideVolumeBar = false;
    this.resetVolumeBarTimeout();
  }

  protected void prepareVolumeLayout(SoundTrack var1) {
    this.track = var1;
    String var2 = this.getSoundLabel(var1);
    this.textView.setText(var2);
    this.seekBar.setProgress(this.computeProgressFromScalar(var1.getVolume()));
    this.resetVolumeBarTimeout();
    if(this.fadeOutAnimator != null && this.fadeOutAnimator.isRunning()) {
      this.fadeOutAnimator.cancel();
      this.layoutVolume.setVisibility(View.VISIBLE);
      this.layoutVolume.setAlpha(1.0F);
    }

  }

  public void showVolumeLayout(SoundTrack var1) {
    if(this.volumeBarEnabler.isSoundVolumeBarEnabled()) {
      super.showVolumeLayout(var1);
    }

  }

  public void stopVolumeBarFadeInAnimationIfNeeded() {
    if(this.fadeInAnimator != null && this.fadeInAnimator.isRunning()) {
      this.fadeInAnimator.cancel();
      this.layoutVolume.setAlpha(1.0F);
      this.layoutVolume.setVisibility(View.GONE);
    }

  }

  public interface VolumeBarEnabler {
    boolean isSoundVolumeBarEnabled();
  }

}
