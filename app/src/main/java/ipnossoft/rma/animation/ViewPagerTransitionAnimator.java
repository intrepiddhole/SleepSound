package ipnossoft.rma.animation;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ViewPagerTransitionAnimator {
  private ViewPagerTransitionAnimator.PageTransitionCallback callback;
  private Map<Integer, ViewPagerInOutTransitionAnimations> pageAnimations = new HashMap();
  private List<ViewPagerTransitionAnimation> transitionAnimationQueue = new ArrayList();

  public ViewPagerTransitionAnimator(ViewPagerTransitionAnimator.PageTransitionCallback var1) {
    this.callback = var1;
  }

  private void addTransition(ViewPagerTransitionAnimation var1, int var2, int var3) {
    if(var1 != null && var1.getCompoundedAnimations() != null) {
      var1.getCompoundedAnimations().addListener(new ViewPagerTransitionAnimator.ViewPagerTransitionAnimationListener(var3));
      boolean var4;
      if(this.transitionAnimationQueue.size() == 0) {
        var4 = true;
      } else {
        var4 = false;
      }

      this.transitionAnimationQueue.add(var1);
      if(var4) {
        this.startNextTransition();
      }
    }

  }

  private void addTransitionIn(int var1, int var2) {
    if(this.shouldAddTransitionIn(var2)) {
      this.addTransition(((ViewPagerInOutTransitionAnimations)this.pageAnimations.get(Integer.valueOf(var2))).getAnimationsIn(), var1, var2);
    }

  }

  private void addTransitionOut(int var1, int var2) {
    if(this.shouldAddTransitionOut(var1, var2)) {
      this.addTransition(((ViewPagerInOutTransitionAnimations)this.pageAnimations.get(Integer.valueOf(var1))).getAnimationsOut(), var1, var2);
    }

  }

  private boolean isTransitioningFromExistingPage(int var1) {
    return var1 != -1 && this.pageExists(var1);
  }

  private boolean pageExists(int var1) {
    return this.pageAnimations.get(Integer.valueOf(var1)) != null;
  }

  private boolean shouldAddTransitionIn(int var1) {
    return this.pageExists(var1) && ((ViewPagerInOutTransitionAnimations)this.pageAnimations.get(Integer.valueOf(var1))).getAnimationsIn() != null;
  }

  private boolean shouldAddTransitionOut(int var1, int var2) {
    return this.pageExists(var2) && ((ViewPagerInOutTransitionAnimations)this.pageAnimations.get(Integer.valueOf(var1))).getAnimationsOut() != null;
  }

  private void startNextTransition() {
    if(this.transitionAnimationQueue.size() > 0 && !this.hasInProgressTransitions()) {
      ((ViewPagerTransitionAnimation)this.transitionAnimationQueue.get(0)).getCompoundedAnimations().start();
    }

  }

  public void addPageAnimation(int var1, ViewPagerInOutTransitionAnimations var2) {
    this.pageAnimations.put(Integer.valueOf(var1), var2);
  }

  public boolean hasInProgressTransitions() {
    Iterator var1 = this.transitionAnimationQueue.iterator();

    do {
      if(!var1.hasNext()) {
        return false;
      }
    } while(!((ViewPagerTransitionAnimation)var1.next()).isInProgress());

    return true;
  }

  public void pageChanged(int var1, int var2) {
    if(this.isTransitioningFromExistingPage(var1)) {
      this.addTransitionOut(var1, var2);
      this.addTransitionIn(var1, var2);
    }

  }

  public void showFirstPage() {
    this.addTransitionIn(-1, 0);
  }

  public interface PageTransitionCallback {
    void pageTransitionStarted(int var1);
  }

  private class ViewPagerTransitionAnimationListener implements AnimatorListener {
    private int targetPage;

    public ViewPagerTransitionAnimationListener(int var2) {
      this.targetPage = var2;
    }

    public void onAnimationCancel(Animator var1) {
      if(ViewPagerTransitionAnimator.this.transitionAnimationQueue.size() > 0) {
        ((ViewPagerTransitionAnimation)ViewPagerTransitionAnimator.this.transitionAnimationQueue.get(0)).setInProgress(false);
        ((ViewPagerTransitionAnimation)ViewPagerTransitionAnimator.this.transitionAnimationQueue.get(0)).getCompoundedAnimations().removeAllListeners();
        ViewPagerTransitionAnimator.this.transitionAnimationQueue.remove(0);
      }

      ViewPagerTransitionAnimator.this.startNextTransition();
    }

    public void onAnimationEnd(Animator var1) {
      if(ViewPagerTransitionAnimator.this.transitionAnimationQueue.size() > 0) {
        ViewPagerTransitionAnimation var2 = (ViewPagerTransitionAnimation)ViewPagerTransitionAnimator.this.transitionAnimationQueue.get(0);
        var2.setInProgress(false);
        var2.getCompoundedAnimations().removeAllListeners();
        ViewPagerTransitionAnimator.this.transitionAnimationQueue.remove(0);
      }

      ViewPagerTransitionAnimator.this.startNextTransition();
    }

    public void onAnimationRepeat(Animator var1) {
      if(ViewPagerTransitionAnimator.this.transitionAnimationQueue.size() > 0) {
        ((ViewPagerTransitionAnimation)ViewPagerTransitionAnimator.this.transitionAnimationQueue.get(0)).setInProgress(false);
        ((ViewPagerTransitionAnimation)ViewPagerTransitionAnimator.this.transitionAnimationQueue.get(0)).getCompoundedAnimations().removeAllListeners();
        ViewPagerTransitionAnimator.this.transitionAnimationQueue.remove(0);
      }

      ViewPagerTransitionAnimator.this.startNextTransition();
    }

    public void onAnimationStart(Animator var1) {
      if(ViewPagerTransitionAnimator.this.transitionAnimationQueue.size() > 0) {
        if(((ViewPagerTransitionAnimation)ViewPagerTransitionAnimator.this.transitionAnimationQueue.get(0)).isAnimationIn()) {
          ViewPagerTransitionAnimator.this.callback.pageTransitionStarted(this.targetPage);
        }

        ((ViewPagerTransitionAnimation)ViewPagerTransitionAnimator.this.transitionAnimationQueue.get(0)).setInProgress(true);
      }

    }
  }
}
