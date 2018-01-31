
package ipnossoft.rma.ui.tutorial;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.Interpolator;
import java.lang.reflect.Field;

public class TutorialViewPager extends ViewPager {
  private float initialXValue = 0.0F;
  private float lastValue;
  private TutorialCustomScroller mScroller = null;

  public TutorialViewPager(Context var1) {
    super(var1);
    this.postInitViewPager();
  }

  public TutorialViewPager(Context var1, AttributeSet var2) {
    super(var1, var2);
    this.postInitViewPager();
  }

  private void postInitViewPager() {
    try {
      Field var1 = ViewPager.class.getDeclaredField("mScroller");
      var1.setAccessible(true);
      Field var2 = ViewPager.class.getDeclaredField("sInterpolator");
      var2.setAccessible(true);
      this.mScroller = new TutorialCustomScroller(this.getContext(), (Interpolator)var2.get((Object)null));
      var1.set(this, this.mScroller);
    } catch (Exception var3) {
      ;
    }
  }

  public boolean onTouchEvent(MotionEvent var1) {
    return false;
  }

  public void setTutorialPageAdapter(TutorialPageAdapter var1) {
    super.setAdapter(var1);
  }
}