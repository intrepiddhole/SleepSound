
package ipnossoft.rma.timer;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.TextView;
import com.quietlycoding.android.picker.NumberPicker;
import ipnossoft.rma.MainActivity;
import ipnossoft.rma.NavigationMenuItemFragment;
import ipnossoft.rma.PersistedDataManager;
import ipnossoft.rma.RelaxMelodiesApp;
import ipnossoft.rma.animation.ViewTransitionAnimator;
import ipnossoft.rma.free.R;
import ipnossoft.rma.media.SoundManager;
import ipnossoft.rma.ui.dialog.RelaxDialog.Builder;
import ipnossoft.rma.ui.dialog.RelaxDialog.RelaxDialogButtonOrientation;
import ipnossoft.rma.util.RelaxAnalytics;
import ipnossoft.rma.util.Utils;

public class TimerFragment extends Fragment implements OnClickListener, TimerListener, ServiceConnection, NavigationMenuItemFragment {
  private static final int MILLISECONDS_PER_HOUR = 3600000;
  private static final int MILLISECONDS_PER_MINUTE = 60000;
  private static final String PREF_PICKER_HOURS = "number_picker_hours";
  private static final String PREF_PICKER_MINUTES = "number_picker_minutes";
  private static final String PREF_STOP_APP = "stop_app_timer";
  private RelaxMelodiesApp app;
  private CheckBox checkboxStopApp;
  private boolean isShowingCustomDialog = false;
  private SharedPreferences prefs;
  private TextView textCountdown;
  private View timerRunningContainer;
  private View timerSelectionContainer;
  private TimerTask timerTask;
  private ViewTransitionAnimator viewTransitionAnimator;

  public TimerFragment() {
  }

  private boolean areViewsTransitioning() {
    return this.viewTransitionAnimator.areViewsTransitioning();
  }

  private void hideRunningTimer() {
    this.viewTransitionAnimator.transitionBetweenViews(this.timerRunningContainer, this.timerSelectionContainer);
  }

  private void hideRunningTimerInstant() {
    this.timerSelectionContainer.setVisibility(View.VISIBLE);
    this.timerSelectionContainer.setAlpha(1.0F);
    this.timerRunningContainer.setVisibility(View.GONE);
  }

  private void setTimerToStopApp() {
    if(this.timerTask != null) {
      boolean var1 = this.checkboxStopApp.isChecked();
      this.prefs.edit().putBoolean("stop_app_timer", var1).commit();
      this.timerTask.setStopApp(var1);
      RelaxAnalytics.logTimerStopApp(var1);
    }

  }

  private void showAppropriateLayout() {
    if(this.app != null) {
      this.timerTask = this.app.getTimerTask();
      if(this.timerTask == null || this.timerTask.isFinished()) {
        this.textCountdown.setText("");
        this.hideRunningTimerInstant();
        return;
      }

      this.timerTask.addListener(this);
      this.checkboxStopApp.setChecked(this.timerTask.isStopApp());
      this.showRunningTimerInstant();
    }

  }

  private void showRunningTimer() {
    this.viewTransitionAnimator.transitionBetweenViews(this.timerSelectionContainer, this.timerRunningContainer);
  }

  private void showRunningTimerInstant() {
    this.timerRunningContainer.setVisibility(View.VISIBLE);
    this.timerRunningContainer.setAlpha(1.0F);
    this.timerSelectionContainer.setVisibility(View.GONE);
  }

  private void showTimerPickerDialog() {
    if(!this.isShowingCustomDialog) {
      this.isShowingCustomDialog = true;
      FragmentActivity var1 = this.getActivity();
      Builder var2 = new Builder(var1, RelaxDialogButtonOrientation.VERTICAL);
      var2.setTitle(R.string.timer_activity_dialog_custom_title);
      View var4 = ((LayoutInflater)var1.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.timer_picker_dialog, (ViewGroup)null);
      var2.setCustomContentView(var4);
      final NumberPicker var3 = (NumberPicker)var4.findViewById(R.id.num_picker_hours);
      var3.setFormatter(NumberPicker.TWO_DIGIT_FORMATTER);
      var3.setRange(0, 23);
      var3.setCurrent(PersistedDataManager.getInteger("number_picker_hours", 0, var1));
      final NumberPicker var5 = (NumberPicker)var4.findViewById(R.id.num_picker_minutes);
      var5.setFormatter(NumberPicker.TWO_DIGIT_FORMATTER);
      var5.setRange(0, 59);
      var5.setCurrent(PersistedDataManager.getInteger("number_picker_minutes", 1, var1));
      var2.setNegativeButton(var1.getString(R.string.timer_activity_dialog_custom_no), (OnClickListener)null);
      var2.setOnDismissListener(new OnDismissListener() {
        public void onDismiss(DialogInterface var1) {
          TimerFragment.this.isShowingCustomDialog = false;
        }
      });
      var2.setPositiveButton(var1.getString(R.string.timer_activity_dialog_custom_yes), new OnClickListener() {
        public void onClick(View var1) {
          int var2 = var3.getCurrentText();
          int var3x = var5.getCurrentText();
          PersistedDataManager.saveInteger("number_picker_hours", var2, TimerFragment.this.getContext());
          PersistedDataManager.saveInteger("number_picker_minutes", var3x, TimerFragment.this.getContext());
          TimerFragment.this.startTimerTask(3600000 * var2 + '\uea60' * var3x);
        }
      });
      var2.show();
    }

  }

