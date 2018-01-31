package com.ipnossoft.api.soundlibrary.loaders;

import android.support.annotation.ArrayRes;
import com.ipnossoft.api.soundlibrary.Sound;
import com.ipnossoft.api.soundlibrary.SoundLibrary;
import java.util.ArrayList;

public class GiftedContentLoader extends ContentLoader {
  @ArrayRes
  private int giftedSoundsId;

  public GiftedContentLoader(SoundLibrary var1, @ArrayRes int var2) {
    super(var1);
    this.giftedSoundsId = var2;
  }

  public void doLoad(LoaderCallback var1) {
    ArrayList var2 = new ArrayList();
    var2.addAll(Sound.loadSoundsFromResourceArray(this.soundLibrary.getContext(), this.giftedSoundsId));
    if(var1 != null) {
      var1.callback(var2);
    }

  }
}
