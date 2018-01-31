package ipnossoft.rma.ui.button;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import ipnossoft.rma.RelaxMelodiesApp;

public class CustomSelectableButton extends ImageView {
  private int normalResourceId;
  private int selectedResourceId;

  public CustomSelectableButton(Context var1) {
    super(var1);
  }

  public CustomSelectableButton(Context var1, AttributeSet var2) {
    super(var1, var2);
  }

  public CustomSelectableButton(Context var1, AttributeSet var2, int var3) {
    super(var1, var2, var3);
  }

  public int getNormalResourceId() {
    return this.normalResourceId;
  }

  public int getSelectedResourceId() {
    return this.selectedResourceId;
  }

  public void loadCurrentImage() {
    this.setSelected(this.isSelected());
  }

  public void setNormalResourceId(int var1) {
    this.normalResourceId = var1;
  }

  public void setSelected(boolean var1) {
    super.setSelected(var1);
    int var2;
    if(var1) {
      var2 = this.selectedResourceId;
    } else {
      var2 = this.normalResourceId;
    }

    Glide.with(RelaxMelodiesApp.getInstance().getApplicationContext()).load(Integer.valueOf(var2)).placeholder(var2).dontAnimate().into(this);
  }

  public void setSelectedResourceId(int var1) {
    this.selectedResourceId = var1;
  }
}
