package ipnossoft.rma;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import com.ipnossoft.api.featuremanager.FeatureManager;
import com.ipnossoft.api.newsservice.NewsService;

public class ConnectivityChangeReceiver extends BroadcastReceiver {
  public ConnectivityChangeReceiver() {
  }

  public void onReceive(Context var1, Intent var2) {
    Log.d("app", "Network connectivity change");
    NetworkInfo var4 = ((ConnectivityManager)var1.getSystemService("connectivity")).getActiveNetworkInfo();
    boolean var3;
    if(var4 != null && var4.isConnectedOrConnecting()) {
      var3 = true;
    } else {
      var3 = false;
    }

    if(var3 && RelaxMelodiesApp.getInstance().isAppStarted()) {
      FeatureManager.getInstance().fetchAvailableFeatures();
      NewsService var5 = RelaxMelodiesApp.getInstance().getNewsService();
      if(var5 != null) {
        var5.fetchNews();
      }
    }

  }
}