  private void startTimerTask(int var1) {
    if(this.timerTask != null) {
      this.timerTask.cancel(true);
    }

    RelaxAnalytics.logTimerActivated((int)((float)var1 / 1000.0F / 60.0F), this.checkboxStopApp.isChecked());
    this.timerTask = new TimerTask((long)var1, SoundManager.getInstance(), this.checkboxStopApp.isChecked(), this.getActivity());
    this.timerTask.addListener(this);
    Utils.executeTask(this.timerTask, new Void[0]);
    this.app.setTimerTask(this.timerTask);
    this.showRunningTimer();
  }

  private void stopTimerTask() {
    if(this.timerTask != null) {
      this.timerTask.cancel(true);
      this.timerTask = null;
      SoundManager.getInstance().stopFadeout();
      this.app.setTimerTask((TimerTask)null);
      this.app.stopAndSaveTimerTask();
    }

    this.textCountdown.setText("");
    this.hideRunningTimer();
    ((MainActivity)this.getActivity()).onTimerUpdate("");
  }

  public void onClick(View var1) {
    if(!this.areViewsTransitioning()) {
      if(var1.getId() == R.id.timer_text_cancel) {
        this.stopTimerTask();
      } else {
        if(var1.getId() == R.id.timer_button_10_min) {
          this.startTimerTask(600000);
          return;
        }

        if(var1.getId() == R.id.timer_button_20_min) {
          this.startTimerTask(1200000);
          return;
        }

        if(var1.getId() == R.id.timer_button_30_min) {
          this.startTimerTask(1800000);
          return;
        }

        if(var1.getId() == R.id.timer_button_1_hr) {
          this.startTimerTask(3600000);
          return;
        }

        if(var1.getId() == R.id.timer_button_2_hr) {
          this.startTimerTask(7200000);
          return;
        }

        if(var1.getId() == R.id.timer_button_custom) {
          this.showTimerPickerDialog();
          return;
        }

        if(var1.getId() == R.id.timer_checkbox_stop_app) {
          this.setTimerToStopApp();
          return;
        }
      }
    }

  }

  public View onCreateView(LayoutInflater var1, ViewGroup var2, Bundle var3) {
    this.app = (RelaxMelodiesApp)this.getActivity().getApplicationContext();
    this.viewTransitionAnimator = new ViewTransitionAnimator();
    View var5 = var1.inflate(R.layout.timer, var2, false);
    var5.findViewById(R.id.timer_text_cancel).setOnClickListener(this);
    var5.findViewById(R.id.timer_checkbox_stop_app).setOnClickListener(this);
    var5.findViewById(R.id.timer_button_10_min).setOnClickListener(this);
    var5.findViewById(R.id.timer_button_20_min).setOnClickListener(this);
    var5.findViewById(R.id.timer_button_30_min).setOnClickListener(this);
    var5.findViewById(R.id.timer_button_1_hr).setOnClickListener(this);
    var5.findViewById(R.id.timer_button_2_hr).setOnClickListener(this);
    var5.findViewById(R.id.timer_button_custom).setOnClickListener(this);
    this.timerRunningContainer = var5.findViewById(R.id.timer_running_container);
    this.timerSelectionContainer = var5.findViewById(R.id.timer_selection_container);
    this.textCountdown = (TextView)var5.findViewById(R.id.timer_text_countdown);
    this.checkboxStopApp = (CheckBox)var5.findViewById(R.id.timer_checkbox_stop_app);
    this.prefs = PreferenceManager.getDefaultSharedPreferences(this.getContext());
    boolean var4 = this.prefs.getBoolean("stop_app_timer", false);
    this.checkboxStopApp.setChecked(Boolean.valueOf(var4).booleanValue());
    return var5;
  }

  public void onPause() {
    super.onPause();
    if(this.timerTask != null) {
      this.timerTask.setStopApp(this.checkboxStopApp.isChecked());
      this.timerTask.addListener((MainActivity)this.getActivity());
    }

    if(this.timerRunningContainer != null) {
      this.timerRunningContainer.setVisibility(View.GONE);
    }

    if(this.timerSelectionContainer != null) {
      this.timerSelectionContainer.setVisibility(View.GONE);
    }

  }

  public void onResume() {
    super.onResume();
    this.showAppropriateLayout();
  }

  public void onServiceConnected(ComponentName var1, IBinder var2) {
    this.app.restoreTimer(this.getActivity());
    this.showAppropriateLayout();
  }

  public void onServiceDisconnected(ComponentName var1) {
  }

  public void onTimerComplete(boolean var1) {
    this.textCountdown.setText("");
    this.hideRunningTimer();
  }

  public void onTimerUpdate(String var1) {
    this.textCountdown.setText(var1);
  }
}
