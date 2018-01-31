package ipnossoft.rma.ui.listview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class BottomFadeListView extends ListView {
  public BottomFadeListView(Context var1) {
    super(var1);
  }

  public BottomFadeListView(Context var1, AttributeSet var2) {
    super(var1, var2);
  }

  public BottomFadeListView(Context var1, AttributeSet var2, int var3) {
    super(var1, var2, var3);
  }

  protected float getTopFadingEdgeStrength() {
    return 0.0F;
  }
}
