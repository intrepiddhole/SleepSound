package ipnossoft.rma.ui.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.wefika.flowlayout.FlowLayout;

import ipnossoft.rma.free.R;

public class RelaxProgressDialog extends ProgressDialog {
  private Context context;
  private TextView defaultMessageTextView;
  private FlowLayout horizontalButtonLayout;
  private Button negativeButton;
  private Button neutralButton;
  private Button positiveButton;
  private RelaxProgressDialog.RelaxProgressDialogAttributes relaxProgressDialogAttributes;
  private View separatorView;
  private TextView titleTextView;
  private LinearLayout verticalButtonLayout;
  private View viewToAddAfterInitialize;

  private RelaxProgressDialog(Context var1, RelaxProgressDialog.RelaxProgressDialogAttributes var2) {
    super(var1);
    this.context = var1;
    this.relaxProgressDialogAttributes = var2;
  }

  private void initializeAttributes() {
    this.titleTextView = (TextView)this.findViewById(R.id.relax_dialog_title);
    this.separatorView = this.findViewById(R.id.relax_dialog_separator);
    this.defaultMessageTextView = (TextView)this.findViewById(R.id.relax_dialog_default_message);
    this.verticalButtonLayout = (LinearLayout)this.findViewById(R.id.relax_dialog_button_vertical_layout);
    this.horizontalButtonLayout = (FlowLayout)this.findViewById(R.id.relax_dialog_button_horizontal_layout);
    if(this.relaxProgressDialogAttributes.buttonOrientation == RelaxProgressDialog.RelaxDialogButtonOrientation.HORIZONTAL) {
      this.initializeHorizontalComponents();
    } else {
      this.initializeVerticalComponents();
    }
  }

  private void initializeHorizontalComponents() {
    this.verticalButtonLayout.setVisibility(View.GONE);
    this.horizontalButtonLayout.setVisibility(View.VISIBLE);
    this.positiveButton = (Button)this.findViewById(R.id.relax_dialog_horizontal_positive_button);
    this.neutralButton = (Button)this.findViewById(R.id.relax_dialog_horizontal_neutral_button);
    this.negativeButton = (Button)this.findViewById(R.id.relax_dialog_horizontal_negative_button);
  }

  private void initializeVerticalComponents() {
    this.verticalButtonLayout.setVisibility(View.VISIBLE);
    this.horizontalButtonLayout.setVisibility(View.GONE);
    this.positiveButton = (Button)this.findViewById(R.id.relax_dialog_vertical_positive_button);
    this.neutralButton = (Button)this.findViewById(R.id.relax_dialog_vertical_neutral_button);
    this.negativeButton = (Button)this.findViewById(R.id.relax_dialog_vertical_negative_button);
  }

  private void populateUI() {
    if(this.relaxProgressDialogAttributes.title != null) {
      this.titleTextView.setVisibility(View.VISIBLE);
      this.separatorView.setVisibility(View.VISIBLE);
      this.titleTextView.setText(this.relaxProgressDialogAttributes.title);
    }

    if(this.relaxProgressDialogAttributes.defaultMessage != null) {
      this.defaultMessageTextView.setVisibility(View.VISIBLE);
      this.defaultMessageTextView.setText(this.relaxProgressDialogAttributes.defaultMessage);
    }

    if(this.relaxProgressDialogAttributes.positiveButton != null) {
      this.positiveButton.setText(this.relaxProgressDialogAttributes.positiveButton.getText());
      this.positiveButton.setOnClickListener(this.wrapOnClickListenerWithDismissCall(this.relaxProgressDialogAttributes.positiveButton.getOnClickListener()));
      this.positiveButton.setVisibility(View.VISIBLE);
    }

    if(this.relaxProgressDialogAttributes.neutralButton != null) {
      this.neutralButton.setText(this.relaxProgressDialogAttributes.neutralButton.getText());
      this.neutralButton.setOnClickListener(this.wrapOnClickListenerWithDismissCall(this.relaxProgressDialogAttributes.neutralButton.getOnClickListener()));
      this.neutralButton.setVisibility(View.VISIBLE);
    }

    if(this.relaxProgressDialogAttributes.negativeButton != null) {
      this.negativeButton.setText(this.relaxProgressDialogAttributes.negativeButton.getText());
      this.negativeButton.setOnClickListener(this.wrapOnClickListenerWithDismissCall(this.relaxProgressDialogAttributes.negativeButton.getOnClickListener()));
      this.negativeButton.setVisibility(View.VISIBLE);
    }

    if(this.viewToAddAfterInitialize != null) {
      RelativeLayout var1 = (RelativeLayout)this.findViewById(R.id.relax_dialog_custom_view_layout);
      var1.removeAllViews();
      this.setupCustomContent(var1, this.viewToAddAfterInitialize);
    }

  }

