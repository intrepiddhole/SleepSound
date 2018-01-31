package ipnossoft.rma.animation;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Handler;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import java.util.Random;

public class ShootingStarAnimator {
  public static final long ANIMATION_DURATION = 800L;
  private final int MAXIMUM_DELAY_SHOOTING_STAR_ANIMATION = 12000;
  private final int MINIMUM_DELAY_SHOOTING_STAR_ANIMATION = 4000;
  private AnimatorSet animation;
  private View parentView;
  private ShootingStarEnabler shootingStarEnabler;
  private ImageView shootingStarImageView;

  public ShootingStarAnimator(ImageView var1, View var2, ShootingStarEnabler var3) {
    this.shootingStarImageView = var1;
    this.parentView = var2;
    this.shootingStarEnabler = var3;
  }

  private AnimatorSet combineBothTypesOfAnimatorSets(AnimatorSet var1, AnimatorSet var2) {
    AnimatorSet var3 = new AnimatorSet();
    var3.playTogether(new Animator[]{var1, var2});
    return var3;
  }

  private AnimatorSet createCombinedAnimations() {
    return this.combineBothTypesOfAnimatorSets(this.createFadeInAndOutAnimatorSet(this.createFadeInAnimation(), this.createFadeOutAnimation()), this.createTranslationAnimatorSet());
  }

  private AnimatorSet createFadeInAndOutAnimatorSet(Animator var1, Animator var2) {
    AnimatorSet var3 = new AnimatorSet();
    var3.playSequentially(new Animator[]{var1, var2});
    return var3;
  }

  private Animator createFadeInAnimation() {
    ObjectAnimator var1 = ObjectAnimator.ofFloat(this.shootingStarImageView, View.ALPHA, new float[]{0.0F, 1.0F});
    var1.setDuration(400L);
    return var1;
  }

  private Animator createFadeOutAnimation() {
    ObjectAnimator var1 = ObjectAnimator.ofFloat(this.shootingStarImageView, View.ALPHA, new float[]{1.0F, 0.0F});
    var1.setDuration(400L);
    return var1;
  }

  private AnimatorSet createTranslationAnimatorSet() {
    AnimatorSet var1 = new AnimatorSet();
    ObjectAnimator var2 = ObjectAnimator.ofFloat(this.shootingStarImageView, View.TRANSLATION_Y, new float[]{this.shootingStarImageView.getY(), this.shootingStarImageView.getY() + (float)this.shootingStarImageView.getHeight() * 2.0F});
    var2.setDuration(800L);
    ObjectAnimator var3 = ObjectAnimator.ofFloat(this.shootingStarImageView, View.TRANSLATION_X, new float[]{this.shootingStarImageView.getX(), this.shootingStarImageView.getX() - (float)this.shootingStarImageView.getWidth() * 2.0F});
    var3.setDuration(800L);
    var1.playTogether(new Animator[]{var3, var2});
    var1.setInterpolator(new LinearInterpolator());
    return var1;
  }

  private int getRandomLeftMarginInDP() {
    return this.getRandomNumberBetweenNumbers(this.shootingStarImageView.getWidth(), this.parentView.getWidth());
  }

  private int getRandomNumberBetweenNumbers(int var1, int var2) {
    return (new Random()).nextInt(Math.max(var2 - var1, 1)) + var1;
  }

  private int getRandomTopMarginInDP() {
    return this.getRandomNumberBetweenNumbers(0, this.parentView.getHeight() - this.shootingStarImageView.getHeight() * 2);
  }

  private void moveImageViewToRandomPosition() {
    LayoutParams var1 = (LayoutParams)this.shootingStarImageView.getLayoutParams();
    var1.setMargins(this.getRandomLeftMarginInDP(), this.getRandomTopMarginInDP(), 0, 0);
    this.shootingStarImageView.setLayoutParams(var1);
  }

  private void shootStar() {
    this.moveImageViewToRandomPosition();
    this.shootingStarImageView.setVisibility(View.VISIBLE);
    if(this.animation == null) {
      this.animation = this.createCombinedAnimations();
    }

    this.animation.start();
  }

  public void start() {
    final Handler var1 = new Handler();
    var1.postDelayed(new Runnable() {
      public void run() {
        if(ShootingStarAnimator.this.shootingStarEnabler.shouldShootStar()) {
          ShootingStarAnimator.this.shootStar();
        }

        var1.postDelayed(this, (long)ShootingStarAnimator.this.getRandomNumberBetweenNumbers(4000, 12000));
      }
    }, (long)this.getRandomNumberBetweenNumbers(4000, 12000));
  }

  public interface ShootingStarEnabler {
    boolean shouldShootStar();
  }
}
