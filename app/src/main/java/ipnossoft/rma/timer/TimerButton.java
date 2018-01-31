package ipnossoft.rma.timer;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;
import ipnossoft.rma.free.R;

public class TimerButton extends RelativeLayout {
  public TimerButton(Context var1, AttributeSet var2) {
    super(var1, var2);
    TypedArray var5 = this.getContext().obtainStyledAttributes(var2, R.styleable.TimerButton);
    String var3 = var5.getString(R.styleable.TimerButton_number);
    String var4 = var1.getString(var5.getResourceId(R.styleable.TimerButton_unit, 0));
    var5.recycle();
    LayoutInflater.from(var1).inflate(R.layout.timer_button, this, true);
    ((TextView)this.findViewById(R.id.main_label)).setText(var3 + " " + var4);
  }
}
