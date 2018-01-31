package ipnossoft.rma.ui.navigation;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.RelativeLayout;

public class NavigationTabBarHandler {
  private final int ANIMATION_DURATION = 200;
  private View commonParent;
  private View navigationTabBarIndicator;

  public NavigationTabBarHandler(View var1, RelativeLayout var2) {
    this.navigationTabBarIndicator = var1;
    this.commonParent = var2;
  }

  private int getRelativeLeft(View var1) {
    if(var1.getParent() == this.commonParent) {
      return var1.getLeft();
    } else {
      int var2 = var1.getLeft();
      return this.getRelativeLeft((View)var1.getParent()) + var2;
    }
  }

  public void moveTabIndicatorToTabButton(Button var1, boolean var2) {
    ObjectAnimator var5 = ObjectAnimator.ofFloat(this.navigationTabBarIndicator, "scaleX", new float[]{(float)(var1.getMeasuredWidth() / this.navigationTabBarIndicator.getMeasuredWidth())});
    var5.setInterpolator(new AccelerateDecelerateInterpolator());
    ObjectAnimator var7 = ObjectAnimator.ofFloat(this.navigationTabBarIndicator, "translationX", new float[]{(float)(this.getRelativeLeft(var1) + var1.getMeasuredWidth() / 2)});
    var7.setInterpolator(new AccelerateDecelerateInterpolator());
    AnimatorSet var6 = new AnimatorSet();
    var6.playTogether(new Animator[]{var5, var7});
    long var3;
    if(var2) {
      var3 = 200L;
    } else {
      var3 = 0L;
    }

    var6.setDuration(var3);
    var6.start();
  }
}
