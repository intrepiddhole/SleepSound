package ipnossoft.rma.animation;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.os.Handler;
import android.view.View;

public class ViewTransitionAnimator {
  private View fadingInView = null;
  private View fadingOutView = null;

  public ViewTransitionAnimator() {
  }

  public boolean areViewsTransitioning() {
    return this.fadingInView != null || this.fadingOutView != null;
  }

  public void cancelAnimations() {
    if(this.fadingInView != null) {
      this.fadingInView.animate().setListener((AnimatorListener)null);
      this.fadingInView.animate().cancel();
      this.fadingInView.setVisibility(View.GONE);
      this.fadingInView.setAlpha(0.0F);
    }

    if(this.fadingOutView != null) {
      this.fadingOutView.animate().setListener((AnimatorListener)null);
      this.fadingOutView.animate().cancel();
      this.fadingOutView.setVisibility(View.VISIBLE);
      this.fadingOutView.setAlpha(1.0F);
    }

  }

  public void transitionBetweenViews(final View var1, final View var2) {
    var2.animate().alpha(1.0F).setListener(new AnimatorListener() {
      public void onAnimationCancel(Animator var1) {
      }

      public void onAnimationEnd(Animator var1) {
        var2.setAlpha(1.0F);
        ViewTransitionAnimator.this.fadingInView = null;
      }

      public void onAnimationRepeat(Animator var1) {
      }

      public void onAnimationStart(Animator var1) {
        var2.setVisibility(View.VISIBLE);
        var2.setAlpha(0.0F);
        ViewTransitionAnimator.this.fadingInView = var2;
      }
    });
    var1.animate().alpha(0.0F).setListener(new AnimatorListener() {
      public void onAnimationCancel(Animator var1x) {
      }

      public void onAnimationEnd(Animator var1x) {
        (new Handler()).post(new Runnable() {
          public void run() {
            var1.setVisibility(View.GONE);
            ViewTransitionAnimator.this.fadingOutView = null;
          }
        });
      }

      public void onAnimationRepeat(Animator var1x) {
      }

      public void onAnimationStart(Animator var1x) {
        ViewTransitionAnimator.this.fadingOutView = var1;
      }
    });
  }
}
