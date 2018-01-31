package ipnossoft.rma.util;

import android.content.Context;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.module.GlideModule;

public class GlideConfiguration implements GlideModule {
  public GlideConfiguration() {
  }

  public void applyOptions(Context var1, GlideBuilder var2) {
    var2.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
  }

  public void registerComponents(Context var1, Glide var2) {
  }
}
