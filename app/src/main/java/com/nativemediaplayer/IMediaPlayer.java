package com.nativemediaplayer;

import android.content.Context;
import android.net.Uri;
import android.view.SurfaceHolder;
import java.io.FileDescriptor;
import java.io.IOException;

public interface IMediaPlayer {
  void attachAuxEffect(int var1);

  int getAudioSessionId();

  int getCurrentPosition();

  int getDuration();

  int getVideoHeight();

  int getVideoWidth();

  boolean isLooping();

  boolean isPlaying();

  void pause() throws IllegalStateException;

  void prepare() throws IOException, IllegalStateException;

  void prepareAsync() throws IllegalStateException;

  void release();

  void reset();

  void seekTo(int var1) throws IllegalStateException;

  void setAudioSessionId(int var1) throws IllegalArgumentException, IllegalStateException;

  void setAudioStreamType(int var1);

  void setAuxEffectSendLevel(float var1);

  void setDataSource(Context var1, Uri var2) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException;

  void setDataSource(FileDescriptor var1) throws IOException, IllegalArgumentException, IllegalStateException;

  void setDataSource(FileDescriptor var1, long var2, long var4) throws IOException, IllegalArgumentException, IllegalStateException;

  void setDataSource(String var1) throws IOException, IllegalArgumentException, IllegalStateException;

  void setDisplay(SurfaceHolder var1);

  void setLooping(boolean var1);

  void setOnBufferingUpdateListener(IMediaPlayer.OnBufferingUpdateListener var1);

  void setOnCompletionListener(Object var1);

  void setOnErrorListener(IMediaPlayer.OnErrorListener var1);

  void setOnInfoListener(IMediaPlayer.OnInfoListener var1);

  void setOnPreparedListener(IMediaPlayer.OnPreparedListener var1);

  void setOnSeekCompleteListener(IMediaPlayer.OnSeekCompleteListener var1);

  void setOnVideoSizeChangedListener(IMediaPlayer.OnVideoSizeChangedListener var1);

  void setScreenOnWhilePlaying(boolean var1);

  void setVolume(float var1, float var2);

  void setWakeMode(Context var1, int var2);

  void start() throws IllegalStateException;

  void stop() throws IllegalStateException;

  public interface OnBufferingUpdateListener {
    void onBufferingUpdate(IMediaPlayer var1, int var2);
  }

  public interface OnErrorListener {
    boolean onError(IMediaPlayer var1, int var2, int var3);
  }

  public interface OnInfoListener {
    boolean onInfo(IMediaPlayer var1, int var2, int var3);
  }

  public interface OnPreparedListener {
    void onPrepared(IMediaPlayer var1);
  }

  public interface OnSeekCompleteListener {
    void onSeekComplete(IMediaPlayer var1);
  }

  public interface OnVideoSizeChangedListener {
    void onVideoSizeChanged(IMediaPlayer var1, int var2, int var3);
  }

  public interface OnCompletionListener {
    void onCompletion(IMediaPlayer var1);
  }

}