package ipnossoft.rma.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import java.util.ArrayList;

import ipnossoft.rma.free.R;

@SuppressLint("AppCompatCustomView")
public class AutoResizeTwoLineTextView extends TextView {
  private String TAG = this.getClass().getSimpleName();
  private float defaultStartingTextSize;
  private boolean shouldMakeFontFit;

  public AutoResizeTwoLineTextView(Context var1) {
    super(var1);
    this.defaultStartingTextSize = (float)this.getContext().getResources().getDimensionPixelSize(R.dimen.small_general_font_size);
    this.shouldMakeFontFit = true;
  }

  public AutoResizeTwoLineTextView(Context var1, AttributeSet var2) {
    super(var1, var2);
    this.defaultStartingTextSize = (float)this.getContext().getResources().getDimensionPixelSize(R.dimen.small_general_font_size);
    this.shouldMakeFontFit = true;
  }

  public AutoResizeTwoLineTextView(Context var1, AttributeSet var2, int var3) {
    super(var1, var2, var3);
    this.defaultStartingTextSize = (float)this.getContext().getResources().getDimensionPixelSize(R.dimen.small_general_font_size);
    this.shouldMakeFontFit = true;
  }

  private static int closestValueToMiddle(ArrayList<Integer> var0, int var1) {
    int var3 = ((Integer)var0.get(0)).intValue();

    int var4;
    for(int var2 = 1; var2 < var0.size(); var3 = var4) {
      var4 = var3;
      if(Math.abs(var1 - ((Integer)var0.get(var2)).intValue()) < Math.abs(var1 - var3)) {
        var4 = ((Integer)var0.get(var2)).intValue();
      }

      ++var2;
    }

    return var3;
  }

  private float computeApproximateSizeBasedOnTextViewLength() {
    return this.length() > 0 && this.length() <= 8?this.defaultStartingTextSize:(this.length() > 8 && this.length() <= 11?this.defaultStartingTextSize * 0.8F:(this.length() > 11 && this.length() <= 14?this.defaultStartingTextSize * 0.7F:(this.length() > 14 && this.length() <= 17?this.defaultStartingTextSize * 0.6F:(this.length() > 17 && this.length() <= 22?this.defaultStartingTextSize * 0.5F:this.defaultStartingTextSize * 0.4F))));
  }

  private boolean doesLineBreak(String var1, int var2) {
    return this.getPaint().breakText(var1, 0, var1.length(), true, (float)var2, (float[])null) != var1.length();
  }

  private void makeFontFitInViewSize() {
    this.setTextSize(0, this.defaultStartingTextSize);
    int var3 = this.getWidth() - (this.getPaddingLeft() + this.getPaddingRight());
    int var2 = this.getHeight();
    int var4 = this.getPaddingTop();
    int var5 = this.getPaddingBottom();
    if(var3 >= 1 && var2 - (var4 + var5) >= 1) {
      this.splitStringInTwoLinesIfNeeded();
      float var1 = this.getTextSize();
      if(0.0F < var1 && var1 < 200.0F) {
        var2 = (int)var1;
      } else {
        var2 = 200;
      }

      while(this.shouldShrinkText(var3) && var1 > 0.0F && var2 > 0) {
        --var2;
        var1 = this.getTextSize();
        this.setTextSize(0, var1 - 1.0F);
      }

      if(var1 == 0.0F || var2 == 0) {
        var1 = (float)Math.round(this.computeApproximateSizeBasedOnTextViewLength());
        Log.i(this.TAG, this.getText().toString() + " has entered infinite loop or was reduced to 0. Defaulting to font size of " + var1);
        this.setTextSize(0, var1);
      }

      this.requestLayout();
    }
  }

  private boolean shouldShrinkText(int var1) {
    boolean var4 = false;
    String var3 = this.getText().toString();
    if(!var3.contains("\n")) {
      var4 = this.doesLineBreak(var3, var1);
    } else {
      String var2 = var3.substring(0, var3.indexOf("\n"));
      var3 = var3.substring(var3.indexOf("\n") + 1, var3.length());
      if(this.doesLineBreak(var2, var1) || this.doesLineBreak(var3, var1)) {
        return true;
      }
    }

    return var4;
  }

  private void splitStringInTwoLinesIfNeeded() {
    if(this.getText().toString().length() > 11) {
      this.shouldMakeFontFit = false;
      this.setText(this.splitTextOnTwoLinesIfAppropriate());
      this.shouldMakeFontFit = true;
    }

  }

  private String splitTextOnTwoLinesIfAppropriate() {
    String var2 = this.getText().toString();
    if(var2.contains(" ") && !var2.contains("\n")) {
      ArrayList var3 = new ArrayList();

      int var1;
      for(var1 = 0; var1 < var2.length(); ++var1) {
        if(Character.toString(var2.charAt(var1)).equals(" ")) {
          var3.add(Integer.valueOf(var1));
        }
      }

      var1 = closestValueToMiddle(var3, Math.round((float)var2.length() / 2.0F));
      StringBuilder var4 = new StringBuilder(var2);
      var4.setCharAt(var1, '\n');
      return var4.toString();
    } else {
      return var2;
    }
  }

  protected void onSizeChanged(int var1, int var2, int var3, int var4) {
    super.onSizeChanged(var1, var2, var3, var4);
    if(var3 == 0 && var1 != 0 && this.shouldMakeFontFit) {
      this.makeFontFitInViewSize();
    }

  }

  public void setDefaultStartingTextSize(float var1) {
    this.defaultStartingTextSize = var1;
  }

  public void setText(CharSequence var1, BufferType var2) {
    super.setText(var1, var2);
    if(this.shouldMakeFontFit && this.getWidth() != 0) {
      this.makeFontFitInViewSize();
    }

  }
}
