package ipnossoft.rma.ui.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import com.wefika.flowlayout.FlowLayout;

import ipnossoft.rma.free.R;
import ipnossoft.rma.ui.button.RoundBorderedButton;

public class RelaxDialog extends AlertDialog {
  private Context context;
  private RelativeLayout customContentLayout;
  private TextView defaultMessageTextView;
  private FlowLayout horizontalButtonLayout;
  private RoundBorderedButton negativeButton;
  private RoundBorderedButton neutralButton;
  private RoundBorderedButton positiveButton;
  private RelaxDialog.RelaxDialogAttributes relaxDialogAttributes;
  private View separatorView;
  private TextView titleTextView;
  private LinearLayout verticalButtonLayout;

  private RelaxDialog(Context var1, RelaxDialog.RelaxDialogAttributes var2) {
    super(var1);
    this.context = var1;
    this.relaxDialogAttributes = var2;
  }

  private void handleCustomMessageView() {
    if(this.relaxDialogAttributes.customViewLayoutResourceId != 0 && this.relaxDialogAttributes.customView == null) {
      View var1 = LayoutInflater.from(this.context).inflate(this.relaxDialogAttributes.customViewLayoutResourceId, (ViewGroup)null, false);
      this.customContentLayout.removeAllViews();
      this.setupCustomContent(this.customContentLayout, var1);
    } else {
      if(this.relaxDialogAttributes.customViewLayoutResourceId == 0 && this.relaxDialogAttributes.customView != null) {
        this.customContentLayout.removeAllViews();
        this.setupCustomContent(this.customContentLayout, this.relaxDialogAttributes.customView);
        return;
      }

      if(this.relaxDialogAttributes.defaultMessage == null) {
        this.customContentLayout.removeAllViews();
        ((LayoutParams)this.customContentLayout.getLayoutParams()).bottomMargin = 0;
        return;
      }
    }

  }

  private void initializeAttributes() {
    this.titleTextView = (TextView)this.findViewById(R.id.relax_dialog_title);
    this.separatorView = this.findViewById(R.id.relax_dialog_separator);
    this.customContentLayout = (RelativeLayout)this.findViewById(R.id.relax_dialog_custom_view_layout);
    this.defaultMessageTextView = (TextView)this.findViewById(R.id.relax_dialog_default_message);
    this.verticalButtonLayout = (LinearLayout)this.findViewById(R.id.relax_dialog_button_vertical_layout);
    this.horizontalButtonLayout = (FlowLayout)this.findViewById(R.id.relax_dialog_button_horizontal_layout);
    if(this.relaxDialogAttributes.buttonOrientation == RelaxDialog.RelaxDialogButtonOrientation.VERTICAL) {
      this.initializeVerticalComponents();
    } else {
      this.initializeHorizontalComponents();
    }
  }

  private void initializeHorizontalComponents() {
    this.verticalButtonLayout.setVisibility(View.GONE);
    this.horizontalButtonLayout.setVisibility(View.VISIBLE);
    this.positiveButton = (RoundBorderedButton)this.findViewById(R.id.relax_dialog_horizontal_positive_button);
    this.neutralButton = (RoundBorderedButton)this.findViewById(R.id.relax_dialog_horizontal_neutral_button);
    this.negativeButton = (RoundBorderedButton)this.findViewById(R.id.relax_dialog_horizontal_negative_button);
  }

  private void initializeVerticalComponents() {
    this.verticalButtonLayout.setVisibility(View.VISIBLE);
    this.horizontalButtonLayout.setVisibility(View.GONE);
    this.positiveButton = (RoundBorderedButton)this.findViewById(R.id.relax_dialog_vertical_positive_button);
    this.neutralButton = (RoundBorderedButton)this.findViewById(R.id.relax_dialog_vertical_neutral_button);
    this.negativeButton = (RoundBorderedButton)this.findViewById(R.id.relax_dialog_vertical_negative_button);
  }

