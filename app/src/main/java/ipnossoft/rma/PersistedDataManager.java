package ipnossoft.rma;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PersistedDataManager {
  private static final String PREFERENCE_FILENAME = "ipnossoft.rma.persisted.data";

  private PersistedDataManager() {
  }

  public static Boolean getBoolean(String var0, boolean var1, Context var2) {
    return Boolean.valueOf(getSharedPreferences(var2).getBoolean(var0, var1));
  }

  public static float getFloat(String var0, float var1, Context var2) {
    return getSharedPreferences(var2).getFloat(var0, var1);
  }

  public static int getInteger(String var0, int var1, Context var2) {
    return getSharedPreferences(var2).getInt(var0, var1);
  }

  public static long getLong(String var0, long var1, Context var3) {
    return getSharedPreferences(var3).getLong(var0, var1);
  }

  private static SharedPreferences getSharedPreferences(Context var0) {
    return var0.getSharedPreferences("ipnossoft.rma.persisted.data", 0);
  }

  public static String getString(String var0, String var1, Context var2) {
    return getSharedPreferences(var2).getString(var0, var1);
  }

  public static void incrementCounter(String var0, Context var1) {
    SharedPreferences var3 = getSharedPreferences(var1);
    int var2 = var3.getInt(var0, 0);
    Editor var4 = var3.edit();
    var4.putInt(var0, var2 + 1);
    var4.apply();
  }

  public static void saveBoolean(String var0, boolean var1, Context var2) {
    Editor var3 = getSharedPreferences(var2).edit();
    var3.putBoolean(var0, var1);
    var3.apply();
  }

  public static void saveFloat(String var0, float var1, Context var2) {
    Editor var3 = getSharedPreferences(var2).edit();
    var3.putFloat(var0, var1);
    var3.apply();
  }

  public static void saveInteger(String var0, int var1, Context var2) {
    Editor var3 = getSharedPreferences(var2).edit();
    var3.putInt(var0, var1);
    var3.apply();
  }

  public static void saveLong(String var0, long var1, Context var3) {
    Editor var4 = getSharedPreferences(var3).edit();
    var4.putLong(var0, var1);
    var4.apply();
  }

  public static void saveString(String var0, String var1, Context var2) {
    Editor var3 = getSharedPreferences(var2).edit();
    var3.putString(var0, var1);
    var3.apply();
  }
}
