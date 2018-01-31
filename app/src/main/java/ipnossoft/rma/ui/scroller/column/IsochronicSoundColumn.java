package ipnossoft.rma.ui.scroller.column;

import android.content.Context;
import com.ipnossoft.api.soundlibrary.Sound;
import ipnossoft.rma.ui.button.SoundButtonGestureListener;
import ipnossoft.rma.ui.scroller.IsochronicSoundGroup;

public class IsochronicSoundColumn extends SingleSoundColumn {
  public static int isochronicSoundGroupIndex = 0;

  public IsochronicSoundColumn(IsochronicSoundGroup var1, Sound var2, Context var3, SoundButtonGestureListener var4) {
    super(isochronicSoundGroupIndex, var1, var2, var3, var4);
    isochronicSoundGroupIndex = isochronicSoundGroupIndex + 1;

  }
}
