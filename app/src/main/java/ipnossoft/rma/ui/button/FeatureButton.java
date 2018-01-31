package ipnossoft.rma.ui.button;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public abstract class FeatureButton extends RelativeLayout {
  private View buttonView;
  private RelativeLayout container;
  private int size;

  public FeatureButton(Context var1) {
    super(var1);
  }

  FeatureButton(Context var1, int var2) {
    super(var1);
    this.size = var2;
  }

  public FeatureButton(Context var1, AttributeSet var2) {
    super(var1, var2);
  }

  public FeatureButton(Context var1, AttributeSet var2, int var3) {
    super(var1, var2, var3);
  }

  void addToContainer(RelativeLayout var1, LayoutParams var2) {
    if(this.container != null) {
      this.removeFromContainer();
    }

    if(var2 != null) {
      var1.addView(this, var2);
    } else {
      var1.addView(this);
    }

    this.container = var1;
  }

  protected abstract View createButtonView();

  public View getButtonView() {
    return this.buttonView;
  }

  protected int getSize() {
    return this.size;
  }

  void removeFromContainer() {
    if(this.container != null) {
      this.container.removeView(this);
      this.container = null;
    }

  }

  void setButtonView(View var1) {
    this.buttonView = var1;
  }

  protected void setSize(int var1) {
    this.size = var1;
  }
}
