package ipnossoft.rma.ui.shootingstar;

import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.bumptech.glide.Glide;
import ipnossoft.rma.MainActivity;

import ipnossoft.rma.animation.ShootingStarAnimator;
import ipnossoft.rma.free.R;

public class ShootingStarHelper {
  public ShootingStarHelper() {
  }

  public static void animateShootingStar(MainActivity var0) {
    RelativeLayout var1 = (RelativeLayout)var0.findViewById(R.id.shooting_star_canvas_layout);
    ImageView var2 = (ImageView)var0.findViewById(R.id.shooting_star_image_view);
    Glide.with(var0).load(Integer.valueOf(R.drawable.shootingstar_single)).placeholder(R.drawable.shootingstar_single).dontAnimate().into(var2);
    (new ShootingStarAnimator(var2, var1, var0)).start();
  }
}
