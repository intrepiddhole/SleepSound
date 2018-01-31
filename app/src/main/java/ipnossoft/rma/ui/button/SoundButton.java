package ipnossoft.rma.ui.button;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build.VERSION;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout.LayoutParams;
import com.ipnossoft.api.soundlibrary.Sound;
import com.ipnossoft.api.soundlibrary.sounds.SoundState;

import ipnossoft.rma.free.R;
import ipnossoft.rma.media.SoundManager;
import ipnossoft.rma.review.ReviewProcess;
import ipnossoft.rma.ui.AutoResizeTwoLineTextView;
import ipnossoft.rma.util.RelaxAnalytics;
import ipnossoft.rma.util.Utils;
import ipnossoft.rma.util.font.RelaxFont;
import ipnossoft.rma.util.font.RelaxFontFactory;
import org.jetbrains.annotations.NotNull;

@SuppressLint("ResourceType")
public class SoundButton extends AnimatedButton implements OnTouchListener {
  private View badgeOverlay;
  private Context context;
  private GestureDetector gestureDetector;
  private SimpleOnGestureListener gestureListener;
  ImageView imageButton;
  int imageHeight;
  int imageWidth;
  private Sound sound;
  private SoundButtonGestureListener soundButtonGestureListener;
  private SoundButtonState state;

  SoundButton(Context var1, Sound var2, int var3, int var4, int var5, SoundButtonGestureListener var6) {
    super(var1, var3, var4, var5);
    this.state = SoundButtonState.NORMAL;
    this.soundButtonGestureListener = var6;
    this.context = var1;
    this.setSound(var2);
    this.setId(Utils.generateUniqueViewId());
    this.setButtonView(this.createButtonView());
    this.initAnimation();
    this.initGestureListener();
    this.disableClipOnParents(this);
    this.gestureDetector = new GestureDetector(this.getContext(), this.gestureListener);
    this.setOnTouchListener(this);
  }

  public SoundButton(Context var1, Sound var2, int var3, SoundButtonGestureListener var4) {
    this(var1, var2, var3, R.anim.rotate_sound_button_1, R.anim.rotate_sound_button_2, var4);
  }

  private void addBadge() {
    this.badgeOverlay.setOnTouchListener(this);
    if(VERSION.SDK_INT >= 21) {
      this.badgeOverlay.setElevation(this.getResources().getDimension(R.dimen.pro_badge_elevation));
    }

    this.badgeOverlay.setClickable(false);
    this.badgeOverlay.requestLayout();
    int var2 = (int)this.getResources().getDimension(R.dimen.pro_badge_size);
    LayoutParams var1 = new LayoutParams(var2, var2);
    var1.addRule(9, -1);
    var1.addRule(10, -1);
    var1 = this.correctTextOverlayLayoutParams(var1);
    this.badgeOverlay.setVisibility(View.VISIBLE);
    ((RelativeLayout)this.imageButton.getParent()).addView(this.badgeOverlay, var1);
  }

  private void adjustLayoutParamsForContainer(RelativeLayout var1) {
    LayoutParams var2 = new LayoutParams(-2, -2);
    var2.addRule(10);
    var2.addRule(14);
    var1.setLayoutParams(var2);
  }

  private void applyDownloadBadge() {
    if(this.badgeOverlay != null) {
      this.removeBadgeOverlay();
    }

    ImageView var1 = new ImageView(this.getContext());
    var1.setBackgroundResource(R.drawable.badge_download);
    var1.setImageResource(R.drawable.puck_download);
    var1.setScaleType(ScaleType.CENTER);
    this.badgeOverlay = var1;
    this.addBadge();
  }

  private void applyGiftBadge() {
    if(this.badgeOverlay != null) {
      this.removeBadgeOverlay();
    }

    ImageView var1 = new ImageView(this.getContext());
    var1.setBackgroundResource(R.drawable.badge_gift);
    var1.setImageResource(R.drawable.icon_gift);
    var1.setScaleType(ScaleType.CENTER);
    this.badgeOverlay = var1;
    this.addBadge();
  }

  @SuppressLint("ResourceType")
  private void applyLockedBadge() {
    if(this.badgeOverlay != null) {
      this.removeBadgeOverlay();
    }

    TextView var1 = new TextView(this.getContext());
    RelaxFont var2 = RelaxFontFactory.INSTANCE.getExtraSmallLabelFont(this.context);
    var1.setMaxLines(1);
    var1.setText(this.getResources().getString(R.string.main_activity_pro_badge));
    var1.setTextSize(0, var2.getSize());
    var1.setTypeface(RelaxFontFactory.INSTANCE.getSemiBoldTypeFace(this.context));
    var1.setBackgroundResource(R.drawable.badge_pro);
    var1.setGravity(17);
    var1.setTextColor(this.getResources().getColor(17170443));
    this.badgeOverlay = var1;
    this.addBadge();
  }

