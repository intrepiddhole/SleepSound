package ipnossoft.rma.ui.parallax;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import ipnossoft.rma.free.R;
import ipnossoft.rma.ui.scroller.RelaxScrollView;
import ipnossoft.rma.ui.scrollview.DisableTouchScrollView;

public class RelaxScrollViewGraphicsAnimator {
  private static final String TRANSLATION_Y = "translationY";
  private int bottomMenuContainerWidthInPixels;
  private DisableTouchScrollView bottomMenuScrollView;
  private Context context;
  private ImageView darkenOverlayImageView;
  private ObjectAnimator fadeBlackAnimation;
  private ObjectAnimator fadeOutBlackAnimation;
  private DisableTouchScrollView frontMountainsScrollView;
  private boolean isFaded = false;
  private ObjectAnimator lightAscentAnimation;
  private int lightContainerWidthInPixels;
  private ObjectAnimator lightDescentAnimation;
  private ImageView lightImageView;
  private DisableTouchScrollView lightScrollView;
  private int mountainWidthInPixels;
  private RelaxScrollView relaxScrollView;
  private DisableTouchScrollView starsScrollView;
  private int starsWidthInPixels;

  public RelaxScrollViewGraphicsAnimator(Context var1, View var2, RelaxScrollView var3) {
    this.context = var1;
    this.relaxScrollView = var3;
    ImageView var4 = (ImageView)var2.findViewById(R.id.background_stars_image_view);
    Glide.with(var1).load(Integer.valueOf(R.drawable.bg_main)).placeholder(R.drawable.bg_main).into(var4);
    var4 = (ImageView)var2.findViewById(R.id.mountains_overlay_image_view);
    Glide.with(var1).load(Integer.valueOf(R.drawable.sounds_parallax_mountains)).placeholder(R.drawable.sounds_parallax_mountains).dontAnimate().into(var4);
    this.lightImageView = (ImageView)var2.findViewById(R.id.background_light_image_view);
    Glide.with(var1).load(Integer.valueOf(R.drawable.bg_moon)).placeholder(R.drawable.bg_moon).dontAnimate().into(this.lightImageView);
    this.darkenOverlayImageView = (ImageView)var2.findViewById(R.id.graphics_dark_overlay_image_view);
    this.frontMountainsScrollView = (DisableTouchScrollView)var2.findViewById(R.id.mountains_overlay_scroll_view);
    this.lightScrollView = (DisableTouchScrollView)var2.findViewById(R.id.background_light_scroll_view);
    this.starsScrollView = (DisableTouchScrollView)var2.findViewById(R.id.background_stars_scroll_view);
    this.mountainWidthInPixels = var1.getResources().getDimensionPixelSize(R.dimen.sounds_parallax_mountain_width);
    this.lightContainerWidthInPixels = var1.getResources().getDimensionPixelSize(R.dimen.light_container_width);
    this.starsWidthInPixels = var1.getResources().getDimensionPixelSize(R.dimen.stars_container_width);
    this.bottomMenuContainerWidthInPixels = var1.getResources().getDimensionPixelSize(R.dimen.bottom_menu_container_width);
  }

  private void fadeBlack() {
    if(!this.isFaded) {
      this.fadeOutBlackAnimation.cancel();
      this.lightAscentAnimation.cancel();
      this.isFaded = true;
      this.fadeBlackAnimation.start();
      this.lightDescentAnimation.start();
    }

  }

  private void fadeOut() {
    if(this.isFaded) {
      this.fadeBlackAnimation.cancel();
      this.lightDescentAnimation.cancel();
      this.isFaded = false;
      this.fadeOutBlackAnimation.start();
      this.lightAscentAnimation.start();
    }

  }

  private void fadeToBlack(int var1) {
    boolean var3 = true;
    if(this.relaxScrollView.getAmbientSoundGroup() != null && this.darkenOverlayImageView != null) {
      int var2 = this.relaxScrollView.getAmbientSoundGroup().getScrollLeft();
      int var5 = this.relaxScrollView.getAmbientSoundGroup().getParentWidth();
      int var4 = this.relaxScrollView.getAmbientSoundGroup().getScrollLeft() - this.relaxScrollView.getAmbientSoundGroup().getParentWidth() / 2;
      boolean var7;
      if(var1 > var2 - var5 && var1 < var4) {
        var7 = true;
      } else {
        var7 = false;
      }

      boolean var6;
      if(var1 >= var4) {
        var6 = var3;
      } else {
        var6 = false;
      }

      if(var7 || var6) {
        this.initFadeAnimations();
        if(!var7) {
          this.fadeOut();
          return;
        }

        this.fadeBlack();
      }
    }

  }

  private void initFadeAnimations() {
    int var1 = (int)this.context.getResources().getDimension(R.dimen.darken_background_height) / 4;
    this.fadeBlackAnimation = ObjectAnimator.ofFloat(this.darkenOverlayImageView, "translationY", new float[]{(float)var1});
    this.fadeBlackAnimation.setDuration(2000L);
    this.fadeBlackAnimation.setInterpolator(new DecelerateInterpolator());
    this.fadeOutBlackAnimation = ObjectAnimator.ofFloat(this.darkenOverlayImageView, "translationY", new float[]{0.0F});
    this.fadeOutBlackAnimation.setDuration(2000L);
    this.fadeOutBlackAnimation.setInterpolator(new DecelerateInterpolator());
    var1 = (int)this.context.getResources().getDimension(R.dimen.main_layout_background_light_margin_bottom);
    this.lightDescentAnimation = ObjectAnimator.ofFloat(this.lightImageView, "translationY", new float[]{(float)(var1 * 2)});
    this.lightDescentAnimation.setDuration(2000L);
    this.lightDescentAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
    this.lightAscentAnimation = ObjectAnimator.ofFloat(this.lightImageView, "translationY", new float[]{0.0F});
    this.lightAscentAnimation.setDuration(2000L);
    this.lightAscentAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
  }

  private void moveParallaxedLayers(int var1, int var2) {
    int var3 = this.relaxScrollView.getMeasuredWidth();
    double var4 = (double)(var2 - var3);
    var4 = (double)var1 / var4;
    var1 = this.mountainWidthInPixels;
    this.frontMountainsScrollView.scrollTo((int)((double)(var1 - var3) * var4), 0);
    var1 = this.lightContainerWidthInPixels;
    this.lightScrollView.scrollTo((int)((double)(var1 - var3) * var4), 0);
    var1 = this.starsWidthInPixels;
    this.starsScrollView.scrollTo((int)((double)(var1 - var3) * var4), 0);
    if(this.bottomMenuScrollView != null) {
      var1 = this.bottomMenuContainerWidthInPixels;
      this.bottomMenuScrollView.scrollTo((int)((double)(var1 - var3) * var4), 0);
    }

  }

  public void handleScroll(int var1, int var2) {
    this.fadeToBlack(var1);
    this.moveParallaxedLayers(var1, var2);
  }

  public void setBottomMenuScrollView(DisableTouchScrollView var1) {
    this.bottomMenuScrollView = var1;
  }
}
