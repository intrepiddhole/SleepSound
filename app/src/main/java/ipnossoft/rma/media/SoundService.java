package ipnossoft.rma.media;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.AsyncTask.Status;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat.Action;
import android.support.v4.app.NotificationCompat.Builder;
import com.ipnossoft.api.featuremanager.FeatureManager;
import ipnossoft.rma.RelaxMelodiesApp;
import ipnossoft.rma.free.R;
import ipnossoft.rma.util.Utils;

public class SoundService extends Service {
  public static final String CUSTOM_NOTIFICATION_INTENT = "RELAX_MELODIES_CUSTOM_NOTIF_INTENT";
  private static final int NOTIFICATION_ID = 1;
  private static SoundService instance;
  private final IBinder binder = new SoundService.SoundMediaPlayerServiceBinder();
  private boolean forceUnbind = false;
  private boolean isNotificationShown = false;
  private SoundService.StopTask stopTask;

  public SoundService() {
  }

  private Notification buildNotification(Activity var1, PendingIntent var2, SoundTrack var3, int var4, Bitmap var5, Action var6) {
    Builder var9 = (new android.support.v7.app.NotificationCompat.Builder(var1)).setSmallIcon(R.drawable.ic_stat_notify).setLargeIcon(var5).setContentIntent(var2);
    String var7;
    if(var3 != null) {
      var7 = var3.getSound().getName();
    } else {
      var7 = this.getString(R.string.app_name);
    }

    Builder var8 = var9.setContentTitle(var7);
    if(var4 > 0) {
      var7 = String.format(var1.getResources().getString(R.string.notification_sounds_selected), new Object[]{Integer.valueOf(var4)});
    } else {
      var7 = this.getString(R.string.notification_no_sounds_selected);
    }

    return var8.setContentText(var7).setStyle((new MediaStyle()).setShowActionsInCompactView(new int[]{0, 1})).setWhen(0L).addAction(var6).addAction(this.onStopAction(var1)).build();
  }

  private PendingIntent getContentPendingIntent(Activity var1) {
    Intent var2 = new Intent(var1, RelaxMelodiesApp.getInstance().getMainActivityClass());
    var2.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
    return PendingIntent.getActivity(var1, 0, var2, PendingIntent.FLAG_UPDATE_CURRENT);
  }

  public static SoundService getInstance() {
    if(instance == null) {
      instance = new SoundService();
    }

    return instance;
  }

  private int getNbPlayingSounds(SoundTrack var1) {
    int var3 = SoundManager.getInstance().getNbSelectedSounds();
    int var2 = var3;
    if(var3 > 0) {
      var2 = var3;
      if(var1 != null) {
        var2 = var3 - 1;
      }
    }

    return var2;
  }

  @NonNull
  private Action getUserAction(Activity var1) {
    return SoundManager.getInstance().isPlaying()?this.onPauseAction(var1):this.onPlayAction(var1);
  }

  private void launchStopTask() {
    if(this.stopTask != null) {
      this.stopTask.cancelTask();
    }

    this.stopTask = new SoundService.StopTask();
    Utils.executeTask(this.stopTask, new Void[0]);
  }

  @NonNull
  private Action onPauseAction(Activity var1) {
    Intent var2 = new Intent();
    var2.setAction("RELAX_MELODIES_CUSTOM_NOTIF_INTENT");
    Bundle var3 = new Bundle();
    var3.putString("action", "pause");
    var2.putExtras(var3);
    PendingIntent var4 = PendingIntent.getBroadcast(var1, 0, var2, PendingIntent.FLAG_UPDATE_CURRENT);
    return new Action(R.drawable.cn_pause, "", var4);
  }

  @NonNull
  private Action onPlayAction(Activity var1) {
    Intent var2 = new Intent();
    var2.setAction("RELAX_MELODIES_CUSTOM_NOTIF_INTENT");
    Bundle var3 = new Bundle();
    var3.putString("action", "play");
    var2.putExtras(var3);
    PendingIntent var4 = PendingIntent.getBroadcast(var1, 0, var2, PendingIntent.FLAG_UPDATE_CURRENT);
    return new Action(R.drawable.cn_play, "", var4);
  }

  private Action onStopAction(Activity var1) {
    Intent var2 = new Intent();
    var2.setAction("RELAX_MELODIES_CUSTOM_NOTIF_INTENT");
    Bundle var3 = new Bundle();
    var3.putString("action", "stop");
    var2.putExtras(var3);
    PendingIntent var4 = PendingIntent.getBroadcast(var1, 1, var2, PendingIntent.FLAG_UPDATE_CURRENT);
    return new Action(R.drawable.cn_clear, "", var4);
  }

  public static void setInstance(SoundService var0) {
    instance = var0;
  }

  @Nullable
  public IBinder onBind(Intent var1) {
    return this.binder;
  }

  public void onRebind(Intent var1) {
    if(this.stopTask != null) {
      this.stopTask.cancelTask();
    }

    super.onRebind(var1);
  }

  public int onStartCommand(Intent var1, int var2, int var3) {
    return Service.START_NOT_STICKY;
  }

  public boolean onUnbind(Intent var1) {
    if(SoundManager.getInstance().getNbSelectedSounds() == 0 || SoundManager.getInstance().isPaused() || !RelaxMelodiesApp.isPremium().booleanValue() && !FeatureManager.getInstance().hasActiveSubscription() || this.forceUnbind) {
      this.stopService();
    }

    return super.onUnbind(var1);
  }

  public void pause(SoundTrack var1) throws Exception {
    var1.pause();
  }

  public void play(SoundTrack var1) throws Exception {
    var1.play();
  }

  public void removeNotification() {
    if(this.isNotificationShown) {
      this.stopForeground(true);
    }

    this.isNotificationShown = false;
  }

  public void showNotification(Activity var1) {
    PendingIntent var2 = this.getContentPendingIntent(var1);
    SoundTrack var3 = SoundManager.getInstance().selectedGuidedMeditation();
    int var6 = this.getNbPlayingSounds(var3);
    Resources var4 = var1.getResources();
    int var5;
    if(RelaxMelodiesApp.isFreeVersion()) {
      var5 = R.drawable.rm_icon_square;
    } else {
      var5 = R.drawable.rmp_icon_square;
    }

    this.startForeground(1, this.buildNotification(var1, var2, var3, var6, BitmapFactory.decodeResource(var4, var5), this.getUserAction(var1)));
    this.isNotificationShown = true;
  }

  public void stop(SoundTrack var1) {
    var1.stop();
  }

  public void stopService() {
    this.launchStopTask();
    SoundManager.getInstance().pauseAll(true);
  }

  public class SoundMediaPlayerServiceBinder extends Binder {
    public SoundMediaPlayerServiceBinder() {
    }

    SoundService getService() {
      return SoundService.this;
    }
  }

  private class StopTask extends AsyncTask<Void, Void, Boolean> {
    volatile boolean cancelled;

    private StopTask() {
    }

    void cancelTask() {
      this.cancelled = true;
      if(this.getStatus() != Status.FINISHED) {
        this.cancel(true);
      }

    }

    protected Boolean doInBackground(Void... var1) {
      try {
        Thread.sleep(100L);
      } catch (InterruptedException var2) {
        return Boolean.valueOf(false);
      }

      return Boolean.valueOf(true);
    }

    protected void onPostExecute(Boolean var1) {
      if(var1.booleanValue() && !this.cancelled) {
        SoundService.this.stopSelf();
      }

    }
  }
}