  private void setupCustomContent(ViewGroup var1, View var2) {
    View var3;
    if(var2 != null) {
      var3 = var2;
    } else {
      var3 = null;
    }

    boolean var4;
    if(var3 != null) {
      var4 = true;
    } else {
      var4 = false;
    }

    if(var4) {
      LayoutParams var5 = new LayoutParams(-1, -2);
      var5.height = (int)TypedValue.applyDimension(0, (float)((int)this.context.getResources().getDimension(R.dimen.relax_progress_dialog_progress_layout_height)), this.context.getResources().getDisplayMetrics());
      (new android.widget.LinearLayout.LayoutParams(-1, -2)).gravity = 49;
      var2.setLayoutParams(var5);
      var1.addView(var2, var5);
    } else {
      var1.setVisibility(View.GONE);
    }
  }

  private View.OnClickListener wrapOnClickListenerWithDismissCall(final View.OnClickListener var1) {
    return new View.OnClickListener() {
      public void onClick(View var1x) {
        if(var1 != null) {
          var1.onClick(var1x);
        }

        RelaxProgressDialog.this.dismiss();
      }
    };
  }

  public void changeTitle(String var1) {
    this.titleTextView.setText(var1);
  }

  public void disableNegativeButton() {
    if(this.negativeButton != null) {
      this.negativeButton.setEnabled(false);
      this.negativeButton.setTextColor(Color.parseColor("#7f333333"));
    }

  }

  public void enableNegativeButton() {
    if(this.negativeButton != null) {
      this.negativeButton.setEnabled(true);
      this.negativeButton.setTextColor(Color.parseColor("#333333"));
    }

  }

  protected void onCreate(Bundle var1) {
    this.requestWindowFeature(1);
    super.onCreate(var1);
    this.setContentView(R.layout.relax_progress_dialog);
    this.initializeAttributes();
    this.populateUI();
  }

  public void setView(View var1) {
    this.viewToAddAfterInitialize = var1;
  }

  public static class Builder {
    private Context context;
    private RelaxProgressDialog.RelaxProgressDialogAttributes relaxProgressDialogAttributes;

    public Builder(Context var1, RelaxProgressDialog.RelaxDialogButtonOrientation var2) {
      this.context = var1;
      this.relaxProgressDialogAttributes = new RelaxProgressDialog.RelaxProgressDialogAttributes();
      this.relaxProgressDialogAttributes.buttonOrientation = var2;
    }

    public RelaxProgressDialog create() {
      RelaxProgressDialog var1 = new RelaxProgressDialog(this.context, this.relaxProgressDialogAttributes);
      var1.setCancelable(this.relaxProgressDialogAttributes.isCancelable);
      if(this.relaxProgressDialogAttributes.isCancelable) {
        var1.setCanceledOnTouchOutside(true);
      }

      var1.setOnCancelListener(this.relaxProgressDialogAttributes.onCancelListener);
      var1.setOnDismissListener(this.relaxProgressDialogAttributes.onDismissListener);
      if(this.relaxProgressDialogAttributes.onKeyListener != null) {
        var1.setOnKeyListener(this.relaxProgressDialogAttributes.onKeyListener);
      }

      var1.setProgressStyle(this.relaxProgressDialogAttributes.style);
      var1.setProgress(this.relaxProgressDialogAttributes.progress);
      var1.setMax(this.relaxProgressDialogAttributes.maxProgress);
      if(this.relaxProgressDialogAttributes.progressNumberFormat != null) {
        var1.setProgressNumberFormat(this.relaxProgressDialogAttributes.progressNumberFormat);
      }

      var1.getWindow().setBackgroundDrawable(new ColorDrawable(0));
      return var1;
    }

    public RelaxProgressDialog.Builder setButtonOrientation(RelaxProgressDialog.RelaxDialogButtonOrientation var1) {
      this.relaxProgressDialogAttributes.buttonOrientation = var1;
      return this;
    }

    public RelaxProgressDialog.Builder setCancelable(boolean var1) {
      this.relaxProgressDialogAttributes.isCancelable = var1;
      return this;
    }

    public RelaxProgressDialog.Builder setMaxProgress(int var1) {
      this.relaxProgressDialogAttributes.maxProgress = var1;
      return this;
    }

    public RelaxProgressDialog.Builder setMessage(int var1) {
      this.relaxProgressDialogAttributes.defaultMessage = this.context.getResources().getString(var1);
      return this;
    }

    public RelaxProgressDialog.Builder setMessage(String var1) {
      this.relaxProgressDialogAttributes.defaultMessage = var1;
      return this;
    }

