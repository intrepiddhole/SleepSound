package ipnossoft.rma.util;

import android.content.Context;
import android.util.DisplayMetrics;

public class DimensionUtils {
  private static int heightInDpi = 0;

  public DimensionUtils() {
  }

  public static int getHeightDensityIndependantPixels(Context var0) {
    if(heightInDpi == 0) {
      DisplayMetrics var1 = var0.getResources().getDisplayMetrics();
      heightInDpi = (int)((float)var1.heightPixels / var1.scaledDensity);
    }

    return heightInDpi;
  }
}
