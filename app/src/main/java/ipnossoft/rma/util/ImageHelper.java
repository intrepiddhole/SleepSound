package ipnossoft.rma.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;

public class ImageHelper {
  public ImageHelper() {
  }

  public static Bitmap getRoundedCornerBitmap(Context var0, int var1, int var2) {
    return var1 != 0?getRoundedCornerBitmap(BitmapFactory.decodeResource(var0.getResources(), var1), var2):null;
  }

  public static Bitmap getRoundedCornerBitmap(Bitmap var0, int var1) {
    if(var0 != null) {
      Bitmap var3 = Bitmap.createBitmap(var0.getWidth(), var0.getHeight(), Config.ARGB_8888);
      Canvas var4 = new Canvas(var3);
      Paint var5 = new Paint();
      Rect var6 = new Rect(0, 0, var0.getWidth(), var0.getHeight());
      RectF var7 = new RectF(var6);
      float var2 = (float)var1;
      var5.setAntiAlias(true);
      var4.drawARGB(0, 0, 0, 0);
      var5.setColor(-12434878);
      var4.drawRoundRect(var7, var2, var2, var5);
      var5.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
      var4.drawBitmap(var0, var6, var6, var5);
      return var3;
    } else {
      return null;
    }
  }
}
