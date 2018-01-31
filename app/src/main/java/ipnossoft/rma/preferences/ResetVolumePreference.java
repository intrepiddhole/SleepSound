package ipnossoft.rma.preferences;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.widget.Toast;

import ipnossoft.rma.free.R;
import ipnossoft.rma.media.SoundManager;
import ipnossoft.rma.util.RelaxAnalytics;

public class ResetVolumePreference extends DialogPreference {
  public ResetVolumePreference(Context var1, AttributeSet var2) {
    super(var1, var2);
    this.setDialogTitle(R.string.prefs_reset_sounds_dialog_title);
    this.setDialogMessage(R.string.prefs_reset_sounds_dialog_message);
    this.setPositiveButtonText(R.string.prefs_reset_sounds_dialog_yes);
    this.setNegativeButtonText(R.string.prefs_reset_sounds_dialog_no);
    if(SoundManager.getInstance() == null) {
      var1.startService(new Intent(var1, SoundManager.class));
    }

  }

  public void onClick(DialogInterface var1, int var2) {
    boolean var3;
    if(var2 == -1) {
      var3 = true;
    } else {
      var3 = false;
    }

    RelaxAnalytics.logResetSoundVolumes(var3);
    if(var2 == -1) {
      SoundManager var4 = SoundManager.getInstance();
      if(var4 != null) {
        var4.resetVolumes();
      } else {
        Toast.makeText(this.getContext(), R.string.prefs_reset_sounds_error_connection, 0).show();
      }
    }
  }
}
