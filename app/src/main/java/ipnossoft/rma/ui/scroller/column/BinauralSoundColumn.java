package ipnossoft.rma.ui.scroller.column;

import android.content.Context;
import com.ipnossoft.api.soundlibrary.Sound;
import ipnossoft.rma.ui.button.SoundButtonGestureListener;
import ipnossoft.rma.ui.scroller.BinauralSoundGroup;

public class BinauralSoundColumn extends SingleSoundColumn {
  public static int binauralSoundGroupIndex = 0;

  public BinauralSoundColumn(BinauralSoundGroup var1, Sound var2, Context var3, SoundButtonGestureListener var4) {
    super(binauralSoundGroupIndex, var1, var2, var3, var4);
    binauralSoundGroupIndex = binauralSoundGroupIndex + 1;
  }
}
