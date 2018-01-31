package ipnossoft.rma.ui.scroller.column;

import android.content.Context;
import android.widget.RelativeLayout;
import ipnossoft.rma.ui.button.SoundButtonGestureListener;
import ipnossoft.rma.ui.scroller.SoundGroup;
import ipnossoft.rma.util.Utils;

public abstract class SoundColumn extends RelativeLayout {
  private static volatile int soundColumnIndex = 0;
  private int columnIndex;
  private volatile boolean innerViewCreated;
  private SoundButtonGestureListener soundButtonGestureListener;
  SoundGroup soundGroup;
  private int soundGroupColumnIndex;

  SoundColumn(int var1, SoundGroup var2, Context var3, SoundButtonGestureListener var4) {
    super(var3);
    int var5 = soundColumnIndex;
    soundColumnIndex = var5 + 1;
    this.columnIndex = var5;
    this.soundGroup = var2;
    this.setId(Utils.generateUniqueViewId());
    this.soundGroupColumnIndex = var1;
    this.soundButtonGestureListener = var4;
  }

  private boolean isInnerViewCreated() {
    return this.innerViewCreated;
  }

  private void setInnerViewCreated(boolean var1) {
    this.innerViewCreated = var1;
  }

  public abstract void createInnerView();

  public void createView() {
    if(!this.isInnerViewCreated()) {
      this.createInnerView();
      this.setInnerViewCreated(true);
    }

  }

  public abstract void destroyInnerView();

  public void destroyView() {
    if(this.isInnerViewCreated()) {
      this.destroyInnerView();
      this.setInnerViewCreated(false);
    }

  }

  public int getColumnIndex() {
    return this.columnIndex;
  }

  SoundButtonGestureListener getSoundButtonGestureListener() {
    return this.soundButtonGestureListener;
  }

  public int getSoundGroupColumnIndex() {
    return this.soundGroupColumnIndex;
  }

  void setAsOneSoundColumn() {
    this.setPivotY((float)(-this.soundGroup.getButtonHeight()));
    this.setPivotX((float)(this.soundGroup.getButtonWidth() / 2));
  }
}
