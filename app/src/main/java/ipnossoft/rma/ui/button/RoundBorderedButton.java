package ipnossoft.rma.ui.button;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;
import ipnossoft.rma.free.R;

public class RoundBorderedButton extends RelativeLayout {
  public RoundBorderedButton(Context var1, AttributeSet var2) {
    super(var1, var2);
    TypedArray var3 = this.getContext().obtainStyledAttributes(var2, R.styleable.RoundedBorderedButton);
    String var14 = var3.getString(R.styleable.RoundedBorderedButton_text);
    float var11 = var3.getDimension(R.styleable.RoundedBorderedButton_textSize, var1.getResources().getDimension(R.dimen.small_general_font_size));
    int var4 = var3.getColor(R.styleable.RoundedBorderedButton_textColor, ContextCompat.getColor(var1, R.color.default_rounded_corner_button_color));
    int var5 = var3.getResourceId(R.styleable.RoundedBorderedButton_backgroundDrawable, -1);
    int var6 = var3.getDimensionPixelSize(R.styleable.RoundedBorderedButton_customPaddingLeftRight, var1.getResources().getDimensionPixelSize(R.dimen.rounded_corner_button_lr));
    int var7 = var3.getDimensionPixelSize(R.styleable.RoundedBorderedButton_customPaddingTopBottom, var1.getResources().getDimensionPixelSize(R.dimen.rounded_corner_button_padding_tb));
    int var8 = var3.getDimensionPixelSize(R.styleable.RoundedBorderedButton_customWidth, -2);
    int var9 = var3.getDimensionPixelSize(R.styleable.RoundedBorderedButton_customHeight, -2);
    int var10 = var3.getDimensionPixelSize(R.styleable.RoundedBorderedButton_minWidth, 0);
    var3.recycle();
    LayoutInflater.from(var1).inflate(R.layout.round_bordered_button, this, true);
    TextView var12 = (TextView)this.findViewById(R.id.main_label);
    var12.setText(var14);
    var12.setTextSize(0, var11);
    var12.setTextColor(var4);
    var12.setPadding(var6, var7, var6, var7);
    var12.setMinWidth(var10);
    var12.setLayoutParams(new LayoutParams(var8, var9));
    RelativeLayout var13 = (RelativeLayout)this.findViewById(R.id.rounded_bordered_button_background_layout);
    if(var5 != -1) {
      var13.setBackgroundResource(var5);
    }

  }

  public void setText(String var1) {
    ((TextView)this.findViewById(R.id.main_label)).setText(var1);
  }
}
