package ipnossoft.rma.deepurl;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.GoogleApiClient.Builder;
import com.ipnossoft.api.httputils.URLUtils;
import com.ipnossoft.api.soundlibrary.Sound;
import ipnossoft.rma.RelaxMelodiesApp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DeepUrlManager {
  private static final String ACTION_PLAY = "play";
  private static final String TAG = "DeepUrlManager";
  private static GoogleApiClient googleApiClient;

  public DeepUrlManager() {
  }

  @NonNull
  private static Uri getAppUri(Sound var0) {
    return Uri.parse(URLUtils.combineParams("android-app://" + RelaxMelodiesApp.getInstance().getApplicationContext().getPackageName() + "/relaxmelodies/play", new String[]{"sounds=" + var0.getName(), "volume=1"}));
  }

  private static DeepUrlAction getUrlAction(String var0, HashMap<String, List<String>> var1, Activity var2) {
    return var0.equals("play")?new PlayAction(var1, var2):null;
  }

  private static void indexPlay(Sound var0) {
    String var1 = var0.getName();
    final Uri var2 = getAppUri(var0);
    Action var3 = Action.newAction("http://schema.org/ViewAction", var1, var2);
    AppIndex.AppIndexApi.start(googleApiClient, var3).setResultCallback(new ResultCallback<Status>() {
      public void onResult(Status var1) {
        if(!var1.isSuccess()) {
          Log.e("DeepUrlManager", "Indexing failed for " + var2.toString() + " > " + var1.toString());
        }

      }
    });
  }

  private static void indexStop(Sound var0) {
    String var1 = var0.getName();
    final Uri var2 = getAppUri(var0);
    Action var3 = Action.newAction("http://schema.org/ViewAction", var1, var2);
    AppIndex.AppIndexApi.end(googleApiClient, var3).setResultCallback(new ResultCallback<Status>() {
      public void onResult(Status var1) {
        if(!var1.isSuccess()) {
          Log.e("DeepUrlManager", "Indexing failed for " + var2.toString() + " > " + var1.toString());
        }

      }
    });
  }

  public static void initialize() {
    googleApiClient = (new Builder(RelaxMelodiesApp.getInstance().getApplicationContext())).addApi(AppIndex.APP_INDEX_API).build();
    googleApiClient.connect();
  }

  public static void onDestroy() {
    if(googleApiClient != null) {
      googleApiClient.disconnect();
    }

  }

  public static DeepUrlAction parseIntent(Intent var0, Activity var1) {
    String var6 = var0.getAction();
    String var11 = var0.getDataString();
    if("android.intent.action.VIEW".equals(var6) && var11 != null) {
      String[] var12 = var11.substring(var11.lastIndexOf("/") + 1).split("\\?");
      if(var12.length == 2) {
        var11 = var12[0].toLowerCase();
        String var7 = var12[1];
        HashMap var13 = new HashMap();
        String[] var14 = var7.split("&");
        int var4 = var14.length;

        for(int var2 = 0; var2 < var4; ++var2) {
          String[] var8 = var14[var2].split("=");
          String[] var9 = var8[1].split(",");
          ArrayList var10 = new ArrayList();
          int var5 = var9.length;

          for(int var3 = 0; var3 < var5; ++var3) {
            var10.add(var9[var3].toLowerCase());
          }

          var13.put(var8[0], var10);
        }

        return getUrlAction(var11, var13, var1);
      }
    }

    return null;
  }
}
