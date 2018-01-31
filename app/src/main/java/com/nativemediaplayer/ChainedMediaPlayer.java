package com.nativemediaplayer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.net.Uri;
import android.view.SurfaceHolder;
import java.io.FileDescriptor;
import java.io.IOException;
import java.lang.ref.WeakReference;

public class ChainedMediaPlayer implements IMediaPlayer {
  private static final String TAG_PREFIX = "ChainedMediaPlayer";
  private static int id = 1;
  private static final int size = 3;
  private final String TAG;
  private int audioStreamType = 3;
  private boolean autoStart;
  private MediaPlayer current = null;
  private boolean isPrepared = false;
  private float leftVolume = 1.0F;
  private boolean looping = false;
  private OnCompletionListener onCompletion = null;
  private OnErrorListener onError = null;
  private OnInfoListener onInfo = null;
  private ChainedMediaPlayer.OnInfo[] onInfos = null;
  private OnPreparedListener onPrepared = null;
  private OnSeekCompleteListener onSeek = null;
  private OnVideoSizeChangedListener onVideoSize = null;
  private MediaPlayer[] players = null;
  private boolean playing = false;
  private MediaPlayer previous = null;
  private float rightVolume = 1.0F;
  private ChainedMediaPlayer.ISetData setData = null;
  private int startOffset = 0;
  private SurfaceHolder surfaceHolder = null;
  private OnBufferingUpdateListener updateBuffer = null;

  public ChainedMediaPlayer(Context var1) {
    this.players = new MediaPlayer[3];
    this.onInfos = new ChainedMediaPlayer.OnInfo[3];
    StringBuilder var3 = (new StringBuilder()).append("ChainedMediaPlayer");
    int var2 = id;
    id = var2 + 1;
    this.TAG = var3.append(var2).toString();
  }

  private void assurePlayers() throws IOException {
    for(int var1 = 0; var1 < 3; ++var1) {
      if(this.players[var1] == null) {
        this.players[var1] = new MediaPlayer();
        if(this.setData != null) {
          this.setData.setData(this, this.players[var1]);
        }
      }
    }

  }

  private void autoStartMe() {
    this.autoStart = true;
  }

  private void setCurrent(MediaPlayer var1) {
    synchronized(this){}

    try {
      this.previous = this.current;
      this.current = var1;
    } finally {
      ;
    }

  }

  @SuppressLint({"NewApi"})
  private void setupLooping(boolean var1) {
    try {
      this.assurePlayers();
    } catch (Throwable var4) {
      var4.printStackTrace();
    }

    int var3;
    if(var1) {
      for(var3 = 0; var3 < 3; ++var3) {
        new ChainedMediaPlayer.OnInfo(this, var3);
      }
    } else {
      for(var3 = 0; var3 < 3; ++var3) {
        this.players[var3].setNextMediaPlayer((MediaPlayer)null);
        this.players[var3].setOnInfoListener(this.onInfo);
      }
    }

  }

  public void attachAuxEffect(int var1) {
  }

  public void finalize() throws Throwable {
    try {
      this.stop();
    } catch (Throwable var2) {
      ;
    }

    super.finalize();
  }

  public int getAudioSessionId() {
    return 0;
  }

  public int getCurrentPosition() {
    try {
      if(this.current != null) {
        return this.current.getCurrentPosition();
      } else {
        throw new Exception("No current player");
      }
    } catch (Exception var4) {
      var4.printStackTrace();

      try {
        if(this.previous != null) {
          int var1 = this.previous.getCurrentPosition();
          return var1;
        }
      } catch (Exception var3) {
        var3.printStackTrace();
      }

      return 0;
    }
  }

  public int getDuration() {
    byte var4 = 0;
    int var3;
    if(this.current != null) {
      try {
        var3 = this.current.getDuration();
        return var3;
      } catch (Throwable var6) {
        ;
      }
    }

    int var2 = 0;

    while(true) {
      var3 = var4;
      if(var2 >= 3) {
        return var3;
      }

      if(this.players[var2] != null && this.players[var2] != this.current) {
        try {
          var3 = this.players[0].getDuration();
          return var3;
        } catch (Throwable var5) {
          ;
        }
      }

      ++var2;
    }
  }

  public int getVideoHeight() {
    return this.current != null?this.current.getVideoHeight():0;
  }

  public int getVideoWidth() {
    return this.current != null?this.current.getVideoWidth():0;
  }

