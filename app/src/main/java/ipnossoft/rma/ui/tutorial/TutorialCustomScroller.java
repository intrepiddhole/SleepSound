package ipnossoft.rma.ui.tutorial;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

public class TutorialCustomScroller extends Scroller {
  public static final int SCROLL_DURATION = 2000;
  private double mScrollFactor = 3.0D;
  private int maxWidth;

  public TutorialCustomScroller(Context var1) {
    super(var1);
    this.commonInit(var1);
  }

  public TutorialCustomScroller(Context var1, Interpolator var2) {
    super(var1, var2);
    this.commonInit(var1);
  }

  @SuppressLint({"NewApi"})
  public TutorialCustomScroller(Context var1, Interpolator var2, boolean var3) {
    super(var1, var2, var3);
  }

  private void commonInit(Context var1) {
    this.maxWidth = var1.getResources().getDisplayMetrics().widthPixels;
  }

  public void abortAnimation() {
  }

  public void startScroll(int var1, int var2, int var3, int var4, int var5) {
    if(var3 >= 0) {
      if(var3 >= this.maxWidth) {
        super.startScroll(var1, var2, var3, var4, 2000);
      } else {
        super.startScroll(var1, var2, var3, var4, var5);
      }
    }
  }
}