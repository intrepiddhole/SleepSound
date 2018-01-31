package ipnossoft.rma.ui.scroller.column;

import android.content.Context;
import android.widget.RelativeLayout.LayoutParams;
import com.ipnossoft.api.soundlibrary.Sound;
import ipnossoft.rma.ui.button.SoundButton;
import ipnossoft.rma.ui.button.SoundButtonGestureListener;
import ipnossoft.rma.ui.scroller.SoundGroup;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class MultipleSoundColumn extends SoundColumn {
  private List<SoundButton> buttons = new ArrayList();
  private boolean even;
  private List<Sound> sounds;

  MultipleSoundColumn(int var1, SoundGroup var2, List<Sound> var3, boolean var4, Context var5, SoundButtonGestureListener var6) {
    super(var1, var2, var5, var6);
    this.sounds = var3;
    this.even = var4;
  }

  private LayoutParams adjustSoundButtonLayoutParams(LayoutParams var1, SoundButton var2) {
    int var3 = this.soundGroup.getOddTopPadding();
    int var4 = this.soundGroup.getEvenTopPadding();
    int var6 = this.soundGroup.getVerticalSoundSpacing();
    int var5 = this.soundGroup.getNavigationBarOffset();
    if(var2 == null) {
      if(this.even) {
        var3 = var4;
      }

      var1.topMargin = var5 + var3;
      return var1;
    } else {
      var1.topMargin = var6;
      var1.addRule(3, var2.getId());
      return var1;
    }
  }

  public void createInnerView() {
    SoundButton var1 = null;
    Iterator var3 = this.sounds.iterator();

    while(var3.hasNext()) {
      Sound var2 = (Sound)var3.next();
      SoundButton var6 = this.soundGroup.createSoundButton(var2, this.getSoundButtonGestureListener());
      LayoutParams var4 = new LayoutParams(this.soundGroup.getButtonWidth(), this.soundGroup.getButtonHeight());
      var4.addRule(14, -1);
      var6.addToContainer(this, this.adjustSoundButtonLayoutParams(var4, var1));
      this.soundGroup.getRelaxScrollView().addButton(var6);
      var1 = var6;
      this.buttons.add(var6);
    }

    this.soundGroup.createAndAppendRope(this, var1, this.sounds.size());
    Iterator var5 = this.buttons.iterator();

    while(var5.hasNext()) {
      ((SoundButton)var5.next()).bringToFront();
    }

  }

  public void destroyInnerView() {
    Iterator var1 = this.buttons.iterator();

    while(var1.hasNext()) {
      ((SoundButton)var1.next()).removeFromContainer();
    }

    this.removeAllViews();
    var1 = this.sounds.iterator();

    while(var1.hasNext()) {
      Sound var2 = (Sound)var1.next();
      this.soundGroup.getRelaxScrollView().disposeButton(var2.getId());
    }

    this.buttons.clear();
  }
}
