package com.nativemediaplayer;

import android.content.Context;
import android.media.*;
import android.net.Uri;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;

//cavaj
public class BufferedAudioPlayer implements IMediaPlayer
{

  private static final String TAG = "BufferedAudioPlayer";
  private static final int TRACK_BUFFER_SIZE = 32768;
  private static final int TRACK_MODE = 1;
  private AudioTrack _AudioTrack;
  private MediaExtractor _MediaExtractor;
  private IMediaPlayer.OnCompletionListener completionListener;
  private IMediaPlayer.OnErrorListener errorListener;
  private int framesPerSample;
  private final Object prepareMonitor;
  private IMediaPlayer.OnPreparedListener preparedListener;
  private int sampleRate;
  private IMediaPlayer.OnSeekCompleteListener seekListener;
  private BufferedAudioInfo threadInfo;

  public BufferedAudioPlayer()
  {
    _AudioTrack = null;
    _MediaExtractor = new MediaExtractor();
    completionListener = null;
    errorListener = null;
    preparedListener = null;
    seekListener = null;
    framesPerSample = 4;
    sampleRate = 44100;
    threadInfo = new BufferedAudioInfo();
    prepareMonitor = new Object();
  }

  public BufferedAudioPlayer(Context context)
  {
    _AudioTrack = null;
    _MediaExtractor = new MediaExtractor();
    completionListener = null;
    errorListener = null;
    preparedListener = null;
    seekListener = null;
    framesPerSample = 4;
    sampleRate = 44100;
    threadInfo = new BufferedAudioInfo();
    prepareMonitor = new Object();
  }

  private static int getBytesPerSample(int i)
  {
    switch(i)
    {
      default:
        throw new IllegalArgumentException((new StringBuilder()).append("Bad audio format ").append(i).toString());

      case 3: // '\003'
        return 1;

      case 1: // '\001'
      case 2: // '\002'
        return 2;

      case 4: // '\004'
        return 4;
    }
  }

  private void initializeAll()
  {
    //empty
  }

  private boolean paused()
  {
    while(_AudioTrack == null || _AudioTrack.getPlayState() != 2)
    {
      return false;
    }
    return true;
  }

  private boolean playing()
  {
    while(_AudioTrack == null || _AudioTrack.getPlayState() != 3)
    {
      return false;
    }
    return true;
  }

  private void startThread()
  {
    (new Thread("BufferedAudioPlayer") {

      public void run()
      {
        initializeAll();
      }
    }).start();
  }

  public void attachAuxEffect(int i)
  {
  }

  public int getAudioSessionId()
  {
    return 0;
  }

  public int getCurrentPosition()
  {
    int i = _AudioTrack.getPlaybackHeadPosition();
    float f = framesPerSample * sampleRate;
    return (int)((float)i / f) * 1000;
  }

  public int getDuration()
  {
    return (int)threadInfo.duration() / 1000;
  }

  public int getVideoHeight()
  {
    return 0;
  }

  public int getVideoWidth()
  {
    return 0;
  }

  public boolean isLooping()
  {
    return threadInfo.looping();
  }

  public boolean isPlaying()
  {
    return playing();
  }

  public void pause()
          throws IllegalStateException
  {
    if(!threadInfo.prepared())
    {
      throw new IllegalStateException("Player has not been prepared");
    }
    if(!threadInfo.released())
    {
      _AudioTrack.pause();
    }
  }