  private void populateUI() {
    if(this.relaxDialogAttributes.title != null) {
      this.titleTextView.setVisibility(View.VISIBLE);
      this.separatorView.setVisibility(View.VISIBLE);
      this.titleTextView.setText(this.relaxDialogAttributes.title);
    }

    if(this.relaxDialogAttributes.defaultMessage != null) {
      this.defaultMessageTextView.setVisibility(View.VISIBLE);
      this.defaultMessageTextView.setText(this.relaxDialogAttributes.defaultMessage);
    }

    if(this.relaxDialogAttributes.positiveButton != null) {
      this.positiveButton.setText(this.relaxDialogAttributes.positiveButton.getText());
      this.positiveButton.setOnClickListener(this.wrapOnClickListenerWithDismissCall(this.relaxDialogAttributes.positiveButton.getOnClickListener()));
      this.positiveButton.setVisibility(View.VISIBLE);
    }

    if(this.relaxDialogAttributes.neutralButton != null) {
      this.neutralButton.setText(this.relaxDialogAttributes.neutralButton.getText());
      this.neutralButton.setOnClickListener(this.wrapOnClickListenerWithDismissCall(this.relaxDialogAttributes.neutralButton.getOnClickListener()));
      this.neutralButton.setVisibility(View.VISIBLE);
    }

    if(this.relaxDialogAttributes.negativeButton != null) {
      this.negativeButton.setText(this.relaxDialogAttributes.negativeButton.getText());
      this.negativeButton.setOnClickListener(this.wrapOnClickListenerWithDismissCall(this.relaxDialogAttributes.negativeButton.getOnClickListener()));
      this.negativeButton.setVisibility(View.VISIBLE);
    }

    this.handleCustomMessageView();
  }

