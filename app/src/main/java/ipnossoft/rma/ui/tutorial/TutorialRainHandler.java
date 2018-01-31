package ipnossoft.rma.ui.tutorial;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.Animator.AnimatorListener;
import android.app.Activity;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import ipnossoft.rma.free.R;

public class TutorialRainHandler {
  private Activity activity;
  private boolean isRainStopped = false;
  private ObjectAnimator rainFallAnimator1;
  private ObjectAnimator rainFallAnimator2;
  private ObjectAnimator rainFallAnimator3;
  private ObjectAnimator rainFallAnimator4;
  private int rainHeight = 0;

  public TutorialRainHandler(Activity var1) {
    this.activity = var1;
    this.init();
  }

  private ObjectAnimator createRainFallAnimator(ImageView var1) {
    ObjectAnimator var2 = ObjectAnimator.ofFloat(var1, "translationY", new float[]{(float)(-this.rainHeight), (float)this.rainHeight});
    var2.setInterpolator(new LinearInterpolator());
    var2.setDuration(3000L);
    var2.setRepeatCount(-1);
    var2.addListener(this.createRainFallListener(var1));
    return var2;
  }

  @NonNull
  private AnimatorListener createRainFallListener(final ImageView var1) {
    return new AnimatorListener() {
      public void onAnimationCancel(Animator var1x) {
      }

      public void onAnimationEnd(Animator var1x) {
        ObjectAnimator var2 = ObjectAnimator.ofFloat(var1, "alpha", new float[]{1.0F, 0.0F});
        var2.setDuration(500L);
        var2.start();
      }

      public void onAnimationRepeat(Animator var1x) {
        var1x.setInterpolator(new LinearInterpolator());
        if(TutorialRainHandler.this.isRainStopped) {
          var1x.end();
          var1.setVisibility(View.GONE);
        }

      }

      public void onAnimationStart(Animator var1x) {
        var1.setVisibility(View.VISIBLE);
        ObjectAnimator var2 = ObjectAnimator.ofFloat(var1, "alpha", new float[]{0.0F, 1.0F});
        var2.setDuration(500L);
        var2.start();
      }
    };
  }

  private void init() {
    this.rainHeight = this.activity.getResources().getDimensionPixelSize(R.dimen.tutorial_rain_height);
    this.rainFallAnimator1 = this.createRainFallAnimator((ImageView)this.activity.findViewById(R.id.tutorial_rainfall_1));
    this.rainFallAnimator2 = this.createRainFallAnimator((ImageView)this.activity.findViewById(R.id.tutorial_rainfall_2));
    this.rainFallAnimator3 = this.createRainFallAnimator((ImageView)this.activity.findViewById(R.id.tutorial_rainfall_3));
    this.rainFallAnimator4 = this.createRainFallAnimator((ImageView)this.activity.findViewById(R.id.tutorial_rainfall_4));
  }

  private void startRainAnimatorAfterDelay(final ObjectAnimator var1, int var2) {
    (new Handler()).postDelayed(new Runnable() {
      public void run() {
        var1.start();
      }
    }, (long)var2);
  }

  public void makeItRain() {
    this.startRainAnimatorAfterDelay(this.rainFallAnimator1, 0);
    this.startRainAnimatorAfterDelay(this.rainFallAnimator2, 750);
    this.startRainAnimatorAfterDelay(this.rainFallAnimator3, 1500);
    this.startRainAnimatorAfterDelay(this.rainFallAnimator4, 2250);
  }

  public void stopTheRain() {
    this.isRainStopped = true;
  }
}
