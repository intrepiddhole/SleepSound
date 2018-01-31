package ipnossoft.rma.tstore;

import android.app.ProgressDialog;
import android.util.Log;
import com.skt.arm.ArmListener;
import com.skt.arm.ArmManager;
import ipnossoft.rma.MainActivity;
import ipnossoft.rma.free.R;

public class TstoreManager implements ArmListener {
  private static final String TAG = "TstoreManager";
  private final MainActivity activity;
  private ArmManager arm;
  private ProgressDialog pDlg;

  public TstoreManager(MainActivity var1) {
    this.activity = var1;
  }

  private void showExitDialog() {
    ExitDialog.newInstance().show(this.activity.getSupportFragmentManager(), "tstore-dialog");
  }

  public void onArmResult() {
    this.pDlg.dismiss();
    if(this.arm.nNetState != 1) {
      this.showExitDialog();
    }

  }

  public void startVerification() {
    String var1 = this.activity.getString(R.string.tstore_progress_dialog_title);
    String var2 = this.activity.getString(R.string.tstore_progress_dialog_message);
    this.pDlg = ProgressDialog.show(this.activity, var1, var2);
    this.arm = new ArmManager(this.activity);
    this.arm.setArmListener(this);

    try {
      this.arm.ARM_Plugin_ExecuteARM(this.activity.getString(R.string.tstore_app_key));
    } catch (Exception var3) {
      this.pDlg.dismiss();
      Log.e("TstoreManager", "Error while executing tstore ARM", var3);
      this.showExitDialog();
    }
  }
}
