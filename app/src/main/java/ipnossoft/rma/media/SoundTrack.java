package ipnossoft.rma.media;

import android.content.Context;
import android.util.Log;
import com.ipnossoft.api.soundlibrary.Sound;
import com.ipnossoft.api.soundlibrary.sounds.GuidedMeditationSound;
import com.ipnossoft.api.soundlibrary.sounds.SoundState;
import com.nativemediaplayer.IMediaPlayer;
import ipnossoft.rma.PersistedDataManager;
import ipnossoft.rma.RelaxMelodiesApp;
import ipnossoft.rma.exceptions.SoundLimitReachedException;

public class SoundTrack {
  public static final float DEFAULT_VOLUME = 0.5F;
  public static final String PREFERENCE_KEY_CURRENT_GUIDED_MEDITATION_VOLUME = "preference_key_current_guided_meditation_volume";
  private String TAG = this.getClass().getSimpleName();
  private Context context;
  private float markedVolume = 0.5F;
  private IMediaPlayer mediaPlayer;
  private Sound sound;
  private SoundState state;
  private float volume = 0.5F;

  public SoundTrack(Context var1, Sound var2, float var3) {
    this.state = SoundState.STOPPED;
    this.context = var1;
    this.sound = var2;
    this.volume = var3;
  }

  private Context getContext() {
    return this.context;
  }

  private void setMediaPlayerVolumeAndNotifyObservers(float var1) {
    if(this.mediaPlayer != null && this.state != SoundState.STOPPED) {
      this.mediaPlayer.setVolume(var1, var1);
    }

    this.volume = var1;
    SoundManager.getInstance().notifySoundVolumeChange(this.sound.getId(), var1);
  }

  private void setVolumeFaded(float var1) {
    this.setMediaPlayerVolumeAndNotifyObservers(var1);
  }

  void disposePlayer() {
    if(this.mediaPlayer != null) {
      try {
        this.mediaPlayer.stop();
        this.mediaPlayer.reset();
        this.mediaPlayer.release();
        this.mediaPlayer = null;
      } catch (IllegalStateException var2) {
        Log.e(this.TAG, var2.getMessage());
        return;
      }
    }

  }

  public IMediaPlayer getMediaPlayer() {
    return this.mediaPlayer;
  }

  public Sound getSound() {
    return this.sound;
  }

  public SoundState getState() {
    return this.state;
  }

  public float getVolume() {
    return this.sound instanceof GuidedMeditationSound?PersistedDataManager.getFloat("preference_key_current_guided_meditation_volume", 0.5F, RelaxMelodiesApp.getInstance().getApplicationContext()):this.volume;
  }

  public void initMediaPlayer() throws Exception {
    this.mediaPlayer = MediaPlayerFactory.createPlayer(this.context, this.sound, this.getVolume());
  }

  public boolean isPaused() {
    return this.state == SoundState.PAUSED;
  }

  public boolean isPlaying() {
    return this.state == SoundState.PLAYING;
  }

  public void markCurrentVolume() {
    this.markedVolume = this.getVolume();
  }

  public void pause() throws Exception {
    if(this.state == SoundState.PLAYING) {
      this.mediaPlayer.pause();
    }

    this.state = SoundState.PAUSED;
  }

  public void play() throws Exception {
    if(this.mediaPlayer == null) {
      this.initMediaPlayer();
    }

    if(this.mediaPlayer != null) {
      this.state = SoundState.PLAYING;
      this.mediaPlayer.start();
    } else {
      throw new SoundLimitReachedException();
    }
  }

  public void resetVolume() {
    this.setVolume(0.5F);
  }

  public void restoreMarkedVolume() {
    this.setVolume(this.markedVolume);
  }

  public void setPercentageMarkedVolume(float var1) {
    float var2 = var1;
    if(var1 < 0.0F) {
      var2 = 0.0F;
    }

    this.setVolumeFaded(this.markedVolume * var2);
  }

  public void setVolume(float var1) {
    this.setMediaPlayerVolumeAndNotifyObservers(var1);
    if(this.sound instanceof GuidedMeditationSound) {
      PersistedDataManager.saveFloat("preference_key_current_guided_meditation_volume", var1, RelaxMelodiesApp.getInstance().getApplicationContext());
    }

  }

  public void stop() {
    if(this.mediaPlayer != null && this.state != SoundState.STOPPED) {
      this.disposePlayer();
    }

    this.state = SoundState.STOPPED;
  }

  public String toString() {
    return "SoundTrack (" + this.sound.getId() + ")";
  }
}
