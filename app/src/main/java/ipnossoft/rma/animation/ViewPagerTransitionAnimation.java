package ipnossoft.rma.animation;

import android.animation.AnimatorSet;

public class ViewPagerTransitionAnimation {
  private AnimatorSet compoundedAnimations;
  private boolean inProgress = false;
  private boolean isAnimationIn = false;

  public ViewPagerTransitionAnimation(boolean var1) {
    this.isAnimationIn = var1;
    this.compoundedAnimations = new AnimatorSet();
  }

  public AnimatorSet getCompoundedAnimations() {
    return this.compoundedAnimations;
  }

  public boolean isAnimationIn() {
    return this.isAnimationIn;
  }

  public boolean isInProgress() {
    return this.inProgress;
  }

  public void setCompoundedAnimations(AnimatorSet var1) {
    this.compoundedAnimations = var1;
  }

  public void setInProgress(boolean var1) {
    this.inProgress = var1;
  }
}
