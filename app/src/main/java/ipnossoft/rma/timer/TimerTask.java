package ipnossoft.rma.timer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import ipnossoft.rma.AppState;
import ipnossoft.rma.AudioFocusManager;
import ipnossoft.rma.media.SoundManager;
import ipnossoft.rma.media.SoundService;
import ipnossoft.rma.util.RelaxAnalytics;
import ipnossoft.rma.util.TimeUtils;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;

// Referenced classes of package ipnossoft.rma.timer:
//            QuittingActivity, TimerListener


//cavaj
public class TimerTask extends AsyncTask
{

  private static final long FADEOUT_DURATION = 30000L;
  private Activity activity;
  private final long duration;
  private volatile boolean isInterrupted;
  private ArrayList listeners;
  private final SoundManager soundManager;
  private boolean startFadeout;
  private final long startTime = System.currentTimeMillis();
  private boolean stopApp;

  public TimerTask(long l, SoundManager soundmanager, boolean flag, Activity activity1)
  {
    duration = l;
    soundManager = soundmanager;
    isInterrupted = false;
    startFadeout = false;
    stopApp = flag;
    activity = activity1;
    listeners = new ArrayList();
  }

  private void gracefullyStopApp()
  {
    Context context;
    if(activity == null)
      return;
    try {
      AudioFocusManager.cancelAudioFocus();
      context = activity.getApplicationContext();
      if (android.os.Build.VERSION.SDK_INT < 16) {
        activity.finish();
      } else {
        activity.finishAffinity();
      }
      Intent intent = new Intent(context, QuittingActivity.class);
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
      context.startActivity(intent);
      SoundService.getInstance().removeNotification();
    }catch (Exception e){
      System.exit(0);
    }
  }

  public void addListener(TimerListener timerlistener)
  {
    Iterator iterator = listeners.iterator();
    do
    {
      if(!iterator.hasNext())
      {
        break;
      }
      WeakReference weakreference = (WeakReference)iterator.next();
      if(weakreference != null && weakreference.get() != null)
      {
        if(((TimerListener)weakreference.get()).getClass() == timerlistener.getClass())
        {
          weakreference.clear();
          iterator.remove();
        }
      } else
      {
        iterator.remove();
      }
    } while(true);
    listeners.add(new WeakReference(timerlistener));
  }

  public long computeRemaining()
  {
    return duration - (System.currentTimeMillis() - startTime);
  }

  protected Object doInBackground(Object aobj[])
  {
    return doInBackground((Void[])aobj);
  }

  protected Void doInBackground(Void avoid[])
  {
    AppState.setTimerRunning(true);
    long l;
    do {
      l = computeRemaining();
      publishProgress(new String[]{
              TimeUtils.convertMillisecondsToString(l)
      });
      if (l >= 30000L) {
        isInterrupted = true;
        return null;
      }
      if (!startFadeout) {
        soundManager.startFadeout();
        startFadeout = true;
      }
      soundManager.setFadedVolume((float) l / 30000F);
      try {
        Thread.sleep(100L);
      } catch (Exception e) {
        e.printStackTrace();
        break;
      }
    }while(l > 0L);
    activity.runOnUiThread(new Runnable(){
      public void run()
      {
        soundManager.forcePauseAll();
        soundManager.stopFadeout();
      }
    });
    return null;
  }

  public boolean isFinished()
  {
    return getStatus() == android.os.AsyncTask.Status.FINISHED || isCancelled();
  }

  public boolean isStopApp()
  {
    return stopApp;
  }

  protected void onPostExecute(Object obj)
  {
    onPostExecute((Void)obj);
  }

  protected void onPostExecute(Void void1)
  {
    AppState.setTimerRunning(false);
    Iterator void1_1 = listeners.iterator();
    do
    {
      if(!void1_1.hasNext())
      {
        break;
      }
      WeakReference weakreference = (WeakReference)void1_1.next();
      if(weakreference != null && weakreference.get() != null)
      {
        ((TimerListener)weakreference.get()).onTimerComplete(stopApp);
      }
    } while(true);
    RelaxAnalytics.logTimerComplete();
    SoundManager.getInstance().pauseAll(false);
    if(stopApp)
    {
      gracefullyStopApp();
    }
  }

  protected void onProgressUpdate(Object aobj[])
  {
    onProgressUpdate((String[])aobj);
  }

  protected void onProgressUpdate(String as[])
  {
    Iterator iterator = listeners.iterator();
    do
    {
      if(!iterator.hasNext())
      {
        break;
      }
      WeakReference weakreference = (WeakReference)iterator.next();
      if(weakreference != null && weakreference.get() != null)
      {
        ((TimerListener)weakreference.get()).onTimerUpdate(as[0]);
      }
    } while(true);
  }

  public void setStopApp(boolean flag)
  {
    stopApp = flag;
  }

  public void waitIsInterrupted()
  {
    do
    {
      if(isInterrupted)
      {
        break;
      }
      try
      {
        Thread.sleep(5L);
        continue;
      }
      catch(InterruptedException interruptedexception) { }
      break;
    } while(true);
  }




}
