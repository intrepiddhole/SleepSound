package ipnossoft.rma.ui.scroller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout.LayoutParams;
import com.ipnossoft.api.soundlibrary.Sound;

import ipnossoft.rma.free.R;
import ipnossoft.rma.ui.button.IsochronicSoundButton;
import ipnossoft.rma.ui.button.SoundButton;
import ipnossoft.rma.ui.button.SoundButtonGestureListener;
import ipnossoft.rma.ui.scroller.column.IsochronicSoundColumn;
import ipnossoft.rma.ui.scroller.column.SoundColumn;
import ipnossoft.rma.ui.soundinfo.IsochronicInfoActivity;
import ipnossoft.rma.util.DimensionUtils;
import ipnossoft.rma.util.RelaxAnalytics;

public class IsochronicSoundGroup extends SoundGroup {
  private float brainwaveGroupSidePadding;
  private final int[] isochronicColumnBottomMargins;

  public IsochronicSoundGroup(Context var1, RelaxScrollView var2) {
    super(var1, var2);
    this.brainwaveGroupSidePadding = this.getResources().getDimension(R.dimen.brainwave_side_padding);
    this.isochronicColumnBottomMargins = new int[6];
    this.addPaddingColumnLeft((int)this.brainwaveGroupSidePadding);
    this.addPaddingColumnRight((int)this.brainwaveGroupSidePadding + this.getResources().getDimensionPixelSize(R.dimen.brainwave_button_swing_distance));
    this.setClipChildren(false);
    this.setClipToPadding(false);
  }

  private int calculateButtonWidth() {
    return (int)this.getResources().getDimension(R.dimen.brainwave_image_width);
  }

  private void moveRightPaddingColumnRight() {
    if(this.columns.size() > 0 && this.paddingColumnRight != null) {
      ((LayoutParams)this.paddingColumnRight.getLayoutParams()).addRule(1, ((SoundColumn)this.columns.get(this.columns.size() - 1)).getId());
    }

  }

  protected void addSoundInternal(Sound var1, SoundButtonGestureListener var2) {
    IsochronicSoundColumn var3 = new IsochronicSoundColumn(this, var1, this.getContext(), var2);
    allSoundColumns.put(Integer.valueOf(var3.getColumnIndex()), var3);
    var3.createView();
    this.addColumn(var3, this.buttonWidth, -2, this.isochronicColumnBottomMargins[var3.getSoundGroupColumnIndex()]);
  }

  public void createAndAppendRope(RelativeLayout var1, SoundButton var2, int var3) {
    var3 = this.getResources().getDimensionPixelSize(R.dimen.main_sound_button_rope_width);
    int var6 = this.getResources().getDimensionPixelSize(R.dimen.brainwave_rope_bottom_margin_from_top);
    ImageView var4 = new ImageView(this.getContext());
    var4.setImageDrawable(this.getResources().getDrawable(R.drawable.rope_normal));
    var4.setScaleType(ScaleType.FIT_XY);
    ((BitmapDrawable)var4.getDrawable()).setAntiAlias(true);
    LayoutParams var5 = new LayoutParams(var3, -1);
    var5.addRule(2, var2.getId());
    var5.bottomMargin = var6;
    var5.addRule(14);
    var5.addRule(10);
    var1.addView(var4, var5);
  }

  public SoundButton createSoundButton(Sound var1, SoundButtonGestureListener var2) {
    IsochronicSoundButton var3 = new IsochronicSoundButton(this.getContext(), var1, this.buttonWidth, var2);
    this.getButtons().add(var3);
    return var3;
  }

  public float getColumnsPerPage() {
    return 6.0F;
  }

  public int getEvenTopPadding() {
    return 0;
  }

  public int getOddTopPadding() {
    return 0;
  }

  public int getVerticalSoundSpacing() {
    return 0;
  }

  protected void onBeforeAddGroupToContainer(LayoutParams var1) {
  }