  public boolean isLooping() {
    return this.looping;
  }

  public boolean isPlaying() {
    for(int var1 = 0; var1 < 3; ++var1) {
      if(this.players[var1] != null && this.players[var1].isPlaying()) {
        return true;
      }
    }

    return false;
  }

  public void pause() throws IllegalStateException {
    synchronized(this){}

    try {
      if(this.isPlaying()) {
        this.startOffset = this.getCurrentPosition();
        if(this.startOffset >= this.current.getDuration()) {
          this.startOffset = 0;
        }

        this.stop();

        try {
          this.prepare();
        } catch (IOException var4) {
          throw new IllegalStateException(var4);
        }
      }
    } finally {
      ;
    }

  }

  public void prepare() throws IOException, IllegalStateException {
    this.assurePlayers();
    this.players[0].prepare();
    if(this.looping) {
      for(int var1 = 1; var1 < 3; ++var1) {
        this.players[var1].prepareAsync();
      }
    }

    this.isPrepared = true;
  }

  public void prepareAsync() throws IllegalStateException {
    try {
      this.assurePlayers();
    } catch (IOException var3) {
      throw new IllegalStateException(var3);
    }

    this.players[0].prepareAsync();
    if(this.looping) {
      for(int var1 = 1; var1 < 3; ++var1) {
        this.players[var1].prepareAsync();
      }
    }

  }

  public void release() {
    try {
      this.stop();
    } catch (Throwable var3) {
      ;
    }

    for(int var2 = 0; var2 < 3; ++var2) {
      if(this.players[var2] != null) {
        this.players[var2].release();
        this.players[var2] = null;
      }
    }

  }

  public void reset() {
    for(int var1 = 0; var1 < 3; ++var1) {
      if(this.players[var1] != null) {
        this.players[var1].reset();
      }
    }

  }

  public void seekTo(int var1) throws IllegalStateException {
    this.startOffset = var1;
    if(this.current != null) {
      this.current.seekTo(var1);
    }

  }

  public void setAudioSessionId(int var1) throws IllegalArgumentException, IllegalStateException {
  }

  public void setAudioStreamType(int var1) {
    try {
      this.assurePlayers();
    } catch (Throwable var4) {
      ;
    }

    this.audioStreamType = var1;

    for(int var2 = 0; var2 < 3; ++var2) {
      this.players[var2].setAudioStreamType(var1);
    }

  }

  public void setAuxEffectSendLevel(float var1) {
  }

