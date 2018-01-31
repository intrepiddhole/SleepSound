package ipnossoft.rma.util;

import android.os.Bundle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class BundleUtils {
  public BundleUtils() {
  }

  public static Bundle convertMapToBundle(Map<String, Object> var0) {
    Bundle var2 = new Bundle();
    if(var0 != null) {
      Iterator var3 = var0.keySet().iterator();

      while(true) {
        while(var3.hasNext()) {
          String var4 = (String)var3.next();
          Object var5 = var0.get(var4);
          if(var5 instanceof Integer) {
            var2.putInt(var4, ((Integer)var5).intValue());
          } else if(var5 instanceof Long) {
            var2.putLong(var4, ((Long)var5).longValue());
          } else if(var5 instanceof Double) {
            var2.putDouble(var4, ((Double)var5).doubleValue());
          } else if(var5 instanceof Float) {
            var2.putFloat(var4, ((Float)var5).floatValue());
          } else if(var5 instanceof Short) {
            var2.putShort(var4, ((Short)var5).shortValue());
          } else if(!(var5 instanceof ArrayList)) {
            var2.putString(var4, String.valueOf(var5));
          } else {
            ArrayList var7 = (ArrayList)var5;
            Object var6 = var7.get(0);
            int var1;
            if(var6 instanceof Integer) {
              int[] var13 = new int[var7.size()];

              for(var1 = 0; var1 < var7.size(); ++var1) {
                var13[var1] = ((Integer)var7.get(var1)).intValue();
              }

              var2.putIntArray(var4, var13);
            } else if(var6 instanceof Long) {
              long[] var12 = new long[var7.size()];

              for(var1 = 0; var1 < var7.size(); ++var1) {
                var12[var1] = ((Long)var7.get(var1)).longValue();
              }

              var2.putLongArray(var4, var12);
            } else if(var6 instanceof Double) {
              double[] var11 = new double[var7.size()];

              for(var1 = 0; var1 < var7.size(); ++var1) {
                var11[var1] = ((Double)var7.get(var1)).doubleValue();
              }

              var2.putDoubleArray(var4, var11);
            } else if(var6 instanceof Float) {
              float[] var10 = new float[var7.size()];

              for(var1 = 0; var1 < var7.size(); ++var1) {
                var10[var1] = ((Float)var7.get(var1)).floatValue();
              }

              var2.putFloatArray(var4, var10);
            } else if(var6 instanceof Short) {
              short[] var9 = new short[var7.size()];

              for(var1 = 0; var1 < var7.size(); ++var1) {
                var9[var1] = ((Short)var7.get(var1)).shortValue();
              }

              var2.putShortArray(var4, var9);
            } else {
              String[] var8 = new String[var7.size()];

              for(var1 = 0; var1 < var7.size(); ++var1) {
                var8[var1] = String.valueOf(var7.get(var1));
              }

              var2.putStringArray(var4, var8);
            }
          }
        }

        return var2;
      }
    } else {
      return var2;
    }
  }
}
