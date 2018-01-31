package ipnossoft.rma.util.font;

import android.content.Context;

public class RelaxFont {
  private Context context;
  private int dimensionId;
  private String family;
  private float size;

  public RelaxFont(Context var1, String var2, int var3) {
    this.context = var1;
    this.family = var2;
    this.dimensionId = var3;
  }

  public String getFamily() {
    return this.family;
  }

  public float getSize() {
    if(this.size == 0.0F) {
      this.size = this.context.getResources().getDimension(this.dimensionId);
    }

    return this.size;
  }
}
