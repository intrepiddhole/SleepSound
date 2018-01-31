package ipnossoft.rma.ui.scroller.column;

import android.annotation.SuppressLint;
import android.content.Context;
import com.ipnossoft.api.soundlibrary.Sound;
import ipnossoft.rma.ui.button.SoundButtonGestureListener;
import ipnossoft.rma.ui.scroller.AmbientSoundGroup;
import java.util.List;

@SuppressLint({"ViewConstructor"})
public class AmbientSoundColumn extends MultipleSoundColumn {
  public static int ambientSoundGroupIndex = 0;

  public AmbientSoundColumn(AmbientSoundGroup var1, List<Sound> var2, boolean var3, Context var4, SoundButtonGestureListener var5) {
    super(ambientSoundGroupIndex, var1, var2, var3, var4, var5);
    ambientSoundGroupIndex = ambientSoundGroupIndex + 1;
  }
}
