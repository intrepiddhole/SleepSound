package com.ipnossoft.api.soundlibrary.loaders;

import android.support.annotation.ArrayRes;
import com.ipnossoft.api.soundlibrary.Sound;
import com.ipnossoft.api.soundlibrary.SoundLibrary;
import com.ipnossoft.api.soundlibrary.sounds.BinauralSound;
import com.ipnossoft.api.soundlibrary.sounds.GuidedMeditationSound;
import com.ipnossoft.api.soundlibrary.sounds.IsochronicSound;
import java.util.ArrayList;

public class BuiltInContentLoader extends ContentLoader {
  @ArrayRes
  private int binauralSoundsId;
  @ArrayRes
  private int guidedMeditationId;
  @ArrayRes
  private int isochronicSoundsId;
  @ArrayRes
  private int normalSoundsId;

  public BuiltInContentLoader(SoundLibrary var1, @ArrayRes int var2, @ArrayRes int var3, @ArrayRes int var4) {
    super(var1);
    this.normalSoundsId = var2;
    this.binauralSoundsId = var3;
    this.isochronicSoundsId = var4;
    this.guidedMeditationId = -1;
  }

  public BuiltInContentLoader(SoundLibrary var1, @ArrayRes int var2, @ArrayRes int var3, @ArrayRes int var4, @ArrayRes int var5) {
    super(var1);
    this.normalSoundsId = var2;
    this.binauralSoundsId = var3;
    this.isochronicSoundsId = var4;
    this.guidedMeditationId = var5;
  }

  public void doLoad(LoaderCallback var1) {
    ArrayList var2 = new ArrayList();
    var2.addAll(Sound.loadSoundsFromResourceArray(this.soundLibrary.getContext(), this.normalSoundsId));
    var2.addAll(BinauralSound.loadSoundsFromResourceArray(this.soundLibrary.getContext(), this.binauralSoundsId));
    var2.addAll(IsochronicSound.loadSoundsFromResourceArray(this.soundLibrary.getContext(), this.isochronicSoundsId));
    if(this.guidedMeditationId != -1) {
      var2.addAll(GuidedMeditationSound.loadSoundsFromResourceArray(this.soundLibrary.getContext(), this.guidedMeditationId));
    }

    if(var1 != null) {
      var1.callback(var2);
    }

  }
}
