package ipnossoft.rma.tstore;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.View.OnClickListener;
import ipnossoft.rma.free.R;
import ipnossoft.rma.ui.dialog.RelaxDialog.Builder;
import ipnossoft.rma.ui.dialog.RelaxDialog.RelaxDialogButtonOrientation;

public class ExitDialog extends DialogFragment {
  public ExitDialog() {
  }

  public static ExitDialog newInstance() {
    ExitDialog var0 = new ExitDialog();
    var0.setCancelable(false);
    return var0;
  }

  @NonNull
  public Dialog onCreateDialog(Bundle var1) {
    Builder var2 = new Builder(this.getActivity(), RelaxDialogButtonOrientation.VERTICAL);
    var2.setTitle(17039380);
    var2.setMessage(this.getString(R.string.tstore_exit_dialog_message));
    var2.setPositiveButton(17039370, new OnClickListener() {
      public void onClick(View var1) {
        ExitDialog.this.getActivity().finish();
      }
    });
    return var2.create();
  }
}
