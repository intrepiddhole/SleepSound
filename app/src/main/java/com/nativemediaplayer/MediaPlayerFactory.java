package com.nativemediaplayer;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

class MediaPlayerFactory {
  private static final String TAG = "MediaPlayerFactory";
  private static final boolean ndkLogging = false;

  MediaPlayerFactory() {
  }

  public static IMediaPlayer ctor(Context var0) {
    if(usingNativeMediaPlayer(var0) && MediaPlayerFactory.Preferences.isNativeAudioInitializable(var0)) {
      if(isChainedAudioAvailable()) {
        Log.d("MediaPlayerFactory", ".ctor() returning ChainedMediaPlayer");
        return new ChainedMediaPlayer(var0);
      } else {
        Log.d("MediaPlayerFactory", ".ctor() returning NativeMediaPlayer");
        return new NativeMediaPlayer(var0);
      }
    } else {
      Log.d("MediaPlayerFactory", ".ctor() returning SdkMediaPlayer");
      return new SdkMediaPlayer(var0);
    }
  }

  private static boolean isChainedAudioAvailable() {
    return OsUtil.apiLevel >= 16;
  }

  private static boolean isNativeAudioAvailable() {
    return OsUtil.apiLevel >= 9;
  }

  public static void shutdown(Context var0) {
    if(usingNativeMediaPlayer(var0) && !isChainedAudioAvailable() && MediaPlayerFactory.Preferences.isNativeAudioInitializable(var0)) {
      Log.d("MediaPlayerFactory", "NativeMediaPlayer.shutdown()");
      NativeMediaPlayer.shutdown();
    }

  }

  public static void startup(Context var0) {
    if(usingNativeMediaPlayer(var0) && !isChainedAudioAvailable() && MediaPlayerFactory.Preferences.isNativeAudioInitializable(var0)) {
      try {
        Log.d("MediaPlayerFactory", "NativeMediaPlayer.startup()");
        NativeMediaPlayer.startup(var0, false, useBufferThread(), useNativeVorbisDecoder());
      } catch (Exception var2) {
        Log.e("MediaPlayerFactory", "Failed to initialize NativeMediaPlayer. ctor() will always return SdkMediaPlayer.");
        MediaPlayerFactory.Preferences.setNativeAudioIsInitializable(var0, false);
        return;
      }
    }

  }

  private static boolean useBufferThread() {
    return OsUtil.apiLevel >= 14;
  }

  private static boolean useNativeVorbisDecoder() {
    return OsUtil.apiLevel >= 14;
  }

  private static boolean usingNativeMediaPlayer(Context var0) {
    return isNativeAudioAvailable() && MediaPlayerFactory.Preferences.useNativePlayer(var0);
  }

  public static class Preferences {
    private static final String BUCKET = MediaPlayerFactory.class.getName();

    public Preferences() {
    }

    private static SharedPreferences getPreferences(Context var0) {
      return var0.getSharedPreferences(BUCKET, 0);
    }

    private static boolean isNativeAudioInitializable(Context var0) {
      return getPreferences(var0).getBoolean("isNativeAudioInitializable", true);
    }

    private static void setNativeAudioIsInitializable(Context var0, boolean var1) {
      Editor var2 = getPreferences(var0).edit();
      var2.putBoolean("isNativeAudioInitializable", var1);
      var2.commit();
    }

    public static void toggleUseNativePlayer(Context var0) {
      Editor var2 = getPreferences(var0).edit();
      boolean var1;
      if(!useNativePlayer(var0)) {
        var1 = true;
      } else {
        var1 = false;
      }

      var2.putBoolean("useNativePlayer", var1);
      var2.commit();
    }

    public static boolean useNativePlayer(Context var0) {
      return getPreferences(var0).getBoolean("useNativePlayer", false);
    }
  }
}
