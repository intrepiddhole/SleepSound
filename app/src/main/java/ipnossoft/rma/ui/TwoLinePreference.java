package ipnossoft.rma.ui;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public class TwoLinePreference extends Preference {
  public TwoLinePreference(Context var1) {
    super(var1);
  }

  public TwoLinePreference(Context var1, AttributeSet var2) {
    super(var1, var2);
  }

  public TwoLinePreference(Context var1, AttributeSet var2, int var3) {
    super(var1, var2, var3);
  }

  protected void onBindView(View var1) {
    super.onBindView(var1);
    TextView var2 = (TextView)var1.findViewById(16908310);
    if(var2 != null) {
      var2.setSingleLine(false);
    }

  }
}
