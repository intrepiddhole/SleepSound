package com.nativemediaplayer;

import android.content.Context;
import android.media.MediaPlayer;
import com.nativemediaplayer.IMediaPlayer;
import com.nativemediaplayer.IMediaPlayer.OnCompletionListener;
import com.nativemediaplayer.IMediaPlayer.OnErrorListener;
import com.nativemediaplayer.IMediaPlayer.OnInfoListener;
import com.nativemediaplayer.IMediaPlayer.OnPreparedListener;
import com.nativemediaplayer.IMediaPlayer.OnSeekCompleteListener;
import com.nativemediaplayer.IMediaPlayer.OnVideoSizeChangedListener;

public class SdkMediaPlayer extends MediaPlayer implements IMediaPlayer {
  public SdkMediaPlayer(Context var1) {
  }

  public static void shutdown() {
  }

  public static void startup(Context var0, boolean var1, boolean var2, boolean var3) {
  }

  public void attachAuxEffect(int var1) {
  }

  public int getAudioSessionId() {
    return 0;
  }

  public void setAudioSessionId(int var1) throws IllegalArgumentException, IllegalStateException {
  }

  public void setAuxEffectSendLevel(float var1) {
  }

  public void setOnBufferingUpdateListener(final IMediaPlayer.OnBufferingUpdateListener var1) {
    super.setOnBufferingUpdateListener(new android.media.MediaPlayer.OnBufferingUpdateListener() {
      public void onBufferingUpdate(MediaPlayer var1x, int var2) {
        var1.onBufferingUpdate((SdkMediaPlayer)var1x, var2);
      }
    });
  }

  @Override
  public void setOnCompletionListener(Object var1) {
    setOnCompletionListener((IMediaPlayer.OnCompletionListener) var1);
  }

  public void setOnCompletionListener(final IMediaPlayer.OnCompletionListener var1) {
    super.setOnCompletionListener(new android.media.MediaPlayer.OnCompletionListener() {
      public void onCompletion(MediaPlayer var1x) {
        var1.onCompletion((SdkMediaPlayer)var1x);
      }
    });
  }

  public void setOnErrorListener(final IMediaPlayer.OnErrorListener var1) {
    super.setOnErrorListener(new android.media.MediaPlayer.OnErrorListener() {
      public boolean onError(MediaPlayer var1x, int var2, int var3) {
        return var1.onError((SdkMediaPlayer)var1x, var2, var3);
      }
    });
  }

  public void setOnInfoListener(final IMediaPlayer.OnInfoListener var1) {
    super.setOnInfoListener(new android.media.MediaPlayer.OnInfoListener() {
      public boolean onInfo(MediaPlayer var1x, int var2, int var3) {
        return var1.onInfo((SdkMediaPlayer)var1x, var2, var3);
      }
    });
  }

  public void setOnPreparedListener(final IMediaPlayer.OnPreparedListener var1) {
    super.setOnPreparedListener(new android.media.MediaPlayer.OnPreparedListener() {
      public void onPrepared(MediaPlayer var1x) {
        var1.onPrepared((SdkMediaPlayer)var1x);
      }
    });
  }

  public void setOnSeekCompleteListener(final IMediaPlayer.OnSeekCompleteListener var1) {
    super.setOnSeekCompleteListener(new android.media.MediaPlayer.OnSeekCompleteListener() {
      public void onSeekComplete(MediaPlayer var1x) {
        var1.onSeekComplete((SdkMediaPlayer)var1x);
      }
    });
  }

  public void setOnVideoSizeChangedListener(final IMediaPlayer.OnVideoSizeChangedListener var1) {
    super.setOnVideoSizeChangedListener(new android.media.MediaPlayer.OnVideoSizeChangedListener() {
      public void onVideoSizeChanged(MediaPlayer var1x, int var2, int var3) {
        var1.onVideoSizeChanged((SdkMediaPlayer)var1x, var2, var3);
      }
    });
  }
}
