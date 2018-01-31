package ipnossoft.rma.util;

import android.content.Context;
import android.telephony.TelephonyManager;
import java.util.Locale;

public class CountryUtils {
  public CountryUtils() {
  }

  public static String getCountry(Context var0) {
    String var1 = null;
    TelephonyManager var3 = (TelephonyManager)var0.getSystemService("phone");
    if(var3 != null) {
      label20: {
        String var2 = var3.getSimCountryIso();
        if(var2 != null) {
          var1 = var2;
          if(var2.length() >= 2) {
            break label20;
          }
        }

        var1 = var3.getNetworkCountryIso();
      }
    }

    Locale var5 = var0.getResources().getConfiguration().locale;
    String var4;
    if(var1 != null) {
      var4 = var1;
      if(var1.length() >= 2) {
        return var4.toUpperCase();
      }
    }

    var4 = var5.getCountry();
    return var4.toUpperCase();
  }
}
