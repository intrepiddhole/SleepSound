package com.ipnossoft.api.featuremanager.util;

import android.util.Log;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;

public class Utils {
  private static final ObjectMapper mapper = new ObjectMapper();

  public Utils() {
  }

  public static <T> T jsonToObject(@NotNull String var0, Class<?> var1) {
    try {
      Object var3 = mapper.readValue(var0, var1);
      return (T) var3;
    } catch (Exception var2) {
      Log.e("Utils", "", var2);
      return null;
    }
  }

  public static <T> String objectToJson(T var0) {
    try {
      String var2 = mapper.writeValueAsString(var0);
      return var2;
    } catch (Exception var1) {
      Log.e("Utils", "", var1);
      return null;
    }
  }
}