  private void createAndAppendLabel(View var1) {
    String var3 = this.sound.getName();
    RelaxFont var4 = RelaxFontFactory.INSTANCE.getSmallRegularFont(this.context);
    AutoResizeTwoLineTextView var2 = new AutoResizeTwoLineTextView(this.getContext());
    var2.setDefaultStartingTextSize(var4.getSize());
    var2.setMaxLines(2);
    var2.setText(var3);
    var2.setClickable(false);
    var2.setLineSpacing(0.0F, 0.8F);
    this.doSetupTextView(var2);
    LayoutParams var6 = new LayoutParams(-1, (int)this.context.getResources().getDimension(R.dimen.sound_button_label_height));
    var6.addRule(3, var1.getId());
    LayoutParams var5 = this.correctTextLayoutParams(var6);
    var2.setGravity(49);
    this.addView(var2, var5);
  }

  @NotNull
  private ImageView createImageButton() {
    CustomSelectableButton var1 = new CustomSelectableButton(this.getContext());
    var1.setId(Utils.generateUniqueViewId());
    var1.setOnTouchListener(this);
    var1.setClickable(false);
    var1.setBackgroundColor(this.getResources().getColor(17170445));
    var1.setScaleType(ScaleType.FIT_CENTER);
    var1.setNormalResourceId(this.sound.getNormalImageResourceId());
    var1.setSelectedResourceId(this.sound.getSelectedImageResourceId());
    return var1;
  }

  private ImageView createNormalImageButton() {
    return this.createImageButton();
  }

  private void doSetupTextView(TextView var1) {
    var1.setTextColor(this.getResources().getColor(17170443));
    var1.setGravity(17);
    var1.setPadding(0, (int)this.getResources().getDimension(R.dimen.sound_image_label_spacing), 0, 0);
    var1.setTypeface(RelaxFontFactory.INSTANCE.getNormalTypeFace(this.context));
    this.doSetupTextViewForXMLLayout(var1);
  }

  private void initGestureListener() {
    this.gestureListener = new SimpleOnGestureListener() {
      public boolean onDoubleTap(MotionEvent var1) {
        if(SoundButton.this.soundButtonGestureListener != null && SoundButton.this.soundButtonGestureListener.onDoubleButtonTap(SoundButton.this)) {
          return true;
        } else {
          SoundButton.this.onDoubleTap((Activity)SoundButton.this.getContext());
          return true;
        }
      }

      public void onLongPress(MotionEvent var1) {
        if(SoundButton.this.soundButtonGestureListener == null || !SoundButton.this.soundButtonGestureListener.onLongPress(SoundButton.this)) {
          SoundButton.this.onLongPress((Activity)SoundButton.this.getContext());
        }
      }

      public boolean onSingleTapConfirmed(MotionEvent var1) {
        if(SoundButton.this.soundButtonGestureListener != null && SoundButton.this.soundButtonGestureListener.onSingleButtonTap(SoundButton.this)) {
          return true;
        } else {
          SoundButton.this.onSingleTap();
          return true;
        }
      }
    };
  }

  private void onDoubleTap(Activity var1) {
    RelaxAnalytics.logShowedSubVolumeWithDoubleTap(this.sound);
    SoundManager.getInstance().showVolumeLayout(var1, this.sound.getId());
  }

  private void onLongPress(Activity var1) {
    RelaxAnalytics.logShowedSubVolumeWithLongPress(this.sound);
    SoundManager.getInstance().showVolumeLayout(var1, this.sound.getId());
  }

  private void onSingleTap() {
    if(ReviewProcess.getInstance().isSoundGifted(this.sound) && this.badgeOverlay != null) {
      this.badgeOverlay.animate().alpha(0.0F);
    }

    SoundManager.getInstance().handleSoundPressed(this.getSound(), (Activity)this.getContext());
  }

  private void removeBadgeOverlay() {
    this.badgeOverlay.setVisibility(View.GONE);
    this.removeView(this.badgeOverlay);
    this.badgeOverlay.invalidate();
    this.badgeOverlay = null;
  }

  private void setSound(Sound var1) {
    this.sound = var1;
  }

  private void startScaleAnimationLoopOnGiftIcon() {
    if(this.badgeOverlay != null) {
      Animation var1 = AnimationUtils.loadAnimation(this.context, R.anim.pulse);
      this.badgeOverlay.startAnimation(var1);
    }

  }

  public void addToContainer(RelativeLayout var1, LayoutParams var2) {
    super.addToContainer(var1, var2);
    this.update(this.sound);
  }

  void calculateImageDimensions() {
    this.imageWidth = (int)this.getResources().getDimension(R.dimen.sound_image_width);
    this.imageHeight = (int)this.getResources().getDimension(R.dimen.sound_image_height);
  }

  LayoutParams correctImageLayoutParams(LayoutParams var1) {
    var1.addRule(14);
    var1.addRule(10);
    return var1;
  }

  LayoutParams correctTextLayoutParams(LayoutParams var1) {
    var1.topMargin = this.getResources().getDimensionPixelSize(R.dimen.main_sound_button_text_top_margin);
    return var1;
  }

