package ipnossoft.rma.media;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CustomSoundServiceNotificationBroadcastReceiver extends BroadcastReceiver {
  public CustomSoundServiceNotificationBroadcastReceiver() {
  }

  public void onReceive(Context var1, Intent var2) {
    String var3 = var2.getExtras().getString("action");
    if("pause".equals(var3)) {
      SoundManager.getInstance().pauseAll(true);
    } else {
      if("play".equals(var3)) {
        SoundManager.getInstance().playAll();
        return;
      }

      if("stop".equals(var3)) {
        SoundService.getInstance().stopService();
        SoundService.getInstance().removeNotification();
        return;
      }
    }

  }
}
