package ipnossoft.rma.util.font;

import android.content.Context;
import android.graphics.Typeface;

import ipnossoft.rma.free.R;
import uk.co.chrisjenx.calligraphy.TypefaceUtils;

public enum RelaxFontFactory {
  INSTANCE;

  private RelaxFont largerFont;
  private Typeface lightTypeFace;
  private Typeface normalTypeFace;
  private RelaxFont proTextFont;
  private Typeface semiBoldTypeFace;
  private RelaxFont smallerFont;

  private RelaxFontFactory() {
  }

  public RelaxFont getExtraSmallLabelFont(Context var1) {
    if(this.proTextFont == null) {
      this.proTextFont = new RelaxFont(var1, "sans-serif-condensed", R.dimen.extra_small_label_font_size);
    }

    return this.proTextFont;
  }

  public RelaxFont getLargeRegularFont(Context var1) {
    if(this.largerFont == null) {
      this.largerFont = new RelaxFont(var1, "sans-serif", R.dimen.large_general_font_size);
    }

    return this.largerFont;
  }

  public Typeface getLightTypeFace(Context var1) {
    if(this.lightTypeFace == null) {
      this.lightTypeFace = TypefaceUtils.load(var1.getAssets(), "fonts/metric_light.ttf");
    }

    return this.lightTypeFace;
  }

  public Typeface getNormalTypeFace(Context var1) {
    if(this.normalTypeFace == null) {
      this.normalTypeFace = Typeface.createFromAsset(var1.getAssets(), "fonts/metric_regular.ttf");
    }

    return this.normalTypeFace;
  }

  public Typeface getSemiBoldTypeFace(Context var1) {
    if(this.semiBoldTypeFace == null) {
      this.semiBoldTypeFace = TypefaceUtils.load(var1.getAssets(), "fonts/metric_semibold.ttf");
    }

    return this.semiBoldTypeFace;
  }

  public RelaxFont getSmallLabelFont(Context var1) {
    if(this.proTextFont == null) {
      this.proTextFont = new RelaxFont(var1, "sans-serif-condensed", R.dimen.small_label_font_size);
    }

    return this.proTextFont;
  }

  public RelaxFont getSmallRegularFont(Context var1) {
    if(this.smallerFont == null) {
      this.smallerFont = new RelaxFont(var1, "sans-serif", R.dimen.small_general_font_size);
    }

    return this.smallerFont;
  }
}