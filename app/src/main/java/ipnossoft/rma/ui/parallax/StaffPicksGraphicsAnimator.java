package ipnossoft.rma.ui.parallax;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import com.bumptech.glide.Glide;

import ipnossoft.rma.free.R;
import ipnossoft.rma.ui.scrollview.DisableTouchScrollView;

public class StaffPicksGraphicsAnimator {
  private int dimenWidthDistantMountains;
  private int dimenWidthLakeMountains;
  private DisableTouchScrollView staffPicksDistantMountains;
  private DisableTouchScrollView staffPicksLakeMountains;

  public StaffPicksGraphicsAnimator(View var1) {
    Context var2 = var1.getContext();
    this.dimenWidthDistantMountains = var2.getResources().getDimensionPixelSize(R.dimen.favorite_staff_picks_width_distant_mountains);
    this.dimenWidthLakeMountains = var2.getResources().getDimensionPixelSize(R.dimen.favorite_staff_picks_width_lake_mountains);
    this.staffPicksDistantMountains = (DisableTouchScrollView)var1.findViewById(R.id.favorite_staff_picks_distant_mountains);
    this.staffPicksLakeMountains = (DisableTouchScrollView)var1.findViewById(R.id.favorite_staff_picks_lake_mountains);
    ImageView var3 = (ImageView)var1.findViewById(R.id.favorite_staff_picks_distant_mountains_image);
    Glide.with(var1.getContext()).load(Integer.valueOf(R.drawable.favorite_bg_01)).placeholder(R.drawable.favorite_bg_01).dontAnimate().into(var3);
    var3 = (ImageView)var1.findViewById(R.id.favorite_staff_picks_lake_mountains_image);
    Glide.with(var1.getContext()).load(Integer.valueOf(R.drawable.favorite_bg_02)).placeholder(R.drawable.favorite_bg_02).dontAnimate().into(var3);
  }

  private void moveParallaxedLayers(int var1, int var2, int var3) {
    double var4 = (double)(var2 - var3);
    var4 = (double)var1 / var4;
    var1 = this.dimenWidthDistantMountains;
    this.staffPicksDistantMountains.scrollTo((int)((double)(var1 - var3) * var4), 0);
    var1 = this.dimenWidthLakeMountains;
    this.staffPicksLakeMountains.scrollTo((int)((double)(var1 - var3) * var4), 0);
  }

  public void handleScroll(int var1, int var2, int var3) {
    this.moveParallaxedLayers(var1, var2, var3);
  }
}
