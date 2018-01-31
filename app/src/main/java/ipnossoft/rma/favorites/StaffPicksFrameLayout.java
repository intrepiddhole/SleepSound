package ipnossoft.rma.favorites;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Path.Direction;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;

public class StaffPicksFrameLayout extends RelativeLayout {
  Path clipPath;
  private boolean doClip = true;
  Paint paint;

  public StaffPicksFrameLayout(Context var1) {
    super(var1);
    this.commonInit();
  }

  public StaffPicksFrameLayout(Context var1, AttributeSet var2) {
    super(var1, var2);
    this.commonInit();
  }

  public StaffPicksFrameLayout(Context var1, AttributeSet var2, int var3) {
    super(var1, var2, var3);
    this.commonInit();
  }

  private void commonInit() {
    this.setWillNotDraw(false);
  }

  private void initPathIfNecessary() {
    if(this.paint == null) {
      this.paint = new Paint();
    }

    if(this.clipPath == null) {
      this.clipPath = new Path();
      int var1 = this.getMeasuredHeight();
      int var2 = this.getMeasuredWidth();
      this.clipPath.addRoundRect(new RectF((float)var2 * 0.013409962F, (float)var1 * 0.07103825F, (float)var2 * (1.0F - 0.013409962F), (float)var1 * (1.0F - 0.07103825F)), (float)var2 * 0.038314175F, (float)var1 * 0.10928962F, Direction.CW);
      this.clipPath.close();
      this.clipPath.addOval(new RectF((float)var2 * 0.031609196F, 0.0F, (float)var2 * (1.0F - 0.031609196F), (float)var1 * 0.18579236F), Direction.CW);
      this.clipPath.addOval(new RectF((float)var2 * 0.031609196F, (float)var1 * (1.0F - 0.18579236F), (float)var2 * (1.0F - 0.031609196F), (float)var1), Direction.CW);
      this.clipPath.addOval(new RectF(0.0F, (float)var1 * 0.09836066F, (float)var2 * 0.05842912F, (float)var1 * (1.0F - 0.09836066F)), Direction.CW);
      this.clipPath.addOval(new RectF((float)var2 * (1.0F - 0.05842912F), (float)var1 * 0.09836066F, (float)var2, (float)var1 * (1.0F - 0.09836066F)), Direction.CW);
    }

  }

  protected void onDraw(Canvas var1) {
    this.initPathIfNecessary();
    if(VERSION.SDK_INT <= 19 && VERSION.SDK_INT >= 11) {
      this.setLayerType(1, (Paint)null);
    }

    if(this.doClip) {
      try {
        var1.clipPath(this.clipPath);
      } catch (UnsupportedOperationException var3) {
        Log.e("ClippedFrame", "clipPath() not supported");
        this.doClip = false;
      }
    } else {
      this.setClipChildren(true);
    }

    super.onDraw(var1);
  }
}
