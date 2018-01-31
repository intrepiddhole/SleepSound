package ipnossoft.rma.ui.scrollview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;
import ipnossoft.rma.favorites.StaffPicksScrollViewAdapter;
import ipnossoft.rma.free.R;
import ipnossoft.rma.ui.parallax.StaffPicksGraphicsAnimator;

public class StaffPicksHorizontalScrollView extends HorizontalScrollView {
  private StaffPicksScrollViewAdapter adapter;
  private StaffPicksGraphicsAnimator staffPicksGraphicsAnimator;
  private int viewPortSize;

  public StaffPicksHorizontalScrollView(Context var1) {
    super(var1);
  }

  public StaffPicksHorizontalScrollView(Context var1, AttributeSet var2) {
    super(var1, var2);
  }

  public StaffPicksHorizontalScrollView(Context var1, AttributeSet var2, int var3) {
    super(var1, var2, var3);
  }

  public void configure(View var1, StaffPicksScrollViewAdapter var2) {
    this.staffPicksGraphicsAnimator = new StaffPicksGraphicsAnimator(var1);
    this.adapter = var2;
    this.viewPortSize = this.getContext().getResources().getDimensionPixelSize(R.dimen.favorite_staff_picks_width_with_rounded_corners);
  }

  protected void onScrollChanged(int var1, int var2, int var3, int var4) {
    super.onScrollChanged(var1, var2, var3, var4);
    var2 = this.getContext().getResources().getDimensionPixelSize(R.dimen.favorite_staff_picks_first_last_margin_lr);
    var3 = this.adapter.calculateCellWidth();
    var4 = this.adapter.getCount();
    this.staffPicksGraphicsAnimator.handleScroll(var1, var3 * var4 + var2 * 2, this.viewPortSize);
  }
}