package ipnossoft.rma.animation;

import android.animation.TimeInterpolator;

public class ElasticInterpolator implements TimeInterpolator {
  double period = 0.45D;

  public ElasticInterpolator() {
  }

  public ElasticInterpolator(double var1) {
    this.period = var1;
  }

  public float getInterpolation(float var1) {
    if(var1 != 0.0F && var1 != 1.0F) {
      double var2 = this.period / 6.283185307179586D;
      double var4 = Math.asin(1.0D / 1.0D);
      return (float)(Math.pow(2.0D, (double)(-10.0F * var1)) * 1.0D * Math.sin(((double)var1 - var2 * var4) * 6.283185307179586D / this.period) + 1.0D);
    } else {
      return var1;
    }
  }
}
