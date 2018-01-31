package ipnossoft.rma.animation;

import android.animation.AnimatorSet;

public class ViewPagerInOutTransitionAnimations {
  private ViewPagerTransitionAnimation animationsIn = new ViewPagerTransitionAnimation(true);
  private ViewPagerTransitionAnimation animationsOut;

  public ViewPagerInOutTransitionAnimations(AnimatorSet var1, AnimatorSet var2) {
    this.animationsIn.setCompoundedAnimations(var1);
    this.animationsOut = new ViewPagerTransitionAnimation(false);
    this.animationsOut.setCompoundedAnimations(var2);
  }

  public ViewPagerTransitionAnimation getAnimationsIn() {
    return this.animationsIn;
  }

  public ViewPagerTransitionAnimation getAnimationsOut() {
    return this.animationsOut;
  }
}
