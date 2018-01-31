package ipnossoft.rma.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.RelativeLayout;

public class RoundedCornerLayout extends RelativeLayout {
  private static final float CORNER_RADIUS = 8.0F;
  private float cornerRadius;
  private Bitmap maskBitmap;
  private Paint maskPaint;
  private Paint paint;

  public RoundedCornerLayout(Context var1) {
    super(var1);
    this.init(var1, (AttributeSet)null, 0);
  }

  public RoundedCornerLayout(Context var1, AttributeSet var2) {
    super(var1, var2);
    this.init(var1, var2, 0);
  }

  public RoundedCornerLayout(Context var1, AttributeSet var2, int var3) {
    super(var1, var2, var3);
    this.init(var1, var2, var3);
  }

  private Bitmap createMask(int var1, int var2) {
    Bitmap var3 = Bitmap.createBitmap(var1, var2, Config.ALPHA_8);
    Canvas var4 = new Canvas(var3);
    Paint var5 = new Paint(1);
    var5.setColor(-1);
    var4.drawRect(0.0F, 0.0F, (float)var1, (float)var2, var5);
    var5.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
    var4.drawRoundRect(new RectF(0.0F, 0.0F, (float)var1, (float)var2), this.cornerRadius, this.cornerRadius, var5);
    return var3;
  }

  private void init(Context var1, AttributeSet var2, int var3) {
    this.cornerRadius = TypedValue.applyDimension(1, 8.0F, var1.getResources().getDisplayMetrics());
    this.paint = new Paint(1);
    this.maskPaint = new Paint(3);
    this.maskPaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
    this.setWillNotDraw(false);
  }

  public void draw(Canvas var1) {
    if(var1.getWidth() > 0 && var1.getHeight() > 0) {
      Bitmap var2 = Bitmap.createBitmap(var1.getWidth(), var1.getHeight(), Config.ARGB_8888);
      Canvas var3 = new Canvas(var2);
      super.draw(var3);
      if(this.maskBitmap == null) {
        this.maskBitmap = this.createMask(var1.getWidth(), var1.getHeight());
      }

      var3.drawBitmap(this.maskBitmap, 0.0F, 0.0F, this.maskPaint);
      var1.drawBitmap(var2, 0.0F, 0.0F, this.paint);
    }

  }
}