    public RelaxProgressDialog.Builder setNegativeButton(int var1, View.OnClickListener var2) {
      this.relaxProgressDialogAttributes.negativeButton = new RelaxProgressDialog.RelaxDialogButton(this.context.getResources().getString(var1), var2);
      return this;
    }

    public RelaxProgressDialog.Builder setNegativeButton(String var1, View.OnClickListener var2) {
      this.relaxProgressDialogAttributes.negativeButton = new RelaxProgressDialog.RelaxDialogButton(var1, var2);
      return this;
    }

    public RelaxProgressDialog.Builder setNeutralButton(int var1, View.OnClickListener var2) {
      this.relaxProgressDialogAttributes.neutralButton = new RelaxProgressDialog.RelaxDialogButton(this.context.getResources().getString(var1), var2);
      return this;
    }

    public RelaxProgressDialog.Builder setNeutralButton(String var1, View.OnClickListener var2) {
      this.relaxProgressDialogAttributes.neutralButton = new RelaxProgressDialog.RelaxDialogButton(var1, var2);
      return this;
    }

    public RelaxProgressDialog.Builder setOnCancelListener(OnCancelListener var1) {
      this.relaxProgressDialogAttributes.onCancelListener = var1;
      return this;
    }

    public RelaxProgressDialog.Builder setOnDismissListener(OnDismissListener var1) {
      this.relaxProgressDialogAttributes.onDismissListener = var1;
      return this;
    }

    public RelaxProgressDialog.Builder setOnKeyListener(OnKeyListener var1) {
      this.relaxProgressDialogAttributes.onKeyListener = var1;
      return this;
    }

    public RelaxProgressDialog.Builder setPositiveButton(int var1, View.OnClickListener var2) {
      this.relaxProgressDialogAttributes.positiveButton = new RelaxProgressDialog.RelaxDialogButton(this.context.getResources().getString(var1), var2);
      return this;
    }

    public RelaxProgressDialog.Builder setPositiveButton(String var1, View.OnClickListener var2) {
      this.relaxProgressDialogAttributes.positiveButton = new RelaxProgressDialog.RelaxDialogButton(var1, var2);
      return this;
    }

    public RelaxProgressDialog.Builder setProgress(int var1) {
      this.relaxProgressDialogAttributes.progress = var1;
      return this;
    }

    public RelaxProgressDialog.Builder setProgressNumberFormat(String var1) {
      this.relaxProgressDialogAttributes.progressNumberFormat = var1;
      return this;
    }

    public RelaxProgressDialog.Builder setStyle(int var1) {
      this.relaxProgressDialogAttributes.style = var1;
      return this;
    }

    public RelaxProgressDialog.Builder setTitle(int var1) {
      this.relaxProgressDialogAttributes.title = this.context.getResources().getString(var1);
      return this;
    }

    public RelaxProgressDialog.Builder setTitle(String var1) {
      this.relaxProgressDialogAttributes.title = var1;
      return this;
    }

    public RelaxProgressDialog show() {
      RelaxProgressDialog var1 = this.create();
      var1.show();
      var1.getWindow().clearFlags(131080);
      var1.getWindow().setSoftInputMode(4);
      return var1;
    }
  }

  private static class RelaxDialogButton {
    private View.OnClickListener onClickListener;
    private String text;

    public RelaxDialogButton(String var1, View.OnClickListener var2) {
      this.text = var1;
      this.onClickListener = var2;
    }

    public View.OnClickListener getOnClickListener() {
      return this.onClickListener;
    }

    public String getText() {
      return this.text;
    }

    public void setOnClickListener(View.OnClickListener var1) {
      this.onClickListener = var1;
    }

    public void setText(String var1) {
      this.text = var1;
    }
  }

  public static enum RelaxDialogButtonOrientation {
    HORIZONTAL,
    VERTICAL;

    private RelaxDialogButtonOrientation() {
    }
  }

  private static class RelaxProgressDialogAttributes {
    private RelaxProgressDialog.RelaxDialogButtonOrientation buttonOrientation;
    private String defaultMessage;
    private boolean isCancelable;
    private int maxProgress;
    private RelaxProgressDialog.RelaxDialogButton negativeButton;
    private RelaxProgressDialog.RelaxDialogButton neutralButton;
    private OnCancelListener onCancelListener;
    private OnDismissListener onDismissListener;
    private OnKeyListener onKeyListener;
    private RelaxProgressDialog.RelaxDialogButton positiveButton;
    private int progress;
    private String progressNumberFormat;
    private int style;
    private String title;

    private RelaxProgressDialogAttributes() {
      this.progress = 0;
      this.maxProgress = 100;
      this.isCancelable = true;
    }
  }
}
