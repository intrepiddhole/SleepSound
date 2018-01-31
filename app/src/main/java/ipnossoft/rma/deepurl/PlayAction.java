package ipnossoft.rma.deepurl;

import android.app.Activity;
import com.ipnossoft.api.soundlibrary.Sound;
import com.ipnossoft.api.soundlibrary.SoundLibrary;
import com.ipnossoft.api.soundlibrary.data.Query;
import ipnossoft.rma.media.SoundManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class PlayAction extends DeepUrlAction {
  private static final String PARAM_SOUNDS = "sounds";
  private static final String PARAM_VOLUME = "volume";
  private Activity activity;
  private List<String> sounds = this.getParameter("sounds");
  private List<Float> volumes = new ArrayList();

  public PlayAction(HashMap<String, List<String>> var1, Activity var2) {
    super(var1);
    this.activity = var2;
    Iterator var4 = this.getParameter("volume").iterator();

    while(var4.hasNext()) {
      String var5 = (String)var4.next();

      try {
        this.volumes.add(Float.valueOf(Float.parseFloat(var5)));
      } catch (Exception var3) {
        ;
      }
    }

  }

  private float getSoundVolume(String var1) {
    boolean var3 = true;
    int var4 = this.sounds.indexOf(var1);
    boolean var2;
    if(var4 != -1) {
      var2 = true;
    } else {
      var2 = false;
    }

    if(this.volumes.size() <= var4) {
      var3 = false;
    }

    return var2 & var3?((Float)this.volumes.get(var4)).floatValue():0.5F;
  }

  public void run() {
    SoundManager.getInstance().clearAll();
    Iterator var1 = SoundLibrary.getInstance().querySounds(new Query() {
      public boolean where(Sound var1) {
        return PlayAction.this.sounds.contains(var1.getName().toLowerCase()) || PlayAction.this.sounds.contains(var1.getId());
      }
    }).iterator();

    while(var1.hasNext()) {
      Sound var2 = (Sound)var1.next();
      if(var2.isPlayable()) {
        SoundManager.getInstance().play(var2.getId(), this.getSoundVolume(var2.getName().toLowerCase()), this.activity, false);
      }
    }

  }
}