  public void prepare()
          throws IOException, IllegalStateException
  {
    Log.d("BufferedAudioPlayer", "prepare() - setup listener");
    setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
      public void onPrepared(IMediaPlayer imediaplayer)
      {
        Log.d("BufferedAudioPlayer", "prepare() - callback");
        synchronized(prepareMonitor)
        {
          Log.d("BufferedAudioPlayer", "prepare() - notify monitor");
          prepareMonitor.notify();
        }
        return;
      }
    });
    Log.d("BufferedAudioPlayer", "prepare() - start thread");
    startThread();
    Object obj = prepareMonitor;
    synchronized (this){
      Log.d("BufferedAudioPlayer", "prepare() - break on .prepared() just in case");
      while (true){
        boolean flag = threadInfo.prepared();
        if(flag)
        {
          Log.e("BufferedAudioPlayer", "interrupt while waiting on synchronous prepare");
          continue;
        }
        Log.d("BufferedAudioPlayer", "prepare() - wait on monitor");
        try{
          prepareMonitor.wait();
        }catch (Throwable obj1){
          Log.e("BufferedAudioPlayer", "interrupt while waiting on synchronous prepare");
        }
      }
    }
  }

  public void prepareAsync()
          throws IllegalStateException
  {
    startThread();
  }

  public void release()
  {
    threadInfo.flagRelease(true);
    stop();
  }

  public void reset()
  {
    if(threadInfo.released())
    {
      threadInfo.flagRelease(false);
      _MediaExtractor = new MediaExtractor();
      threadInfo.released(false);
    }
  }

  public void seekTo(int i)
          throws IllegalStateException
  {
    if(!threadInfo.prepared())
    {
      throw new IllegalStateException();
    } else
    {
      threadInfo.dataSeekTo(i);
      threadInfo.flagSeek(true);
      return;
    }
  }

  public void setAudioSessionId(int i)
          throws IllegalArgumentException, IllegalStateException
  {
  }

  public void setAudioStreamType(int i)
  {
    if(threadInfo.prepared())
    {
      throw new IllegalStateException("Player already been prepared");
    } else
    {
      threadInfo.streamType(i);
      return;
    }
  }

  public void setAuxEffectSendLevel(float f)
  {
  }

  public void setDataSource(Context context, Uri uri)
          throws IOException, IllegalArgumentException, SecurityException, IllegalStateException
  {
    if(threadInfo.prepared())
    {
      throw new IllegalStateException("Player already been prepared");
    }
    if(uri == null)
    {
      throw new IllegalArgumentException("Uri is null");
    } else
    {
      _MediaExtractor.setDataSource(context, uri, new HashMap());
      return;
    }
  }

  public void setDataSource(FileDescriptor filedescriptor)
          throws IOException, IllegalArgumentException, IllegalStateException
  {
    if(threadInfo.prepared())
    {
      throw new IllegalStateException("Player already been prepared");
    }
    if(filedescriptor == null)
    {
      throw new IllegalArgumentException("FileDescriptor is null");
    } else
    {
      _MediaExtractor.setDataSource(filedescriptor);
      return;
    }
  }

  public void setDataSource(FileDescriptor filedescriptor, long l, long l1)
          throws IOException, IllegalArgumentException, IllegalStateException
  {
    if(threadInfo.prepared())
    {
      throw new IllegalStateException("Player already been prepared");
    }
    if(filedescriptor == null)
    {
      throw new IllegalArgumentException("FileDescriptor is null");
    } else
    {
      _MediaExtractor.setDataSource(filedescriptor, l, l1);
      return;
    }
  }

  public void setDataSource(String s)
          throws IOException, IllegalArgumentException, IllegalStateException
  {
    if(threadInfo.prepared())
    {
      throw new IllegalStateException("Player already been prepared");
    }
    if(TextUtils.isEmpty(s))
    {
      throw new IllegalArgumentException("Path is empty");
    } else
    {
      _MediaExtractor.setDataSource(s);
      return;
    }
  }

  public void setDisplay(SurfaceHolder surfaceholder)
  {
  }

  public void setLooping(boolean flag)
          throws IllegalStateException
  {
    threadInfo.looping(flag);
  }

  public void setOnBufferingUpdateListener(IMediaPlayer.OnBufferingUpdateListener onbufferingupdatelistener)
  {
    throw new UnsupportedOperationException("Unsupported");
  }

  @Override
  public void setOnCompletionListener(Object var1) {
    setOnCompletionListener((IMediaPlayer.OnCompletionListener)var1);
  }

  public void setOnCompletionListener(IMediaPlayer.OnCompletionListener oncompletionlistener)
  {
    completionListener = oncompletionlistener;
  }

  public void setOnErrorListener(IMediaPlayer.OnErrorListener onerrorlistener)
  {
    errorListener = onerrorlistener;
  }

  public void setOnInfoListener(IMediaPlayer.OnInfoListener oninfolistener)
  {
    throw new RuntimeException("Not implemented");
  }

  public void setOnPreparedListener(IMediaPlayer.OnPreparedListener onpreparedlistener)
  {
    preparedListener = onpreparedlistener;
  }

  public void setOnSeekCompleteListener(IMediaPlayer.OnSeekCompleteListener onseekcompletelistener)
  {
    seekListener = onseekcompletelistener;
  }

  public void setOnVideoSizeChangedListener(IMediaPlayer.OnVideoSizeChangedListener onvideosizechangedlistener)
  {
  }

  public void setScreenOnWhilePlaying(boolean flag)
  {
    throw new RuntimeException("Not implemented");
  }

  public void setVolume(float f, float f1)
  {
    threadInfo.dataLeftVolume(f);
    threadInfo.dataRightVolume(f1);
    threadInfo.flagUpdateVolume(true);
  }

  public void setWakeMode(Context context, int i)
  {
    ((PowerManager)context.getSystemService(Context.POWER_SERVICE)).newWakeLock(i, "wLock").acquire();
  }

  public void start()
          throws IllegalStateException
  {
    if(!threadInfo.prepared())
    {
      throw new IllegalStateException("Player has not been prepared");
    }
    if(!threadInfo.released())
    {
      _AudioTrack.play();
    }
  }

  public void stop()
          throws IllegalStateException
  {
    if(!threadInfo.prepared())
    {
      throw new IllegalStateException("Player has not been prepared");
    }
    threadInfo.flagStop(true);
    if(!threadInfo.released())
    {
      _AudioTrack.stop();
    }
  }


}
