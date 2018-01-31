package ipnossoft.rma.util;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Build.VERSION;
import android.text.Html;
import com.amplitude.api.Amplitude;
import ipnossoft.rma.RelaxMelodiesApp;
import ipnossoft.rma.free.R;

public class SendMailUtils {
  private static final String LINE = "_________________________";

  public SendMailUtils() {
  }

  private static String buildMailText(RelaxMelodiesApp var0, Activity var1) {
    String var2 = var0.getAppName();
    String var3 = String.format("%s %s (%s)", new Object[]{Build.MANUFACTURER, Build.MODEL, Build.PRODUCT});
    String var4 = VERSION.RELEASE;
    String var6 = var0.getMarketCustomParam().name().toLowerCase();
    String var5 = Amplitude.getInstance().getDeviceId();
    return String.format("\n\n%s\nProduct: %s\nVersion: %s\nModel: %s\nSystem: Android %s\nMarket: %s\nAmplitude: %s\n%s", new Object[]{"_________________________", var2, getVersion(var1), var3, var4, var6, var5, "_________________________"});
  }

  private static String getVersion(Activity var0) {
    try {
      String var1 = var0.getApplicationInfo().packageName;
      PackageInfo var3 = var0.getPackageManager().getPackageInfo(var1, 0);
      String var4 = String.format("%s (%d)", new Object[]{var3.versionName, Integer.valueOf(var3.versionCode)});
      return var4;
    } catch (NameNotFoundException var2) {
      return null;
    }
  }

  private static void safeStartActivity(Activity var0, Intent var1) {
    if(var1.resolveActivity(var0.getPackageManager()) != null) {
      var0.startActivity(var1);
    }

  }

  private static void sendMail(Activity var0, String var1, String var2, String var3, String... var4) {
    Intent var5 = new Intent("android.intent.action.SEND");
    var5.setType("text/plain");
    var5.putExtra("android.intent.extra.SUBJECT", var1);
    if(var3 != null) {
      var5.putExtra("android.intent.extra.TEXT", Html.fromHtml(var3));
    } else {
      var5.putExtra("android.intent.extra.TEXT", var2);
    }

    if(var4 != null) {
      var5.putExtra("android.intent.extra.EMAIL", var4);
    }

    safeStartActivity(var0, Intent.createChooser(var5, var0.getString(R.string.web_activity_mail_intent_title)));
  }

  public static void sendShareMail(Activity var0) {
    RelaxMelodiesApp var2 = (RelaxMelodiesApp)var0.getApplicationContext();
    String var1 = String.format("%s %s!", new Object[]{var0.getString(R.string.mail_friend_title), var2.getAppName()});
    String var4 = var2.getWebMarketLink("email_sharing");
    String var3 = var0.getString(R.string.mail_friend_text);
    sendMail(var0, var1, var3 + " \n\n" + var4, var3 + "<br/><br/><a href=\"" + var4 + "\">" + var1 + "</a>", new String[0]);
  }

  public static void sendSupportMail(Activity var0) {
    RelaxMelodiesApp var1 = (RelaxMelodiesApp)var0.getApplicationContext();
    String var2 = buildMailText(var1, var0);
    sendMail(var0, String.format("%s Android %s", new Object[]{var1.getAppName(), getVersion(var0)}), var2, (String)null, new String[]{var0.getString(R.string.mail_support_address)});
  }
}
