package ipnossoft.rma.ui.scroller.column;

import android.content.Context;
import android.widget.RelativeLayout.LayoutParams;
import com.ipnossoft.api.soundlibrary.Sound;
import ipnossoft.rma.ui.button.SoundButton;
import ipnossoft.rma.ui.button.SoundButtonGestureListener;
import ipnossoft.rma.ui.scroller.SoundGroup;

public class SingleSoundColumn extends SoundColumn {
  private SoundButton button;
  private Sound sound;

  SingleSoundColumn(int var1, SoundGroup var2, Sound var3, Context var4, SoundButtonGestureListener var5) {
    super(var1, var2, var4, var5);
    this.sound = var3;
  }

  public void createInnerView() {
    this.button = this.soundGroup.createSoundButton(this.sound, this.getSoundButtonGestureListener());
    this.button.setPivotY((float)(-this.soundGroup.getButtonHeight()));
    this.button.setPivotX((float)(this.soundGroup.getButtonWidth() / 2));
    LayoutParams var1 = new LayoutParams(this.soundGroup.getButtonWidth(), -2);
    var1.addRule(12);
    this.button.addToContainer(this, var1);
    this.soundGroup.getRelaxScrollView().addButton(this.button);
    this.setClipChildren(false);
    this.setClipToPadding(false);
  }

  public void destroyInnerView() {
    this.button.removeFromContainer();
    this.removeAllViews();
    this.soundGroup.getRelaxScrollView().disposeButton(this.sound.getId());
    this.button = null;
  }
}
