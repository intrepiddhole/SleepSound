package com.quietlycoding.android.picker;

import android.content.Context;
import android.os.Handler;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.method.NumberKeyListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLongClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import ipnossoft.rma.free.R;

public class NumberPicker extends LinearLayout implements OnClickListener, OnFocusChangeListener, OnLongClickListener {
	private static final int DEFAULT_MAX = 200;
	private static final int DEFAULT_MIN = 0;
	private static final char[] DIGIT_CHARACTERS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
	private static final String TAG = "NumberPicker";
	public static final NumberPicker.Formatter TWO_DIGIT_FORMATTER = new NumberPicker.Formatter() {
		final Object[] mArgs = new Object[1];
		final StringBuilder mBuilder = new StringBuilder();
		final java.util.Formatter mFmt;

		{
			this.mFmt = new java.util.Formatter(this.mBuilder);
		}

		public String toString(int var1) {
			this.mArgs[0] = Integer.valueOf(var1);
			this.mBuilder.delete(0, this.mBuilder.length());
			this.mFmt.format("%02d", this.mArgs);
			return this.mFmt.toString();
		}
	};
	private int mCurrent;
	private boolean mDecrement;
	private NumberPickerButton mDecrementButton;
	private String[] mDisplayedValues;
	private int mEnd;
	private NumberPicker.Formatter mFormatter;
	private final Handler mHandler;
	private boolean mIncrement;
	private NumberPickerButton mIncrementButton;
	private NumberPicker.OnChangedListener mListener;
	private final InputFilter mNumberInputFilter;
	private int mPrevious;
	private final Runnable mRunnable;
	private long mSpeed;
	private int mStart;
	private final EditText mText;

	public NumberPicker(Context var1) {
		this(var1, (AttributeSet)null);
	}

	public NumberPicker(Context var1, AttributeSet var2) {
		this(var1, var2, 0);
	}

	public NumberPicker(Context var1, AttributeSet var2, int var3) {
		super(var1, var2);
		this.mSpeed = 300L;
		this.mRunnable = new Runnable() {
			public void run() {
				if(NumberPicker.this.mIncrement) {
					NumberPicker.this.changeCurrent(NumberPicker.this.mCurrent + 1);
					NumberPicker.this.mHandler.postDelayed(this, NumberPicker.this.mSpeed);
				} else if(NumberPicker.this.mDecrement) {
					NumberPicker.this.changeCurrent(NumberPicker.this.mCurrent - 1);
					NumberPicker.this.mHandler.postDelayed(this, NumberPicker.this.mSpeed);
					return;
				}

			}
		};
		this.setOrientation(VERTICAL);
		((LayoutInflater)var1.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.number_picker, this, true);
		this.mHandler = new Handler();
		NumberPicker.NumberPickerInputFilter var4 = new NumberPicker.NumberPickerInputFilter();
		this.mNumberInputFilter = new NumberPicker.NumberRangeKeyListener();
		this.mIncrementButton = (NumberPickerButton)this.findViewById(R.id.increment);
		this.mIncrementButton.setOnClickListener(this);
		this.mIncrementButton.setOnLongClickListener(this);
		this.mIncrementButton.setNumberPicker(this);
		this.mDecrementButton = (NumberPickerButton)this.findViewById(R.id.decrement);
		this.mDecrementButton.setOnClickListener(this);
		this.mDecrementButton.setOnLongClickListener(this);
		this.mDecrementButton.setNumberPicker(this);
		this.mText = (EditText)this.findViewById(R.id.timepicker_input);
		this.mText.setOnFocusChangeListener(this);
		this.mText.setFilters(new InputFilter[]{var4});
		this.mText.setRawInputType(2);
		if(!this.isEnabled()) {
			this.setEnabled(false);
		}

		this.mStart = 0;
		this.mEnd = 200;
	}

	private void changeCurrent(int var1) {
		int var2;
		if(var1 > this.mEnd) {
			var2 = this.mStart;
		} else {
			var2 = var1;
			if(var1 < this.mStart) {
				var2 = this.mEnd;
			}
		}

		this.mPrevious = this.mCurrent;
		this.mCurrent = var2;
		this.notifyChange();
		this.updateView();
	}

	private String formatNumber(int var1) {
		return this.mFormatter != null?this.mFormatter.toString(var1):String.valueOf(var1);
	}

	private int getSelectedPos(String var1) {
		if(this.mDisplayedValues == null) {
			return Integer.parseInt(var1);
		} else {
			int var2;
			for(var2 = 0; var2 < this.mDisplayedValues.length; ++var2) {
				var1 = var1.toLowerCase();
				if(this.mDisplayedValues[var2].toLowerCase().startsWith(var1)) {
					return this.mStart + var2;
				}
			}

			try {
				var2 = Integer.parseInt(var1);
				return var2;
			} catch (NumberFormatException var3) {
				return this.mStart;
			}
		}
	}

	private void notifyChange() {
		if(this.mListener != null) {
			this.mListener.onChanged(this, this.mPrevious, this.mCurrent);
		}

	}

