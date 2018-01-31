package ipnossoft.rma;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class KillBackgroundSoundNotificationService extends Service
{

  private final IBinder mBinder = new KillBinder(this);
  private NotificationManager notificationManager;

  public class KillBinder extends Binder
  {
    public final Service service;

    public KillBinder(Service service1)
    {
      super();
      service = service1;
    }
  }

  public KillBackgroundSoundNotificationService()
  {

  }

  public IBinder onBind(Intent intent)
  {
    return mBinder;
  }

  public void onCreate()
  {
    notificationManager = (NotificationManager)getSystemService("notification");
    notificationManager.cancel(RelaxMelodiesApp.NOTIFICATION_BACKGROUND_SOUND_ID);
  }

  public int onStartCommand(Intent intent, int i, int j)
  {
    return START_STICKY;
  }
}
