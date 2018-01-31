package ipnossoft.rma.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View.MeasureSpec;
import android.widget.TextView;
import ipnossoft.rma.R.styleable;

public class FontFitTextView extends TextView {
  private float defaultTextSize;
  private Paint mTestPaint;

  public FontFitTextView(Context var1) {
    super(var1);
    this.initialize(this.getTextSize());
  }

  public FontFitTextView(Context var1, AttributeSet var2) {
    super(var1, var2);
    TypedArray var3 = var1.obtainStyledAttributes(var2, styleable.FontFitTextView);
    this.initialize(var3.getDimension(styleable.FontFitTextView_defaultTextSize, this.getTextSize()));
    var3.recycle();
  }

  private void initialize(float var1) {
    this.mTestPaint = new Paint();
    this.mTestPaint.set(this.getPaint());
    this.defaultTextSize = var1;
  }

  private void refitText(String var1, int var2) {
    if(var2 > 0 && !var1.isEmpty()) {
      var2 = var2 - this.getPaddingLeft() - this.getPaddingRight();
      if(var2 > 2) {
        this.mTestPaint.set(this.getPaint());
        this.mTestPaint.setTextSize(this.defaultTextSize);
        if(this.mTestPaint.measureText(var1) > (float)var2) {
          float var4 = this.defaultTextSize;
          float var3 = 2.0F;

          while(var4 - var3 > 0.5F) {
            float var5 = (var4 + var3) / 2.0F;
            this.mTestPaint.setTextSize(var5);
            if(this.mTestPaint.measureText(var1) >= (float)var2) {
              var4 = var5;
            } else {
              var3 = var5;
            }
          }

          super.setTextSize(0, var3);
          return;
        }
      }
    }

  }

  private void setTextToDefaultSize() {
    if(this.defaultTextSize != 0.0F) {
      super.setTextSize(0, this.defaultTextSize);
    }
  }

  protected void onMeasure(int var1, int var2) {
    super.onMeasure(var1, var2);
    var1 = MeasureSpec.getSize(var1);
    var2 = this.getMeasuredHeight();
    this.refitText(this.getText().toString(), var1);
    this.setMeasuredDimension(var1, var2);
  }

  protected void onSizeChanged(int var1, int var2, int var3, int var4) {
    if(var1 != var3 || var2 != var4) {
      if(this.defaultTextSize == 0.0F) {
        this.defaultTextSize = this.getTextSize();
      }

      this.refitText(this.getText().toString(), var1);
    }

  }

  protected void onTextChanged(CharSequence var1, int var2, int var3, int var4) {
    this.setTextToDefaultSize();
    this.refitText(var1.toString(), this.getWidth());
  }

  public void setTextSize(float var1) {
    super.setTextSize(var1);
    this.defaultTextSize = this.getTextSize();
  }
}
