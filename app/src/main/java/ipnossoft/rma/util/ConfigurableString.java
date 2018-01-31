package ipnossoft.rma.util;

import android.content.Context;
import ipnossoft.rma.RelaxPropertyHandler;
import java.util.Locale;

public class ConfigurableString {
  static String currentLanguage = null;

  public ConfigurableString() {
  }

  private static String currentLanguage() {
    if(currentLanguage == null) {
      currentLanguage = Locale.getDefault().getLanguage().toLowerCase();
    }

    return currentLanguage;
  }

  public static String getString(Context var0, String var1, int var2) {
    String var3 = null;
    if(currentLanguage().equals("en")) {
      var3 = RelaxPropertyHandler.getInstance().getProperties().getProperty(var1);
    }

    var1 = var3;
    if(var3 == null) {
      var1 = var0.getResources().getString(var2);
    }

    return var1;
  }
}
