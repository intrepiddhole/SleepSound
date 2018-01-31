package ipnossoft.rma.ui.recyclerview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

public class BottomFadeRecyclerView extends RecyclerView {
  public BottomFadeRecyclerView(Context var1) {
    super(var1);
  }

  public BottomFadeRecyclerView(Context var1, @Nullable AttributeSet var2) {
    super(var1, var2);
  }

  public BottomFadeRecyclerView(Context var1, @Nullable AttributeSet var2, int var3) {
    super(var1, var2, var3);
  }

  protected float getTopFadingEdgeStrength() {
    return 0.0F;
  }
}
