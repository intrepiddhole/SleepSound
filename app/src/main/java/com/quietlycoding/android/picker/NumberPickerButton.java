package com.quietlycoding.android.picker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Button;
import ipnossoft.rma.free.R;

@SuppressLint("AppCompatCustomView")
public class NumberPickerButton extends Button {
    private NumberPicker mNumberPicker;

    public NumberPickerButton(Context var1) {
        super(var1);
    }

    public NumberPickerButton(Context var1, AttributeSet var2) {
        super(var1, var2);
    }

    public NumberPickerButton(Context var1, AttributeSet var2, int var3) {
        super(var1, var2, var3);
    }

    private void cancelLongpress() {
        if(R.id.increment == this.getId()) {
            this.mNumberPicker.cancelIncrement();
        } else if(R.id.decrement == this.getId()) {
            this.mNumberPicker.cancelDecrement();
            return;
        }

    }

    private void cancelLongpressIfRequired(MotionEvent var1) {
        if(var1.getAction() == 3 || var1.getAction() == 1) {
            this.cancelLongpress();
        }

    }

    public boolean onKeyUp(int var1, KeyEvent var2) {
        if(var1 == 23 || var1 == 66) {
            this.cancelLongpress();
        }

        return super.onKeyUp(var1, var2);
    }

    public boolean onTouchEvent(MotionEvent var1) {
        this.cancelLongpressIfRequired(var1);
        return super.onTouchEvent(var1);
    }

    public boolean onTrackballEvent(MotionEvent var1) {
        this.cancelLongpressIfRequired(var1);
        return super.onTrackballEvent(var1);
    }

    public void setNumberPicker(NumberPicker var1) {
        this.mNumberPicker = var1;
    }
}