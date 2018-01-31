package ipnossoft.rma.util;

import android.content.Context;

class DeviceUtils {
  DeviceUtils() {
  }

  public static String getScreenDensity(Context var0) {
    float var1 = var0.getResources().getDisplayMetrics().scaledDensity;
    return var1 >= 4.0F?"xxxhdpi":((double)var1 >= 3.5D?"xxxhdpi & xxhdpi":(var1 >= 3.0F?"xxhdpi":(var1 >= 2.0F?"xhdpi":((double)var1 >= 1.5D?"hdpi":(var1 >= 1.0F?"mdpi":"ldpi")))));
  }
}
