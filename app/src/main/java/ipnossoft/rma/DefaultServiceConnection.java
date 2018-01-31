package ipnossoft.rma;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import ipnossoft.rma.media.SoundManager;

public class DefaultServiceConnection implements ServiceConnection {
  private static final String TAG = "DefaultServiceConnection";

  public DefaultServiceConnection() {
  }

  public void connect(Context var1) {
    var1.bindService(new Intent(var1, SoundManager.class), this, 0);
  }

  public void disconnect(Context var1) {
    var1.unbindService(this);
  }

  public void onServiceConnected(ComponentName var1, IBinder var2) {
  }

  public void onServiceDisconnected(ComponentName var1) {
  }
}