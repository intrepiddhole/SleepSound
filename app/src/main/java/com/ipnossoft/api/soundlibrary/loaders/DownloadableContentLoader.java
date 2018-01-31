package com.ipnossoft.api.soundlibrary.loaders;

import com.ipnossoft.api.dynamiccontent.model.InAppPurchase;
import com.ipnossoft.api.featuremanager.AsyncOperationListener;
import com.ipnossoft.api.featuremanager.FeatureManager;
import com.ipnossoft.api.soundlibrary.Sound;
import com.ipnossoft.api.soundlibrary.SoundFactory;
import com.ipnossoft.api.soundlibrary.SoundLibrary;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DownloadableContentLoader extends ContentLoader {
  public DownloadableContentLoader(SoundLibrary var1) {
    super(var1);
  }

  public void doLoad(final LoaderCallback var1) {
    FeatureManager.getInstance().fetchAvailableFeatures(new AsyncOperationListener() {
      public void onCompleted(Object var1x, boolean var2) {
        ArrayList var5 = new ArrayList();
        List var3 = FeatureManager.getInstance().getInAppPurchases();
        if(var3 != null) {
          Iterator var6 = var3.iterator();

          while(var6.hasNext()) {
            Sound var4 = SoundFactory.createFromInAppPurchase((InAppPurchase)var6.next());
            if(var4 != null) {
              var5.add(var4);
            }
          }
        }

        if(var1 != null) {
          var1.callback(var5);
        }

      }
    });
  }
}