  LayoutParams correctTextOverlayLayoutParams(LayoutParams var1) {
    var1.topMargin = (int)((double)this.imageHeight * 0.21D - (double)(this.getResources().getDimension(R.dimen.pro_badge_size) / 2.0F));
    var1.leftMargin = (int)((double)this.imageWidth * 0.73D - (double)(this.getResources().getDimension(R.dimen.pro_badge_size) / 2.0F));
    return var1;
  }

  protected View createButtonView() {
    RelativeLayout var1 = new RelativeLayout(this.getContext());
    this.adjustLayoutParamsForContainer(var1);
    var1.setId(Utils.generateUniqueViewId());
    var1.setClickable(false);
    this.calculateImageDimensions();
    LayoutParams var2 = this.correctImageLayoutParams(new LayoutParams(this.imageWidth, this.imageHeight));
    this.imageButton = this.createNormalImageButton();
    var1.addView(this.imageButton, var2);
    this.setButtonStateNormal();
    this.addView(var1);
    this.createAndAppendLabel(var1);
    this.setAnimatedView(var1);
    this.setClickable(true);
    return this.imageButton;
  }

  protected void disableClipOnParents(View var1) {
    if(var1.getParent() != null) {
      if(var1 instanceof ViewGroup) {
        ((ViewGroup)var1).setClipChildren(false);
        ((ViewGroup)var1).setClipToPadding(false);
      }

      if(var1.getParent() instanceof View) {
        this.disableClipOnParents((View)var1.getParent());
        return;
      }
    }

  }

  protected void doSetupTextViewForXMLLayout(TextView var1) {
    var1.setShadowLayer(6.0F, 2.0F, 3.0F, -1728053248);
    var1.setOnTouchListener(this);
  }

  public void flashGiftedSpecialEffect() {
    this.startScaleAnimationLoopOnGiftIcon();
  }

  public int getImageHeight() {
    return this.imageHeight;
  }

  public int getImageWidth() {
    return this.imageWidth;
  }

  public Sound getSound() {
    return this.sound;
  }

  protected float getTextSize() {
    return this.getResources().getDimension(R.dimen.small_general_font_size);
  }

  public boolean isLocked() {
    return SoundManager.getInstance().isLocked(this.getSound());
  }

  public boolean onTouch(View var1, MotionEvent var2) {
    return this.gestureDetector.onTouchEvent(var2);
  }

  void setButtonStateDownloadable() {
    if(this.state != SoundButtonState.DOWNLOADABLE) {
      this.state = SoundButtonState.DOWNLOADABLE;
      this.applyDownloadBadge();
    }

  }

  void setButtonStateGifted() {
    if(this.state != SoundButtonState.GIFT) {
      this.state = SoundButtonState.GIFT;
      this.applyGiftBadge();
    }

  }

  void setButtonStateLocked() {
    if(this.state != SoundButtonState.LOCKED) {
      this.state = SoundButtonState.LOCKED;
      this.applyLockedBadge();
    }

  }

  protected void setButtonStateNormal() {
    if(this.state != SoundButtonState.NORMAL) {
      this.state = SoundButtonState.NORMAL;
      if(this.badgeOverlay != null) {
        this.removeBadgeOverlay();
      }
    }

  }

  public void setSelected(boolean var1) {
    super.setSelected(var1);
  }

  void setSoundImage(ImageView var1) {
    try {
      if(var1 instanceof CustomSelectableButton) {
        ((CustomSelectableButton)var1).loadCurrentImage();
      } else {
        Utils.setSoundImage(this.sound, var1);
      }
    } catch (OutOfMemoryError var2) {
      Log.e("Memory Error", "Out of Memory when setting image for sound " + this.sound.getName());
    }
  }

  public void setupCustomSelectableButton(CustomSelectableButton var1) {
    var1.setOnTouchListener(this);
    var1.setClickable(false);
    var1.setNormalResourceId(this.sound.getNormalImageResourceId());
    var1.setSelectedResourceId(this.sound.getSelectedImageResourceId());
  }

  public void update(Sound var1) {
    this.sound = var1;
    this.setSoundImage(this.imageButton);
    if(this.isLocked()) {
      this.setButtonStateLocked();
    } else {
      if(ReviewProcess.getInstance().isSoundGifted(var1)) {
        this.setButtonStateGifted();
        return;
      }

      if(!var1.isPlayable()) {
        this.setButtonStateDownloadable();
        return;
      }

      this.setButtonStateNormal();
      if(SoundManager.getInstance() != null) {
        this.update(SoundManager.getInstance().getSoundState(var1.getId()));
        return;
      }
    }

  }

  public void update(SoundState var1) {
    if(var1 == SoundState.STOPPED) {
      this.setSelected(false);
      this.setAnimated(false);
    } else {
      if(var1 == SoundState.PAUSED) {
        this.setSelected(true);
        this.setAnimated(false);
        return;
      }

      if(var1 == SoundState.PLAYING) {
        this.setSelected(true);
        this.setAnimated(true);
        return;
      }
    }

  }
}
