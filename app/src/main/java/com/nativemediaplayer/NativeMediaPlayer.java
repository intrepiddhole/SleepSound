package com.nativemediaplayer;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.*;
import android.view.SurfaceHolder;
import java.io.FileDescriptor;
import java.io.IOException;
import java.lang.ref.WeakReference;

//cavaj
public class NativeMediaPlayer implements IMediaPlayer
{
  private class EventHandler extends Handler
  {

    private NativeMediaPlayer mMediaPlayer;
    private String mNativeName;

    public void handleMessage(Message message)
    {
      if(mNativeName == null)
      {
        mNativeName = String.format("%d NativeMediaPlayer", new Object[] {
                Integer.valueOf(native_id())
        });
      }
      if(!(mMediaPlayer != null && mMediaPlayer.$playerObject != 0))
        return;
      switch(message.what)
      {
        default:
          return;

        case 200:
          return;

        case 1: // '\001'
          if(mOnPreparedListener != null)
          {
            mOnPreparedListener.onPrepared(mMediaPlayer);
            return;
          }
          break;

        case 2: // '\002'
          if(mOnCompletionListener != null)
          {
            mOnCompletionListener.onCompletion(mMediaPlayer);
            return;
          }
          break;

        case 3: // '\003'
          if(mOnBufferingUpdateListener != null)
          {
            mOnBufferingUpdateListener.onBufferingUpdate(mMediaPlayer, message.arg1);
            return;
          }
          break;

        case 4: // '\004'
          if(mOnSeekCompleteListener != null)
          {
            mOnSeekCompleteListener.onSeekComplete(mMediaPlayer);
            return;
          }
          break;

        case 100: // 'd'
          boolean flag = false;
          if(mOnErrorListener != null)
          {
            flag = mOnErrorListener.onError(mMediaPlayer, message.arg1, message.arg2);
          }
          if(mOnCompletionListener != null && !flag)
          {
            mOnCompletionListener.onCompletion(mMediaPlayer);
            return;
          }
          break;

        case 0: // '\0'
        case 5: // '\005'
          break;
      }
      if(mOnInfoListener == null)
        return;
      mOnInfoListener.onInfo(mMediaPlayer, message.arg1, message.arg2);
    }

    public void release()
    {
      mMediaPlayer = null;
      mNativeName = null;
    }

    public EventHandler(NativeMediaPlayer nativemediaplayer1, Looper looper)
    {
      super(looper);
      mNativeName = null;
      mMediaPlayer = nativemediaplayer1;
    }
  }


  private static final int MEDIA_BUFFERING_UPDATE = 3;
  private static final int MEDIA_ERROR = 100;
  private static final int MEDIA_INFO = 200;
  private static final int MEDIA_NOP = 0;
  private static final int MEDIA_PLAYBACK_COMPLETE = 2;
  private static final int MEDIA_PREPARED = 1;
  private static final int MEDIA_SEEK_COMPLETE = 4;
  private static final int MEDIA_SET_VIDEO_SIZE = 5;
  public static final String TAG = "NativeMediaPlayer";
  private int $playerObject;
  private int mAudioSessionId;
  private EventHandler mEventHandler;
  private IMediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener;
  private IMediaPlayer.OnCompletionListener mOnCompletionListener;
  private IMediaPlayer.OnErrorListener mOnErrorListener;
  private IMediaPlayer.OnInfoListener mOnInfoListener;
  private IMediaPlayer.OnPreparedListener mOnPreparedListener;
  private IMediaPlayer.OnSeekCompleteListener mOnSeekCompleteListener;

  public NativeMediaPlayer(Context context)
  {
    $playerObject = 0;
    try
    {
      Looper context_1 = Looper.myLooper();
      if(context_1 == null) {
        context_1 = Looper.getMainLooper();
        if(context_1 == null)
          mEventHandler = null;
        else
          mEventHandler = new EventHandler(this, context_1);
      } else
        mEventHandler = new EventHandler(this, context_1);
      native_setup(new WeakReference(this));
    }
    catch(Exception e)
    {
    }
  }

  private static void postEventFromNative(Object obj, int i, int j, int k, Object obj1)
  {
    if(obj != null)
    {
      if((obj = (NativeMediaPlayer)((WeakReference)obj).get()) != null && ((NativeMediaPlayer) (obj)).mEventHandler != null)
      {
        obj1 = ((NativeMediaPlayer) (obj)).mEventHandler.obtainMessage(i, j, k, obj1);
        ((NativeMediaPlayer) (obj)).mEventHandler.sendMessage(((Message) (obj1)));
        return;
      }
    }
  }

  public static native void shutdown();

  public static native void startup(Context context, boolean flag, boolean flag1, boolean flag2);

  public native void attachAuxEffect(int i);

