package ipnossoft.rma.ui.scroller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout.LayoutParams;
import com.ipnossoft.api.soundlibrary.Sound;
import com.ipnossoft.api.soundlibrary.SoundLibrary;
import ipnossoft.rma.MainActivity;
import ipnossoft.rma.free.R;
import ipnossoft.rma.ui.button.SoundButton;
import ipnossoft.rma.ui.button.SoundButtonGestureListener;
import ipnossoft.rma.ui.scroller.column.AmbientSoundColumn;
import ipnossoft.rma.ui.scroller.column.MultipleSoundColumn;
import java.util.ArrayList;
import java.util.List;

@SuppressLint({"ViewConstructor"})
public class AmbientSoundGroup extends SoundGroup {
  private static final float SOUND_BUTTON_KNOT_X_RATIO = 0.5F;
  public static final float SOUND_BUTTON_KNOT_Y_RATIO = 0.17447917F;
  private List<Sound> columnSounds = new ArrayList();
  private boolean even;
  private int evenTopPadding;
  private int groupBottomMarginToScrollBar;
  private boolean lastColumnNotFull;
  private int negativeGroupLeftMargin;
  private int oddTopPadding;
  private int oldHeight;
  private int soundsPerColumn = this.getSoundsPerColumn();
  private int verticalSoundSpacing;

  public AmbientSoundGroup(Context var1, RelaxScrollView var2) {
    super(var1, var2);
    this.negativeGroupLeftMargin = (int)this.getResources().getDimension(R.dimen.negative_sound_group_margin_left);
    this.groupBottomMarginToScrollBar = (int)this.getResources().getDimension(R.dimen.sound_group_bottom_margin_to_scroll_bar);
  }

  private void adjustVariablesForNewHeight(int var1) {
    this.oddTopPadding = (int)this.getResources().getDimension(R.dimen.sounds_odd_top_padding);
    this.evenTopPadding = (int)this.getResources().getDimension(R.dimen.sounds_even_top_padding);
    this.soundsPerColumn = this.getSoundsPerColumn();
    this.verticalSoundSpacing = (var1 - this.buttonHeight * this.soundsPerColumn - this.navigationBarOffset - this.oddTopPadding - this.bottomMenuHeight - this.groupBottomMarginToScrollBar) / (this.soundsPerColumn - 1);
  }

  private void createColumnAndAddSounds(SoundButtonGestureListener var1) {
    if(this.lastColumnNotFull && this.columnSounds.size() == this.soundsPerColumn) {
      this.lastColumnNotFull = false;
      final MultipleSoundColumn var4 = (MultipleSoundColumn)this.getLastColumn();
      (new Handler(Looper.getMainLooper())).post(new Runnable() {
        public void run() {
          var4.destroyView();
          var4.createView();
        }
      });
      this.columnSounds = new ArrayList();
    } else {
      AmbientSoundColumn var3 = new AmbientSoundColumn(this, this.columnSounds, this.even, this.getContext(), var1);
      boolean var2;
      if(!this.even) {
        var2 = true;
      } else {
        var2 = false;
      }

      this.even = var2;
      if(!this.lastColumnNotFull) {
        this.columnSounds = new ArrayList();
      }

      allSoundColumns.put(Integer.valueOf(var3.getColumnIndex()), var3);
      var3.createView();
      this.addColumn(var3, this.buttonWidth, -1, 0);
    }
  }

  private void rebuild(Activity var1) {
    this.removeAllViewsAndColumns();
    this.even = false;
    this.columnSounds.clear();
    this.lastColumnNotFull = false;
    this.addSounds(SoundLibrary.getInstance().getSortedAmbientSounds(), (MainActivity)var1);
  }

  protected void addSoundInternal(Sound var1, SoundButtonGestureListener var2) {
    this.columnSounds.add(var1);
    if(this.columnSounds.size() == this.soundsPerColumn) {
      this.createColumnAndAddSounds(var2);
    }

  }

  public void createAndAppendRope(RelativeLayout var1, SoundButton var2, int var3) {
    int var6 = this.getResources().getDimensionPixelSize(R.dimen.main_sound_button_rope_width);
    ImageView var4 = new ImageView(this.getContext());
    var4.setScaleType(ScaleType.FIT_XY);
    var4.setImageDrawable(this.getResources().getDrawable(R.drawable.rope_normal));
    LayoutParams var5 = new LayoutParams(var6, -1);
    var5.addRule(2, var2.getId());
    var5.addRule(14);
    var5.addRule(10);
    if(var3 == 1) {
      var6 = -this.navigationBarOffset;
      if(this.even) {
        var3 = this.evenTopPadding;
      } else {
        var3 = this.oddTopPadding;
      }

      var5.bottomMargin = (int)((float)(var6 - var3) - (float)this.buttonHeight * 0.25F);
    } else {
      var5.bottomMargin = (int)(-((float)this.buttonHeight * 0.5F + (float)this.getVerticalSoundSpacing()));
    }

    var1.addView(var4, var5);
  }

  public SoundButton createSoundButton(Sound var1, SoundButtonGestureListener var2) {
    SoundButton var3 = new SoundButton(this.getContext(), var1, this.buttonWidth, var2);
    var3.getAnimatedView().setPivotY((float)var3.getImageHeight() * 0.17447917F);
    var3.getAnimatedView().setPivotX((float)var3.getImageWidth() * 0.5F);
    return var3;
  }

  public int getEvenTopPadding() {
    return this.evenTopPadding;
  }

  public int getOddTopPadding() {
    return this.oddTopPadding;
  }

  public int getVerticalSoundSpacing() {
    return this.verticalSoundSpacing;
  }

  protected void onBeforeAddGroupToContainer(LayoutParams var1) {
    var1.leftMargin = -this.negativeGroupLeftMargin;
  }

  protected void onFinishedAddingSounds(SoundButtonGestureListener var1) {
    if(this.columnSounds.size() != this.soundsPerColumn) {
      this.lastColumnNotFull = true;
    }

    if(!this.columnSounds.isEmpty()) {
      this.createColumnAndAddSounds(var1);
    }

  }

  public void setViewPortDimensions(int var1, int var2, Activity var3) {
    super.setViewPortDimensions(var1, var2, var3);
    if(var1 > 0 && var2 > 0) {
      this.adjustVariablesForNewHeight(var2);
      if(this.oldHeight == 0 || this.oldHeight == var2) {
        this.oldHeight = var2;
        return;
      }

      this.rebuild(var3);
      this.oldHeight = var2;
    }

  }

  protected void setupInfoBar(Activity var1) {
  }

  protected void updateColumnLayout() {
  }

  protected void updatePaddingLayout() {
  }
}