	private void updateView() {
		if(this.mDisplayedValues == null) {
			this.mText.setText(this.formatNumber(this.mCurrent));
		} else {
			this.mText.setText(this.mDisplayedValues[this.mCurrent - this.mStart]);
		}

		this.mText.setSelection(this.mText.getText().length());
	}

	private void validateCurrentView(CharSequence var1) {
		int var2 = this.getSelectedPos(var1.toString());
		if(var2 >= this.mStart && var2 <= this.mEnd && this.mCurrent != var2) {
			this.mPrevious = this.mCurrent;
			this.mCurrent = var2;
			this.notifyChange();
		}

		this.updateView();
	}

	private void validateInput(View var1) {
		String var2 = String.valueOf(((TextView)var1).getText());
		if("".equals(var2)) {
			this.updateView();
		} else {
			this.validateCurrentView(var2);
		}
	}

	public void cancelDecrement() {
		this.mDecrement = false;
	}

	public void cancelIncrement() {
		this.mIncrement = false;
	}

	public int getCurrent() {
		return this.mCurrent;
	}

	public int getCurrentText() {
		String var1 = this.mText.getText().toString();
		return var1.equals("")?0:Integer.parseInt(var1);
	}

	public void onClick(View var1) {
		this.validateInput(this.mText);
		if(!this.mText.hasFocus()) {
			this.mText.requestFocus();
		}

		if(R.id.increment == var1.getId()) {
			this.changeCurrent(this.mCurrent + 1);
		} else if(R.id.decrement == var1.getId()) {
			this.changeCurrent(this.mCurrent - 1);
			return;
		}

	}

	public void onFocusChange(View var1, boolean var2) {
		if(!var2) {
			this.validateInput(var1);
		}

	}

	public boolean onLongClick(View var1) {
		this.mText.clearFocus();
		if(R.id.increment == var1.getId()) {
			this.mIncrement = true;
			this.mHandler.post(this.mRunnable);
		} else if(R.id.decrement == var1.getId()) {
			this.mDecrement = true;
			this.mHandler.post(this.mRunnable);
			return true;
		}

		return true;
	}

	public void setCurrent(int var1) {
		this.mCurrent = var1;
		this.updateView();
	}

	public void setEnabled(boolean var1) {
		super.setEnabled(var1);
		this.mIncrementButton.setEnabled(var1);
		this.mDecrementButton.setEnabled(var1);
		this.mText.setEnabled(var1);
	}

	public void setFormatter(NumberPicker.Formatter var1) {
		this.mFormatter = var1;
	}

	public void setOnChangeListener(NumberPicker.OnChangedListener var1) {
		this.mListener = var1;
	}

	public void setRange(int var1, int var2) {
		this.mStart = var1;
		this.mEnd = var2;
		this.mCurrent = var1;
		this.updateView();
	}

	public void setRange(int var1, int var2, String[] var3) {
		this.mDisplayedValues = var3;
		this.mStart = var1;
		this.mEnd = var2;
		this.mCurrent = var1;
		this.updateView();
	}

	public void setSpeed(long var1) {
		this.mSpeed = var1;
	}

	public interface Formatter {
		String toString(int var1);
	}

	private class NumberPickerInputFilter implements InputFilter {
		private NumberPickerInputFilter() {
		}

		public CharSequence filter(CharSequence var1, int var2, int var3, Spanned var4, int var5, int var6) {
			Object var9;
			if(NumberPicker.this.mDisplayedValues == null) {
				var9 = NumberPicker.this.mNumberInputFilter.filter(var1, var2, var3, var4, var5, var6);
			} else {
				String var7 = String.valueOf(var1.subSequence(var2, var3));
				String var10 = String.valueOf(var4.subSequence(0, var5) + var7 + var4.subSequence(var6, var4.length())).toLowerCase();
				String[] var8 = NumberPicker.this.mDisplayedValues;
				var3 = var8.length;
				var2 = 0;

				while(true) {
					if(var2 >= var3) {
						return "";
					}

					var9 = var7;
					if(var8[var2].toLowerCase().startsWith(var10)) {
						break;
					}

					++var2;
				}
			}

			return (CharSequence)var9;
		}
	}

	private class NumberRangeKeyListener extends NumberKeyListener {
		private NumberRangeKeyListener() {
		}

		public CharSequence filter(CharSequence var1, int var2, int var3, Spanned var4, int var5, int var6) {
			CharSequence var8 = super.filter(var1, var2, var3, var4, var5, var6);
			CharSequence var7 = var8;
			if(var8 == null) {
				var7 = var1.subSequence(var2, var3);
			}

			String var9 = String.valueOf(var4.subSequence(0, var5)) + var7 + var4.subSequence(var6, var4.length());
			return (CharSequence)("".equals(var9)?var9:(NumberPicker.this.getSelectedPos(var9) > NumberPicker.this.mEnd?"":var7));
		}

		protected char[] getAcceptedChars() {
			return NumberPicker.DIGIT_CHARACTERS;
		}

		public int getInputType() {
			return 2;
		}
	}

	public interface OnChangedListener {
		void onChanged(NumberPicker var1, int var2, int var3);
	}
}
