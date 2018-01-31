package com.nativemediaplayer;

import android.os.Build.VERSION;
import java.lang.reflect.Field;

public class OsUtil {
  public static final int apiLevel = getApiLevel();

  public OsUtil() {
  }

  private static int getApiLevel() {
    try {
      Field var0 = VERSION.class.getField("SDK_INT");
      var0.setAccessible(true);
      int var1 = var0.getInt((Object)null);
      return var1;
    } catch (Throwable var2) {
      return 3;
    }
  }
}