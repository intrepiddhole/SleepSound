package ipnossoft.rma.guidedmeditations;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import ipnossoft.rma.free.R;

public class GuidedMeditationPlayingProgressView extends View {
  boolean firstOnMeasure = true;
  private Paint mArcPaint;
  RectF mOval;
  private Paint mOvalPaint;
  float startAngle = 270.0F;
  float strokeWidth = 0.0F;
  float sweepAngle = 0.0F;

  public GuidedMeditationPlayingProgressView(Context var1) {
    super(var1);
    this.initGuidedMeditationPlayingProgressView();
  }

  public GuidedMeditationPlayingProgressView(Context var1, AttributeSet var2) {
    super(var1, var2);
    this.initGuidedMeditationPlayingProgressView();
  }

  public GuidedMeditationPlayingProgressView(Context var1, AttributeSet var2, int var3) {
    super(var1, var2, var3);
    this.initGuidedMeditationPlayingProgressView();
  }

  private void createOvalDimensions() {
    LayoutParams var1 = this.getLayoutParams();
    float var2 = this.strokeWidth / 2.0F;
    this.mOval = new RectF(var2, var2, (float)var1.width - var2, (float)var1.height - var2);
  }

  private void initGuidedMeditationPlayingProgressView() {
    this.strokeWidth = (float)this.getResources().getDimensionPixelSize(R.dimen.guided_meditation_playing_play_pause_play_pause_stroke_width);
    this.initPaints();
  }

  private void initPaints() {
    this.mArcPaint = new Paint() {
      {
        this.setDither(true);
        this.setStyle(Style.STROKE);
        this.setStrokeJoin(Join.ROUND);
        this.setColor(Color.parseColor("#52d5ff"));
        this.setAlpha(56);
        this.setStrokeWidth(GuidedMeditationPlayingProgressView.this.strokeWidth);
        this.setAntiAlias(true);
      }
    };
    this.mOvalPaint = new Paint() {
      {
        this.setStyle(Style.FILL);
        this.setColor(0);
      }
    };
  }

  protected void onDraw(Canvas var1) {
    super.onDraw(var1);
    var1.drawOval(this.mOval, this.mOvalPaint);
    var1.drawArc(this.mOval, this.startAngle, this.sweepAngle, false, this.mArcPaint);
  }

  protected void onMeasure(int var1, int var2) {
    super.onMeasure(var1, var2);
    if(this.firstOnMeasure) {
      this.createOvalDimensions();
      this.firstOnMeasure = false;
    }

  }

  public void setPercentageProgress(float var1) {
    float var2 = this.sweepAngle;
    this.sweepAngle = 360.0F * var1;
    if(this.sweepAngle != var2) {
      this.invalidate();
    }

  }
}
