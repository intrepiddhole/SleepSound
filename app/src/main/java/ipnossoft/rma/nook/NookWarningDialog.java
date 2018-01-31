package ipnossoft.rma.nook;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View.OnClickListener;
import ipnossoft.rma.free.R;
import ipnossoft.rma.ui.dialog.RelaxDialog.Builder;
import ipnossoft.rma.ui.dialog.RelaxDialog.RelaxDialogButtonOrientation;

public class NookWarningDialog extends DialogFragment {
  public NookWarningDialog() {
  }

  public static NookWarningDialog newInstance() {
    NookWarningDialog var0 = new NookWarningDialog();
    var0.setCancelable(false);
    return var0;
  }

  @NonNull
  public Dialog onCreateDialog(Bundle var1) {
    Builder var2 = new Builder(this.getActivity(), RelaxDialogButtonOrientation.VERTICAL);
    var2.setTitle(17039380);
    var2.setMessage(this.getString(R.string.nook_warning_dialog_message));
    var2.setPositiveButton(17039370, (OnClickListener)null);
    return var2.create();
  }
}