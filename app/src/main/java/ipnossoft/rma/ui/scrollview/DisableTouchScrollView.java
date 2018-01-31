
package ipnossoft.rma.ui.scrollview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class DisableTouchScrollView extends ScrollView {
  public DisableTouchScrollView(Context var1, AttributeSet var2) {
    super(var1, var2);
  }

  public DisableTouchScrollView(Context var1, AttributeSet var2, int var3) {
    super(var1, var2, var3);
  }

  public boolean onInterceptTouchEvent(MotionEvent var1) {
    return false;
  }

  public boolean onTouchEvent(MotionEvent var1) {
    return false;
  }
}
