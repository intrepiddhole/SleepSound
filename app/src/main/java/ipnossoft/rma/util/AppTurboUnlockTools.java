package ipnossoft.rma.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.util.Log;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.util.Iterator;
import org.json.JSONException;
import org.json.JSONObject;

public class AppTurboUnlockTools {
  private static final String APP_OF_THE_DAY_CONFIG_PATH = "http://cdn1.ipnoscloud.com/config/rma/config.json";

  public AppTurboUnlockTools() {
  }

  public static void isAppTurboFeaturingUs(Activity var0, final AppTurboIsFeaturingUsCallback var1) {
    if(isAppTurboUnlockable(var0)) {
      Volley.newRequestQueue(var0).add(new StringRequest(0, "http://cdn1.ipnoscloud.com/config/rma/config.json", new Listener<String>() {
        public void onResponse(String var1x) {
          try {
            boolean var2 = (new JSONObject(var1x)).optBoolean("AppOfTheDayFeaturingUs", false);
            var1.isFeatured(var2);
          } catch (JSONException var3) {
            var3.printStackTrace();
          }
        }
      }, new ErrorListener() {
        public void onErrorResponse(VolleyError var1) {
          if(var1 != null && var1.getMessage() != null && !var1.getMessage().isEmpty()) {
            Log.e("AppOfTheDayMain", var1.getMessage());
          }

        }
      }));
    }

  }

  public static boolean isAppTurboUnlockable(Context var0) {
    Iterator var1 = var0.getPackageManager().getInstalledPackages(0).iterator();

    PackageInfo var2;
    do {
      if(!var1.hasNext()) {
        Intent var3 = new Intent("android.intent.action.VIEW");
        var3.setFlags(268435456);
        var3.setData(Uri.parse("appturbo://check"));
        if(var0.getPackageManager().queryIntentActivities(var3, 65536).size() <= 0) {
          return false;
        }
        break;
      }

      var2 = (PackageInfo)var1.next();
    } while(!var2.packageName.equalsIgnoreCase("com.appturbo.appturboCA2015") && !var2.packageName.equalsIgnoreCase("com.appturbo.appoftheday2015") && !var2.packageName.equalsIgnoreCase("com.appturbo.appofthenight"));

    return true;
  }
}
