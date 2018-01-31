package ipnossoft.rma.media;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import ipnossoft.rma.media.SoundService.SoundMediaPlayerServiceBinder;

public class SoundServiceConnection implements ServiceConnection {
  private SoundMediaPlayerServiceBinder localBinder;

  public SoundServiceConnection() {
  }

  public void onServiceConnected(ComponentName var1, IBinder var2) {
    this.localBinder = (SoundMediaPlayerServiceBinder)var2;
    SoundService.setInstance(this.localBinder.getService());
  }

  public void onServiceDisconnected(ComponentName var1) {
    this.localBinder.getService().removeNotification();
  }
}
