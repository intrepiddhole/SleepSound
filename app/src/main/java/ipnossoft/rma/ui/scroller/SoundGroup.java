package ipnossoft.rma.ui.scroller;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import com.ipnossoft.api.soundlibrary.Sound;
import ipnossoft.rma.RelaxMelodiesApp;
import ipnossoft.rma.free.R;
import ipnossoft.rma.ui.button.SoundButton;
import ipnossoft.rma.ui.button.SoundButtonGestureListener;
import ipnossoft.rma.ui.scroller.column.SoundColumn;
import ipnossoft.rma.util.DimensionUtils;
import ipnossoft.rma.util.UserExperienceManager;
import ipnossoft.rma.util.Utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public abstract class SoundGroup extends RelativeLayout {
  private static volatile int COLUMN_SWING_MARGIN = 0;
  static HashMap<Integer, SoundColumn> allSoundColumns = new HashMap();
  int bottomMenuHeight;
  int buttonHeight;
  int buttonWidth;
  private List<SoundButton> buttons;
  int columnMargin;
  List<SoundColumn> columns;
  RelativeLayout infoBar;
  int navigationBarOffset;
  private RelativeLayout paddingColumnLeft;
  RelativeLayout paddingColumnRight;
  int parentWidth;
  private RelaxScrollView relaxScrollView;
  private int scrollOffset;
  private HashMap<String, Sound> sounds;

  SoundGroup(Context var1, RelaxScrollView var2) {
    super(var1);
    this.buttonHeight = (int)this.getResources().getDimension(R.dimen.sound_button_height);
    this.setId(Utils.generateUniqueViewId());
    this.relaxScrollView = var2;
    this.columns = new ArrayList();
    this.buttons = new ArrayList();
    this.sounds = new HashMap();
    this.setVisibility(View.GONE);
    this.navigationBarOffset = (int)this.getResources().getDimension(R.dimen.navigation_bar_height);
    this.bottomMenuHeight = (int)this.getResources().getDimension(R.dimen.sound_group_bottom_spacing);
  }

  private void clear() {
    Iterator var1 = this.buttons.iterator();

    while(var1.hasNext()) {
      ((SoundButton)var1.next()).removeFromContainer();
    }

    var1 = this.columns.iterator();

    while(var1.hasNext()) {
      this.removeView((RelativeLayout)var1.next());
    }

    this.columns.clear();
    this.buttons.clear();
    this.sounds.clear();
  }

  @NonNull
  private LayoutParams createColumnLayoutParams(int var1, int var2, RelativeLayout var3) {
    LayoutParams var4 = new LayoutParams(var1, var2);
    if(this.columns.size() > 0) {
      var4.leftMargin = this.columnMargin;
    }

    if(var3 != null) {
      var4.addRule(1, var3.getId());
    }

    return var4;
  }

  void addColumn(SoundColumn var1, int var2, int var3, int var4) {
    RelativeLayout var5 = null;
    if(this.columns.size() > 0) {
      var5 = (RelativeLayout)this.columns.get(this.columns.size() - 1);
    } else if(this.paddingColumnLeft != null) {
      var5 = this.paddingColumnLeft;
    }

    LayoutParams var6 = this.createColumnLayoutParams(var2, var3, var5);
    var6.bottomMargin = var4;
    this.addView(var1, var6);
    this.columns.add(var1);
  }

  public void addGroupToContainer(RelativeLayout var1, int var2) {
    LayoutParams var3 = new LayoutParams(-2, -1);
    if(var2 > 0) {
      var3.addRule(1, var2);
    }

    this.onBeforeAddGroupToContainer(var3);
    var1.addView(this, var3);
    this.updatePaddingLayout();
  }

  void addPaddingColumnLeft(int var1) {
    this.paddingColumnLeft = new RelativeLayout(this.getContext());
    this.paddingColumnLeft.setId(Utils.generateUniqueViewId());
    LayoutParams var2 = new LayoutParams(var1, -1);
    var2.addRule(9, -1);
    this.addView(this.paddingColumnLeft, var2);
  }

  void addPaddingColumnRight(int var1) {
    this.paddingColumnRight = new RelativeLayout(this.getContext());
    this.paddingColumnRight.setId(Utils.generateUniqueViewId());
    LayoutParams var2 = new LayoutParams(var1, -1);
    var2.addRule(11, -1);
    this.addView(this.paddingColumnRight, var2);
  }

  void addSound(Sound var1, SoundButtonGestureListener var2) {
    if(!this.sounds.containsKey(var1.getId())) {
      this.addSoundInternal(var1, var2);
      this.sounds.put(var1.getId(), var1);
    }

  }

  protected abstract void addSoundInternal(Sound var1, SoundButtonGestureListener var2);

  public <T extends Sound> void addSounds(List<T> var1, SoundButtonGestureListener var2) {
    Iterator var3 = var1.iterator();

    while(var3.hasNext()) {
      Sound var4 = (Sound)var3.next();
      if(!this.sounds.containsKey(var4.getId())) {
        this.addSoundInternal(var4, var2);
        this.sounds.put(var4.getId(), var4);
      }
    }

    if(!var1.isEmpty()) {
      this.onFinishedAddingSounds(var2);
    }

  }

  public abstract void createAndAppendRope(RelativeLayout var1, SoundButton var2, int var3);

  public abstract SoundButton createSoundButton(Sound var1, SoundButtonGestureListener var2);

  public int getButtonHeight() {
    return this.buttonHeight;
  }

  public int getButtonWidth() {
    return this.buttonWidth;
  }

  List<SoundButton> getButtons() {
    return this.buttons;
  }

  public int getColumnMargin() {
    return this.columnMargin;
  }

  float getColumnsPerPage() {
    switch(UserExperienceManager.getScreenSize(this.getContext())) {
      case ExtraLarge:
        return 5.47F;
      case Large:
      default:
        return 4.47F;
      case Normal:
        return 3.47F;
      case Small:
        return 3.47F;
    }
  }

  public abstract int getEvenTopPadding();

  public RelativeLayout getLastColumn() {
    return this.columns.size() == 0?null:(RelativeLayout)this.columns.get(this.columns.size() - 1);
  }

  public int getNavigationBarOffset() {
    return this.navigationBarOffset;
  }

  public abstract int getOddTopPadding();

  public int getParentWidth() {
    return this.parentWidth;
  }

  public RelaxScrollView getRelaxScrollView() {
    return this.relaxScrollView;
  }

  public int getScrollLeft() {
    int var2 = this.getLeft();
    int var1;
    if(this.paddingColumnLeft != null) {
      var1 = COLUMN_SWING_MARGIN;
    } else {
      var1 = 0;
    }

    return var1 + var2;
  }

  public int getScrollOffset() {
    return this.scrollOffset;
  }

  public int getScrollRight() {
    return this.getRight();
  }

  public HashMap<String, Sound> getSounds() {
    return this.sounds;
  }

  int getSoundsPerColumn() {
    boolean var2;
    if(DimensionUtils.getHeightDensityIndependantPixels(this.getContext()) > 640) {
      var2 = true;
    } else {
      var2 = false;
    }

    boolean var1 = RelaxMelodiesApp.getInstance().areAdsEnabled();
    if(var2 && !var1) {
      var2 = true;
    } else {
      var2 = false;
    }

    switch(this.getContext().getResources().getConfiguration().screenLayout & 15) {
      case 1:
        return 3;
      case 2:
        byte var3;
        if(var2) {
          var3 = 4;
        } else {
          var3 = 3;
        }

        return var3;
      default:
        if(!var2) {
          return 3;
        }
      case 3:
      case 4:
        return 4;
    }
  }

  public abstract int getVerticalSoundSpacing();

  void initColumnSwingMargin(int var1) {
    if(COLUMN_SWING_MARGIN == 0) {
      COLUMN_SWING_MARGIN = (int)((float)var1 / this.getColumnsPerPage());
    }

  }

  protected abstract void onBeforeAddGroupToContainer(LayoutParams var1);

  protected abstract void onFinishedAddingSounds(SoundButtonGestureListener var1);

  void removeAllViewsAndColumns() {
    Iterator var1 = this.columns.iterator();

    while(var1.hasNext()) {
      SoundColumn var2 = (SoundColumn)var1.next();
      var2.removeAllViews();
      var2.destroyView();
      this.removeView(var2);
      allSoundColumns.remove(Integer.valueOf(var2.getColumnIndex()));
    }

    this.columns.clear();
    this.sounds.clear();
    this.clear();
  }

  public void setScrollOffset(int var1) {
    this.scrollOffset = var1;
  }

  public void setSounds(HashMap<String, Sound> var1) {
    this.sounds = var1;
  }

  public void setViewPortDimensions(int var1, int var2, Activity var3) {
    if(var1 > 0 && var2 > 0) {
      this.buttonWidth = (int)((float)var1 / this.getColumnsPerPage());
      this.parentWidth = var1;
      this.initColumnSwingMargin(var1);
      if(this.infoBar == null) {
        this.setupInfoBar(var3);
      }
    }

  }

  void setupInfoBar(Activity var1) {
    this.infoBar = (RelativeLayout)var1.getLayoutInflater().inflate(R.layout.sound_info_bar, (ViewGroup)null);
    this.infoBar.setId(Utils.generateUniqueViewId());
    LayoutParams var2 = new LayoutParams(this.getParentWidth(), -2);
    var2.addRule(12, -1);
    var2.addRule(9);
    var2.bottomMargin = (int)this.getResources().getDimension(R.dimen.main_binaural_information_margin) + (int)this.getResources().getDimension(R.dimen.sound_group_bottom_spacing);
    if(this.paddingColumnLeft != null) {
      var2.addRule(1, this.paddingColumnLeft.getId());
    }

    this.addView(this.infoBar, var2);
  }

  protected abstract void updateColumnLayout();

  protected abstract void updatePaddingLayout();
}
