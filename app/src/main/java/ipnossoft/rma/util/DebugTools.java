package ipnossoft.rma.util;

import android.app.Activity;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

import ipnossoft.rma.free.R;

public class DebugTools {
  public DebugTools() {
  }

  public static void addDebugDeviceBoxToView(Activity var0, ViewGroup var1) {
    LinearLayout var2 = (LinearLayout)View.inflate(var0, R.layout.debug_label, (ViewGroup)null);
    var2.setClickable(true);
    var2.setOnClickListener(new DebugTools.DebugDeviceBoxOnClickListener());
    ((TextView)var2.findViewById(R.id.debug_label)).setText(getScreenDPI(var0) + "\r\n" + getScreenSize(var0) + "\r\nTouch to disable until next restart");
    LayoutParams var3 = new LayoutParams(-2, -2);
    var3.addRule(14, -1);
    var3.addRule(10, -1);
    var3.topMargin = (int)(var0.getResources().getDimension(R.dimen.navigation_bar_height) + 10.0F);
    var1.addView(var2, var3);
  }

  private static String getScreenDPI(Activity var0) {
    DisplayMetrics var3 = var0.getResources().getDisplayMetrics();
    int var1 = var3.densityDpi;
    String var4 = DeviceUtils.getScreenDensity(var0);
    String var2 = "normal";
    if((var0.getResources().getConfiguration().screenLayout & 15) == 3) {
      var2 = "large";
    } else if((var0.getResources().getConfiguration().screenLayout & 15) == 4) {
      var2 = "xlarge";
    }

    return "Dpi = " + var1 + "\r\nDpi name = " + var4 + "\r\nScale ratio = " + var3.scaledDensity + "\r\nDevice size = " + var2;
  }

  private static String getScreenSize(Activity var0) {
    Display var4 = var0.getWindowManager().getDefaultDisplay();
    Point var1 = new Point();
    var4.getSize(var1);
    int var2 = var1.x;
    int var3 = var1.y;
    return "Width = " + var2 + "\r\nHeight = " + var3;
  }

  private static class DebugDeviceBoxOnClickListener implements OnClickListener {
    private DebugDeviceBoxOnClickListener() {
    }

    public void onClick(View var1) {
      ((ViewManager)var1.getParent()).removeView(var1);
    }
  }
}