  protected void onFinishedAddingSounds(SoundButtonGestureListener var1) {
  }

  public void setViewPortDimensions(int var1, int var2, Activity var3) {
    if(var1 > 0 && var2 > 0) {
      this.buttonWidth = this.calculateButtonWidth();
      this.parentWidth = var1;
      this.initColumnSwingMargin(var1);
      if(this.infoBar == null) {
        this.setupInfoBar(var3);
      }

      int var6 = this.getResources().getDimensionPixelSize(R.dimen.main_binaural_information_height) + this.getResources().getDimensionPixelSize(R.dimen.main_binaural_information_margin);
      var2 = var2 - var6 - this.navigationBarOffset - this.buttonHeight - this.bottomMenuHeight;
      this.columnMargin = (int)(((float)(var1 - (int)(this.getColumnsPerPage() * (float)this.buttonWidth)) - 2.0F * this.brainwaveGroupSidePadding) / (this.getColumnsPerPage() - 1.0F));
      this.isochronicColumnBottomMargins[0] = this.bottomMenuHeight + var6 + (int)((float)var2 * 0.3F);
      this.isochronicColumnBottomMargins[1] = this.bottomMenuHeight + var6 + (int)((float)var2 * 0.92F);
      int[] var7 = this.isochronicColumnBottomMargins;
      var1 = this.bottomMenuHeight;
      float var5 = (float)var2;
      float var4;
      if(DimensionUtils.getHeightDensityIndependantPixels(this.getContext()) <= 592) {
        var4 = 0.45F;
      } else {
        var4 = 0.55F;
      }

      var7[2] = (int)(var4 * var5) + var1 + var6;
      var7 = this.isochronicColumnBottomMargins;
      var1 = this.bottomMenuHeight;
      var5 = (float)var2;
      if(DimensionUtils.getHeightDensityIndependantPixels(this.getContext()) <= 592) {
        var4 = 0.0F;
      } else {
        var4 = 0.1F;
      }

      var7[3] = (int)(var4 * var5) + var1 + var6;
      this.isochronicColumnBottomMargins[4] = this.bottomMenuHeight + var6 + (int)((float)var2 * 0.76F);
      var7 = this.isochronicColumnBottomMargins;
      var1 = this.bottomMenuHeight;
      var5 = (float)var2;
      if(DimensionUtils.getHeightDensityIndependantPixels(this.getContext()) <= 592) {
        var4 = 0.28F;
      } else {
        var4 = 0.38F;
      }

      var7[5] = (int)(var4 * var5) + var1 + var6;
    }

  }

  protected void setupInfoBar(final Activity var1) {
    super.setupInfoBar(var1);
    TextView var2 = (TextView)this.infoBar.findViewById(R.id.sound_info_bar_title);
    TextView var3 = (TextView)this.infoBar.findViewById(R.id.sound_info_bar_text);
    View var4 = this.infoBar.findViewById(R.id.sound_info_bar_button);
    var2.setText(this.getResources().getString(R.string.main_activity_isochronic_tones));
    var3.setText(this.getResources().getString(R.string.main_activity_isochronic_no_headphones_required));
    var4.setOnClickListener(new OnClickListener() {
      public void onClick(View var1x) {
        RelaxAnalytics.logIsochronicsInfoShown();
        RelaxAnalytics.logScreenIsochronicsInfo();
        Intent var2 = new Intent(var1, IsochronicInfoActivity.class);
        var1.startActivity(var2);
      }
    });
  }

  protected void updateColumnLayout() {
    if(this.columns.size() > 0) {
      for(int var1 = 0; var1 < this.isochronicColumnBottomMargins.length; ++var1) {
        ((LayoutParams)((SoundColumn)this.columns.get(var1)).getLayoutParams()).bottomMargin = this.isochronicColumnBottomMargins[var1];
      }
    }

  }

  protected void updatePaddingLayout() {
    this.moveRightPaddingColumnRight();
  }
}
