package ipnossoft.rma.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.google.android.gms.analytics.CampaignTrackingReceiver;

public class ReferrerReceiver extends BroadcastReceiver {
  public ReferrerReceiver() {
  }

  public void onReceive(Context var1, Intent var2) {
    String var3 = var2.getExtras().getString("referrer");
    if(var3 != null) {
      Analytics.attributeInstall(var3, var1);
    }

    (new CampaignTrackingReceiver()).onReceive(var1, var2);
  }
}