  private void setupCustomContent(ViewGroup var1, View var2) {
    View var4;
    if(var2 != null) {
      var4 = var2;
    } else {
      var4 = null;
    }

    boolean var3;
    if(var4 != null) {
      var3 = true;
    } else {
      var3 = false;
    }

    if(var3) {
      var1.addView(var2, new android.view.ViewGroup.LayoutParams(-1, -2));
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

        RelaxDialog.this.dismiss();
      }
    };
  }

  protected void onCreate(Bundle var1) {
    this.requestWindowFeature(1);
    super.onCreate(var1);
    this.setContentView(R.layout.relax_dialog);
    this.initializeAttributes();
    this.populateUI();
  }

  public static class Builder {
    private Context context;
    private RelaxDialog.RelaxDialogAttributes relaxDialogAttributes;

    public Builder(Context var1, RelaxDialog.RelaxDialogButtonOrientation var2) {
      this.context = var1;
      this.relaxDialogAttributes = new RelaxDialog.RelaxDialogAttributes();
      this.relaxDialogAttributes.buttonOrientation = var2;
    }

    public RelaxDialog create() {
      RelaxDialog var1 = new RelaxDialog(this.context, this.relaxDialogAttributes);
      var1.setCancelable(this.relaxDialogAttributes.isCancelable);
      if(this.relaxDialogAttributes.isCancelable) {
        var1.setCanceledOnTouchOutside(true);
      }

      var1.setOnCancelListener(this.relaxDialogAttributes.onCancelListener);
      var1.setOnDismissListener(this.relaxDialogAttributes.onDismissListener);
      if(this.relaxDialogAttributes.onKeyListener != null) {
        var1.setOnKeyListener(this.relaxDialogAttributes.onKeyListener);
      }

      var1.getWindow().setBackgroundDrawable(new ColorDrawable(0));
      return var1;
    }

    public RelaxDialog.Builder setButtonOrientation(RelaxDialog.RelaxDialogButtonOrientation var1) {
      this.relaxDialogAttributes.buttonOrientation = var1;
      return this;
    }

    public RelaxDialog.Builder setCancelable(boolean var1) {
      this.relaxDialogAttributes.isCancelable = var1;
      return this;
    }

    public RelaxDialog.Builder setCustomContentView(int var1) {
      this.relaxDialogAttributes.customViewLayoutResourceId = var1;
      this.relaxDialogAttributes.customView = null;
      return this;
    }

    public RelaxDialog.Builder setCustomContentView(View var1) {
      this.relaxDialogAttributes.customView = var1;
      this.relaxDialogAttributes.customViewLayoutResourceId = 0;
      return this;
    }

    public RelaxDialog.Builder setMessage(int var1) {
      this.relaxDialogAttributes.defaultMessage = this.context.getResources().getString(var1);
      return this;
    }

    public RelaxDialog.Builder setMessage(String var1) {
      this.relaxDialogAttributes.defaultMessage = var1;
      return this;
    }

    public RelaxDialog.Builder setNegativeButton(int var1, View.OnClickListener var2) {
      this.relaxDialogAttributes.negativeButton = new RelaxDialog.RelaxDialogButton(this.context.getResources().getString(var1), var2);
      return this;
    }

    public RelaxDialog.Builder setNegativeButton(String var1, View.OnClickListener var2) {
      this.relaxDialogAttributes.negativeButton = new RelaxDialog.RelaxDialogButton(var1, var2);
      return this;
    }

    public RelaxDialog.Builder setNeutralButton(int var1, View.OnClickListener var2) {
      this.relaxDialogAttributes.neutralButton = new RelaxDialog.RelaxDialogButton(this.context.getResources().getString(var1), var2);
      return this;
    }

    public RelaxDialog.Builder setNeutralButton(String var1, View.OnClickListener var2) {
      this.relaxDialogAttributes.neutralButton = new RelaxDialog.RelaxDialogButton(var1, var2);
      return this;
    }

    public RelaxDialog.Builder setOnCancelListener(OnCancelListener var1) {
      this.relaxDialogAttributes.onCancelListener = var1;
      return this;
    }

    public RelaxDialog.Builder setOnDismissListener(OnDismissListener var1) {
      this.relaxDialogAttributes.onDismissListener = var1;
      return this;
    }

    public RelaxDialog.Builder setOnKeyListener(OnKeyListener var1) {
      this.relaxDialogAttributes.onKeyListener = var1;
      return this;
    }

    public RelaxDialog.Builder setPositiveButton(int var1, View.OnClickListener var2) {
      this.relaxDialogAttributes.positiveButton = new RelaxDialog.RelaxDialogButton(this.context.getResources().getString(var1), var2);
      return this;
    }

    public RelaxDialog.Builder setPositiveButton(String var1, View.OnClickListener var2) {
      this.relaxDialogAttributes.positiveButton = new RelaxDialog.RelaxDialogButton(var1, var2);
      return this;
    }

    public RelaxDialog.Builder setTitle(int var1) {
      this.relaxDialogAttributes.title = this.context.getResources().getString(var1);
      return this;
    }

    public RelaxDialog.Builder setTitle(String var1) {
      this.relaxDialogAttributes.title = var1;
      return this;
    }

    public RelaxDialog show() {
      RelaxDialog var1 = this.create();
      var1.show();
      var1.getWindow().clearFlags(131080);
      var1.getWindow().setSoftInputMode(4);
      return var1;
    }

    public RelaxDialog showWithKeyboard() {
      RelaxDialog var1 = this.create();
      var1.show();
      var1.getWindow().clearFlags(131080);
      var1.getWindow().setSoftInputMode(5);
      return var1;
    }
  }

  private static class RelaxDialogAttributes {
    private RelaxDialog.RelaxDialogButtonOrientation buttonOrientation;
    private View customView;
    private int customViewLayoutResourceId;
    private String defaultMessage;
    private boolean isCancelable;
    private RelaxDialog.RelaxDialogButton negativeButton;
    private RelaxDialog.RelaxDialogButton neutralButton;
    private OnCancelListener onCancelListener;
    private OnDismissListener onDismissListener;
    private OnKeyListener onKeyListener;
    private RelaxDialog.RelaxDialogButton positiveButton;
    private String title;

    private RelaxDialogAttributes() {
      this.isCancelable = true;
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
}