  public void setDataSource(final Context var1, final Uri var2) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
    this.setData = new ChainedMediaPlayer.ISetData() {
      public void setData(ChainedMediaPlayer var1x, MediaPlayer var2x) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        var2x.setDataSource(var1, var2);
        this.initPlayer(var1x, var2x);
      }
    };

    for(int var3 = 0; var3 < 3; ++var3) {
      if(this.players[var3] != null) {
        this.setData.setData(this, this.players[var3]);
      }
    }

  }

  public void setDataSource(final FileDescriptor var1) throws IOException, IllegalArgumentException, IllegalStateException {
    this.setData = new ChainedMediaPlayer.ISetData() {
      public void setData(ChainedMediaPlayer var1x, MediaPlayer var2) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        var2.setDataSource(var1);
        this.initPlayer(var1x, var2);
      }
    };

    for(int var2 = 0; var2 < 3; ++var2) {
      if(this.players[var2] != null) {
        this.setData.setData(this, this.players[var2]);
      }
    }

  }

  public void setDataSource(final FileDescriptor var1, final long var2, final long var4) throws IOException, IllegalArgumentException, IllegalStateException {
    this.setData = new ChainedMediaPlayer.ISetData() {
      public void setData(ChainedMediaPlayer var1x, MediaPlayer var2x) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        var2x.setDataSource(var1, var2, var4);
        this.initPlayer(var1x, var2x);
      }
    };

    for(int var6 = 0; var6 < 3; ++var6) {
      if(this.players[var6] != null) {
        this.setData.setData(this, this.players[var6]);
      }
    }

  }

  public void setDataSource(final String var1) throws IOException, IllegalArgumentException, IllegalStateException {
    this.setData = new ChainedMediaPlayer.ISetData() {
      public void setData(ChainedMediaPlayer var1x, MediaPlayer var2) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        var2.setDataSource(var1);
        this.initPlayer(var1x, var2);
      }
    };

    for(int var2 = 0; var2 < 3; ++var2) {
      if(this.players[var2] != null) {
        this.setData.setData(this, this.players[var2]);
      }
    }

  }

  public void setDisplay(SurfaceHolder var1) {
    try {
      this.assurePlayers();
    } catch (Throwable var4) {
      var4.printStackTrace();
    }

    this.surfaceHolder = var1;

    for(int var2 = 0; var2 < 3; ++var2) {
      if(this.players[var2] != null) {
        this.players[var2].setDisplay(var1);
      }
    }

  }

  public void setLooping(boolean var1) {
    if(this.isPrepared) {
      throw new RuntimeException("setLooping() cannot be called after ChainedMediaPlayer has already been prepared.");
    } else {
      this.setupLooping(var1);
      this.looping = var1;
    }
  }

  public void setOnBufferingUpdateListener(com.nativemediaplayer.IMediaPlayer.OnBufferingUpdateListener var1) {
    this.updateBuffer = new ChainedMediaPlayer.MediaPlayerOnBufferingUpdateListener(this, var1, null);

    try {
      this.assurePlayers();
    } catch (Throwable var3) {
      var3.printStackTrace();
    }

    for(int var2 = 0; var2 < 3; ++var2) {
      if(this.players[var2] != null) {
        this.players[var2].setOnBufferingUpdateListener(this.updateBuffer);
      }
    }

  }

  public void setOnCompletionListener(com.nativemediaplayer.IMediaPlayer.OnCompletionListener var1) {
    this.onCompletion = new ChainedMediaPlayer.MediaPlayerOnCompletionListener(this, var1, null);

    try {
      this.assurePlayers();
    } catch (Throwable var3) {
      var3.printStackTrace();
    }

    for(int var2 = 0; var2 < 3; ++var2) {
      if(this.players[var2] != null) {
        this.players[var2].setOnCompletionListener(this.onCompletion);
      }
    }

  }

  public void setOnErrorListener(com.nativemediaplayer.IMediaPlayer.OnErrorListener var1) {
    this.onError = new ChainedMediaPlayer.MediaPlayerOnErrorListener(this, var1, null);

    try {
      this.assurePlayers();
    } catch (Throwable var3) {
      var3.printStackTrace();
    }

    for(int var2 = 0; var2 < 3; ++var2) {
      if(this.players[var2] != null) {
        this.players[var2].setOnErrorListener(this.onError);
      }
    }

  }

  public void setOnInfoListener(com.nativemediaplayer.IMediaPlayer.OnInfoListener var1) {
    this.onInfo = new ChainedMediaPlayer.MediaPlayerOnInfoListener(this, var1, null);

    try {
      this.assurePlayers();
    } catch (Throwable var3) {
      var3.printStackTrace();
    }

    for(int var2 = 0; var2 < 3; ++var2) {
      if(this.looping) {
        new ChainedMediaPlayer.OnInfo(this, var2);
      } else {
        this.players[var2].setOnInfoListener(this.onInfo);
        this.players[var2].setOnPreparedListener(this.onPrepared);
      }
    }

  }

  public void setOnPreparedListener(com.nativemediaplayer.IMediaPlayer.OnPreparedListener var1) {
    this.onPrepared = new ChainedMediaPlayer.MediaPlayerOnPreparedListener(this, var1, null);

    try {
      this.assurePlayers();
    } catch (Throwable var3) {
      var3.printStackTrace();
    }

    for(int var2 = 0; var2 < 3; ++var2) {
      if(this.looping) {
        new ChainedMediaPlayer.OnInfo(this, var2);
      } else {
        this.players[var2].setOnInfoListener(this.onInfo);
        this.players[var2].setOnPreparedListener(this.onPrepared);
      }
    }

  }

  public void setOnSeekCompleteListener(com.nativemediaplayer.IMediaPlayer.OnSeekCompleteListener var1) {
    this.onSeek = new ChainedMediaPlayer.MediaPlayerOnSeekCompleteListener(this, var1, null);

    try {
      this.assurePlayers();
    } catch (Throwable var3) {
      var3.printStackTrace();
    }

    for(int var2 = 0; var2 < 3; ++var2) {
      if(this.players[var2] != null) {
        this.players[var2].setOnSeekCompleteListener(this.onSeek);
      }
    }

  }

  public void setOnVideoSizeChangedListener(com.nativemediaplayer.IMediaPlayer.OnVideoSizeChangedListener var1) {
    this.onVideoSize = new ChainedMediaPlayer.MediaPlayerOnVideoSizeChangedListener(this, var1, null);

    try {
      this.assurePlayers();
    } catch (Throwable var3) {
      var3.printStackTrace();
    }

    for(int var2 = 0; var2 < 3; ++var2) {
      if(this.players[var2] != null) {
        this.players[var2].setOnVideoSizeChangedListener(this.onVideoSize);
      }
    }

  }

  public void setScreenOnWhilePlaying(boolean var1) {
    for(int var2 = 0; var2 < 3; ++var2) {
      if(this.players[var2] != null) {
        this.players[var2].setScreenOnWhilePlaying(var1);
      }
    }

  }

  public void setVolume(float var1, float var2) {
    this.leftVolume = var1;
    this.rightVolume = var2;

    for(int var3 = 0; var3 < 3; ++var3) {
      if(this.players[var3] != null) {
        this.players[var3].setVolume(var1, var2);
      }
    }

  }

  public void setWakeMode(Context var1, int var2) {
    for(int var3 = 0; var3 < 3; ++var3) {
      if(this.players[var3] != null) {
        this.players[var3].setWakeMode(var1, var2);
      }
    }

  }

  public void start() throws IllegalStateException {
    synchronized(this){}

    try {
      this.playing = true;
      if(this.current != null) {
        this.current.start();
      } else {
        this.setupLooping(this.looping);
        this.players[0].seekTo(this.startOffset);
        this.players[0].start();
        this.setCurrent(this.players[0]);
      }
    } finally {
      ;
    }

  }

  public void stop() throws IllegalStateException {
    this.playing = false;

    for(int var1 = 0; var1 < 3; ++var1) {
      if(this.players[var1] != null) {
        this.players[var1].stop();
        this.players[var1].release();
      }

      this.players[var1] = null;
    }

    this.setCurrent((MediaPlayer)null);
  }

  public static class ISetData {
    public ISetData() {
    }

    public void initPlayer(ChainedMediaPlayer var1, MediaPlayer var2) {
      var2.setVolume(var1.leftVolume, var1.rightVolume);
      var2.setAudioStreamType(var1.audioStreamType);
      var2.setDisplay(var1.surfaceHolder);
      var2.setOnBufferingUpdateListener(var1.updateBuffer);
      var2.setOnCompletionListener(var1.onCompletion);
      var2.setOnErrorListener(var1.onError);
      var2.setOnSeekCompleteListener(var1.onSeek);
      var2.setOnVideoSizeChangedListener(var1.onVideoSize);
      if(var1.looping) {
        for(int var3 = 0; var3 < var1.players.length; ++var3) {
          if(var1.players[var3] == var2) {
            new ChainedMediaPlayer.OnInfo(var1, var3);
          }
        }
      } else {
        var2.setOnInfoListener(var1.onInfo);
        var2.setOnPreparedListener(var1.onPrepared);
      }

    }

    public void setData(ChainedMediaPlayer var1, MediaPlayer var2) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
    }
  }

  private static final class MediaPlayerOnBufferingUpdateListener implements OnBufferingUpdateListener {
    private final WeakReference<ChainedMediaPlayer> ref;
    private final com.nativemediaplayer.IMediaPlayer.OnBufferingUpdateListener target;

    private MediaPlayerOnBufferingUpdateListener(ChainedMediaPlayer var1, com.nativemediaplayer.IMediaPlayer.OnBufferingUpdateListener var2) {
      this.ref = new WeakReference(var1);
      this.target = var2;
    }

    public void onBufferingUpdate(MediaPlayer var1, int var2) {
      ChainedMediaPlayer var3 = (ChainedMediaPlayer)this.ref.get();
      if(this.target != null && var3 != null) {
        this.target.onBufferingUpdate(var3, var2);
      }

    }
  }

  private static final class MediaPlayerOnCompletionListener implements OnCompletionListener {
    private final WeakReference<ChainedMediaPlayer> ref;
    private final com.nativemediaplayer.IMediaPlayer.OnCompletionListener target;

    private MediaPlayerOnCompletionListener(ChainedMediaPlayer var1, com.nativemediaplayer.IMediaPlayer.OnCompletionListener var2) {
      this.ref = new WeakReference(var1);
      this.target = var2;
    }

    public void onCompletion(MediaPlayer var1) {
      ChainedMediaPlayer var2 = (ChainedMediaPlayer)this.ref.get();
      if(this.target != null && var2 != null) {
        this.target.onCompletion(var2);
      }

    }
  }

  private static final class MediaPlayerOnErrorListener implements OnErrorListener {
    private final WeakReference<ChainedMediaPlayer> ref;
    private final com.nativemediaplayer.IMediaPlayer.OnErrorListener target;

    private MediaPlayerOnErrorListener(ChainedMediaPlayer var1, com.nativemediaplayer.IMediaPlayer.OnErrorListener var2) {
      this.ref = new WeakReference(var1);
      this.target = var2;
    }

    public boolean onError(MediaPlayer var1, int var2, int var3) {
      ChainedMediaPlayer var4 = (ChainedMediaPlayer)this.ref.get();
      if(var4 != null) {
        if(var2 != 100) {
          if(this.target != null && var4 != null) {
            return this.target.onError(var4, var2, var3);
          }

          return false;
        }

        for(var2 = 0; var2 < var4.players.length; ++var2) {
          if(var4.players[var2] == var1) {
            var4.players[var2].release();
            var4.players[var2] = null;
            if(var4.looping) {
              var4.setCurrent((MediaPlayer)null);
              var3 = var4.onInfos[var2].getNext();
              var4.onInfos[var2] = null;

              try {
                var4.assurePlayers();
              } catch (Throwable var6) {
                var6.printStackTrace();
              }

              var1 = var4.players[var3];
              ChainedMediaPlayer.OnInfo var5 = var4.onInfos[var3];
              var4.setCurrent(var1);
              if(var4.playing && !var4.isPlaying()) {
                if(var5.isPrepared()) {
                  var1.start();
                } else {
                  var4.onInfos[var3].autoStartMe();
                }
              }

              var4.players[var2].prepareAsync();
              return true;
            }

            try {
              var4.assurePlayers();
            } catch (Throwable var7) {
              var7.printStackTrace();
            }

            if(var4.playing) {
              var4.autoStartMe();
            }

            var4.players[var2].prepareAsync();
            return true;
          }
        }
      }

      return true;
    }
  }

  private static final class MediaPlayerOnInfoListener implements OnInfoListener {
    private final WeakReference<ChainedMediaPlayer> ref;
    private final com.nativemediaplayer.IMediaPlayer.OnInfoListener target;

    private MediaPlayerOnInfoListener(ChainedMediaPlayer var1, com.nativemediaplayer.IMediaPlayer.OnInfoListener var2) {
      this.ref = new WeakReference(var1);
      this.target = var2;
    }

    public boolean onInfo(MediaPlayer var1, int var2, int var3) {
      ChainedMediaPlayer var4 = (ChainedMediaPlayer)this.ref.get();
      return var4 != null?this.target.onInfo(var4, var2, var3):true;
    }
  }

  private static final class MediaPlayerOnPreparedListener implements OnPreparedListener {
    private final WeakReference<ChainedMediaPlayer> ref;
    private final com.nativemediaplayer.IMediaPlayer.OnPreparedListener target;

    private MediaPlayerOnPreparedListener(ChainedMediaPlayer var1, com.nativemediaplayer.IMediaPlayer.OnPreparedListener var2) {
      this.ref = new WeakReference(var1);
      this.target = var2;
    }

    public void onPrepared(MediaPlayer var1) {
      ChainedMediaPlayer var2 = (ChainedMediaPlayer)this.ref.get();
      if(var2 != null) {
        if(var2.autoStart) {
          var2.autoStart = false;
          var1.start();
        } else if(!var2.isPlaying()) {
          this.target.onPrepared(var2);
          return;
        }
      }

    }
  }

  private static final class MediaPlayerOnSeekCompleteListener implements OnSeekCompleteListener {
    private final WeakReference<ChainedMediaPlayer> ref;
    private final com.nativemediaplayer.IMediaPlayer.OnSeekCompleteListener target;

    private MediaPlayerOnSeekCompleteListener(ChainedMediaPlayer var1, com.nativemediaplayer.IMediaPlayer.OnSeekCompleteListener var2) {
      this.ref = new WeakReference(var1);
      this.target = var2;
    }

    public void onSeekComplete(MediaPlayer var1) {
      ChainedMediaPlayer var2 = (ChainedMediaPlayer)this.ref.get();
      if(var2 != null) {
        this.target.onSeekComplete(var2);
      }

    }
  }

  private static final class MediaPlayerOnVideoSizeChangedListener implements OnVideoSizeChangedListener {
    private final WeakReference<ChainedMediaPlayer> ref;
    private final com.nativemediaplayer.IMediaPlayer.OnVideoSizeChangedListener target;

    private MediaPlayerOnVideoSizeChangedListener(ChainedMediaPlayer var1, com.nativemediaplayer.IMediaPlayer.OnVideoSizeChangedListener var2) {
      this.ref = new WeakReference(var1);
      this.target = var2;
    }

    public void onVideoSizeChanged(MediaPlayer var1, int var2, int var3) {
      ChainedMediaPlayer var4 = (ChainedMediaPlayer)this.ref.get();
      if(this.target != null && var4 != null) {
        this.target.onVideoSizeChanged(var4, var2, var3);
      }

    }
  }

  static class OnInfo implements OnInfoListener, OnPreparedListener {
    private static final int MEDIA_INFO_STARTED_AS_NEXT = 2;
    private boolean autoStart;
    private int index;
    private boolean isPrepared = false;
    private int next;
    private int prev;
    private WeakReference<ChainedMediaPlayer> ref;

    @SuppressLint({"NewApi"})
    public OnInfo(ChainedMediaPlayer var1, int var2) {
      this.ref = new WeakReference(var1);
      this.index = var2;
      this.prev = (var2 + 3 - 1) % 3;
      this.next = (var2 + 1) % 3;
      MediaPlayer var3 = var1.players[var2];
      var3.setOnInfoListener(this);
      var3.setOnPreparedListener(this);
      var1.onInfos[var2] = this;
    }

    public void autoStartMe() {
      ChainedMediaPlayer var1 = (ChainedMediaPlayer)this.ref.get();
      if(var1 != null) {
        var1.autoStart = true;
      }

    }

    public MediaPlayer getCurrent() {
      ChainedMediaPlayer var1 = (ChainedMediaPlayer)this.ref.get();
      return var1 != null?var1.players[this.index]:null;
    }

    public int getNext() {
      return this.next;
    }

    public MediaPlayer getPrevious() {
      ChainedMediaPlayer var1 = (ChainedMediaPlayer)this.ref.get();
      return var1 != null?var1.players[this.prev]:null;
    }

    public boolean isPrepared() {
      return this.isPrepared;
    }

    @SuppressLint({"NewApi"})
    public boolean onInfo(MediaPlayer var1, int var2, int var3) {
      ChainedMediaPlayer var6 = (ChainedMediaPlayer)this.ref.get();
      if(var6 != null) {
        if(var2 == 2) {
          MediaPlayer var5 = this.getCurrent();
          MediaPlayer var4 = var5;
          if(var1 != var5) {
            var4 = var1;
          }

          var6.setCurrent(var4);
          var1 = this.getPrevious();
          var1.reset();
          var1.release();
          MediaPlayer[] var9 = var6.players;
          var2 = this.prev;
          var1 = new MediaPlayer();
          var9[var2] = var1;

          try {
            var6.setData.setData(var6, var1);
          } catch (Throwable var8) {
            ;
          }

          try {
            var1.prepareAsync();
          } catch (Throwable var7) {
            var7.printStackTrace();
          }

          return true;
        }

        if(var6.onInfo != null) {
          var6.onInfo.onInfo(var1, var2, var3);
        }
      } else {
        var1.stop();
        var1.release();
      }

      return false;
    }

    @SuppressLint({"NewApi"})
    public void onPrepared(MediaPlayer var1) {
      ChainedMediaPlayer var2 = (ChainedMediaPlayer)this.ref.get();
      if(var2 != null) {
        this.isPrepared = true;
        if(this.autoStart) {
          var1.start();
          this.autoStart = false;
        } else {
          if(var2.looping) {
            MediaPlayer var3 = this.getPrevious();
            if(var3 != null && !var1.isPlaying()) {
              try {
                var3.setNextMediaPlayer(var1);
              } catch (Throwable var4) {
                var4.printStackTrace();
              }
            }
          }

          if(var2.onPrepared != null) {
            var2.onPrepared.onPrepared(var1);
            return;
          }
        }

      } else {
        var1.stop();
        var1.release();
      }
    }
  }
}
