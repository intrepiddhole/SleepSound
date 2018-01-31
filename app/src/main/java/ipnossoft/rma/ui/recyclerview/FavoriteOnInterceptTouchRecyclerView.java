package ipnossoft.rma.ui.recyclerview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

import ipnossoft.rma.free.R;

public class FavoriteOnInterceptTouchRecyclerView extends RecyclerView {
  private boolean interceptedAtMove = false;
  private float mx;
  private float my;

  public FavoriteOnInterceptTouchRecyclerView(Context var1) {
    super(var1);
  }

  public FavoriteOnInterceptTouchRecyclerView(Context var1, @Nullable AttributeSet var2) {
    super(var1, var2);
  }

  public FavoriteOnInterceptTouchRecyclerView(Context var1, @Nullable AttributeSet var2, int var3) {
    super(var1, var2, var3);
  }

  public boolean canScrollHorizontally(int var1) {
    return false;
  }

  public boolean canScrollVertically(int var1) {
    return true;
  }

  protected float getTopFadingEdgeStrength() {
    return 0.0F;
  }

  public boolean onInterceptTouchEvent(MotionEvent var1) {
    switch(var1.getAction()) {
      case 0:
        this.mx = var1.getX();
        this.my = var1.getY();
        this.interceptedAtMove = false;
        break;
      case 1:
      case 8:
        if(this.interceptedAtMove) {
          return false;
        }
        break;
      case 2:
        if(this.mx == 0.0F && this.my == 0.0F) {
          this.mx = var1.getX();
          this.my = var1.getY();
        } else {
          float var2 = var1.getX();
          float var3 = var1.getY();
          if((double)Math.abs(this.my - var3) <= (double)Math.abs(this.mx - var2) * 1.5D && (HorizontalScrollView)this.findViewById(R.id.staff_pick_horizontal_scroll_view) != null) {
            this.interceptedAtMove = true;
            return false;
          }

          this.mx = 0.0F;
          this.my = 0.0F;
        }
      case 3:
      case 4:
      case 5:
      case 6:
      case 7:
    }

    return super.onInterceptTouchEvent(var1);
  }
}