  protected void finalize()
  {
    native_finalize();
  }

  public int getAudioSessionId()
  {
    return mAudioSessionId;
  }

  public native int getCurrentPosition();

  public native int getDuration();

  public int getVideoHeight()
  {
    throw new RuntimeException("Not implemented");
  }

  public int getVideoWidth()
  {
    throw new RuntimeException("Not implemented");
  }

  public native boolean isLooping();

  public native boolean isPlaying();

  public native void native_finalize();

  public native int native_id();

  public native void native_release();

  public native void native_setup(Object obj);

  public native void pause()
          throws IllegalStateException;

  public native void prepare()
          throws IOException, IllegalStateException;

  public void prepareAsync()
          throws IllegalStateException
  {
    try
    {
      prepare();
      return;
    }
    catch(IOException ioexception)
    {
      throw new IllegalStateException(ioexception);
    }
  }

  public void release()
  {
    native_release();
    if(mEventHandler != null)
    {
      mEventHandler.release();
    }
    mEventHandler = null;
    mOnBufferingUpdateListener = null;
    mOnCompletionListener = null;
    mOnErrorListener = null;
    mOnPreparedListener = null;
    mOnSeekCompleteListener = null;
    mAudioSessionId = -1;
    mOnInfoListener = null;
  }

  public native void reset();

  public native void seekTo(int i)
          throws IllegalStateException;

  public void setAudioSessionId(int i)
          throws IllegalArgumentException, IllegalStateException
  {
    mAudioSessionId = i;
  }

  public native void setAudioStreamType(int i);

  public native void setAuxEffectSendLevel(float f);

  public void setDataSource(Context context, Uri uri)
          throws IOException, IllegalArgumentException, SecurityException, IllegalStateException
  {
    if(uri.getScheme().equals("file"))
    {
      ParcelFileDescriptor context_1 = context.getContentResolver().openFileDescriptor(uri, "r");
      setDataSource(context_1.getFileDescriptor());
      context_1.close();
      return;
    }
    if(uri.getScheme().equals("android.resource"))
    {
      AssetFileDescriptor context_1 = context.getContentResolver().openAssetFileDescriptor(uri, "r");
      setDataSource(context_1.getFileDescriptor(), context_1.getStartOffset(), context_1.getLength());
      context_1.close();
    } else {
      setDataSource(uri.toString());
    }
  }

  public native void setDataSource(FileDescriptor filedescriptor)
          throws IOException, IllegalArgumentException, IllegalStateException;

  public native void setDataSource(FileDescriptor filedescriptor, long l, long l1)
          throws IOException, IllegalArgumentException, IllegalStateException;

  public native void setDataSource(String s)
          throws IOException, IllegalArgumentException, IllegalStateException;

  public void setDisplay(SurfaceHolder surfaceholder)
  {
    throw new RuntimeException("Not implemented");
  }

  public native void setLooping(boolean flag);

  public void setOnBufferingUpdateListener(IMediaPlayer.OnBufferingUpdateListener onbufferingupdatelistener)
  {
    mOnBufferingUpdateListener = onbufferingupdatelistener;
  }

  @Override
  public void setOnCompletionListener(Object var1) {
    setOnCompletionListener((IMediaPlayer.OnCompletionListener)var1);
  }

  public void setOnCompletionListener(IMediaPlayer.OnCompletionListener oncompletionlistener)
  {
    mOnCompletionListener = oncompletionlistener;
  }

  public void setOnErrorListener(IMediaPlayer.OnErrorListener onerrorlistener)
  {
    mOnErrorListener = onerrorlistener;
  }

  public void setOnInfoListener(IMediaPlayer.OnInfoListener oninfolistener)
  {
    throw new RuntimeException("Not implemented");
  }

  public void setOnPreparedListener(IMediaPlayer.OnPreparedListener onpreparedlistener)
  {
    mOnPreparedListener = onpreparedlistener;
  }

  public void setOnSeekCompleteListener(IMediaPlayer.OnSeekCompleteListener onseekcompletelistener)
  {
    mOnSeekCompleteListener = onseekcompletelistener;
  }

  public void setOnVideoSizeChangedListener(IMediaPlayer.OnVideoSizeChangedListener onvideosizechangedlistener)
  {
    throw new RuntimeException("Not implemented");
  }

  public void setScreenOnWhilePlaying(boolean flag)
  {
    throw new RuntimeException("Not implemented");
  }

  public native void setVolume(float f, float f1);

  public void setWakeMode(Context context, int i)
  {
    throw new RuntimeException("Not implemented");
  }

  public native void start()
          throws IllegalStateException;

  public native void stop()
          throws IllegalStateException;

  static
  {
    try
    {
      System.loadLibrary("JNIAudio");
    }
    catch(Exception exception) { }
  }







}
