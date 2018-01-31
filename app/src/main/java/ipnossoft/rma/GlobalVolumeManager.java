package ipnossoft.rma;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import ipnossoft.rma.free.R;
import ipnossoft.rma.util.RelaxAnalytics;

public class GlobalVolumeManager implements OnSeekBarChangeListener {
  private String TAG = this.getClass().getSimpleName();
  private final AudioManager audioManager;
  private float progressAtTrackingStart;
  public SeekBar seekBar;

  public GlobalVolumeManager(Activity var1) {
    var1.setVolumeControlStream(3);
    this.audioManager = (AudioManager)var1.getSystemService(Context.AUDIO_SERVICE);
    this.seekBar = (SeekBar)var1.findViewById(R.id.seekbar_volume);
    this.init();
  }

  private void init() {
    try {
      this.seekBar.setOnSeekBarChangeListener(this);
      this.seekBar.setMax(this.audioManager.getStreamMaxVolume(3));
      this.updateSeekBar();
    } catch (SecurityException var2) {
      Log.e(this.TAG, var2.getMessage());
    }
  }

  private void updateVolume(int var1) {
    try {
      float var2 = (float)this.seekBar.getProgress() / (float)this.seekBar.getMax();
      this.audioManager.adjustStreamVolume(3, var1, 0);
      this.updateSeekBar();
      RelaxAnalytics.logMainVolumeChanged(var2, (float)this.seekBar.getProgress() / (float)this.seekBar.getMax());
    } catch (SecurityException var4) {
      Log.e(this.TAG, var4.getMessage());
    }
  }

  public void onProgressChanged(SeekBar var1, int var2, boolean var3) {
    if(var3) {
      try {
        this.audioManager.setStreamVolume(3, var2, 0);
      } catch (SecurityException var4) {
        Log.e(this.TAG, var4.getMessage());
        return;
      }
    }

  }

  public void onStartTrackingTouch(SeekBar var1) {
    this.progressAtTrackingStart = (float)var1.getProgress() / (float)var1.getMax();
  }

  public void onStopTrackingTouch(SeekBar var1) {
    float var2 = (float)var1.getProgress() / (float)var1.getMax();
    RelaxAnalytics.logMainVolumeChanged(this.progressAtTrackingStart, var2);
  }

  public void updateSeekBar() {
    int var1 = this.audioManager.getStreamVolume(3);
    this.seekBar.setProgress(var1);
  }

  public void updateVolumeDown() {
    this.updateVolume(-1);
  }

  public void updateVolumeUp() {
    this.updateVolume(1);
  }
}
