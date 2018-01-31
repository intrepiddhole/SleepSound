package ipnossoft.rma.media;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import ipnossoft.rma.RelaxMelodiesApp;

public class CustomHeadsetBroadcastReceiver extends BroadcastReceiver {
  public CustomHeadsetBroadcastReceiver() {
  }

  public void onReceive(Context var1, Intent var2) {
    if(var2.getAction().equals("android.intent.action.HEADSET_PLUG")) {
      RelaxMelodiesApp.getInstance().updateVolumeBar();
    } else {
      SoundManager.getInstance().pauseAll(true);
    }
  }
}
