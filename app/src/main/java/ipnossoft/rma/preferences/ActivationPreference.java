package ipnossoft.rma.preferences;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import com.ipnossoft.api.featuremanager.FeatureManager;
import com.ipnossoft.api.promocode.PromoCodeService;
import com.ipnossoft.api.promocode.client.PromoCodeServiceImplementation;
import com.ipnossoft.api.promocode.exceptions.PromoCodeException;
import com.ipnossoft.api.promocode.exceptions.PromoCodeUnexpectedException;
import com.ipnossoft.api.promocode.model.PromoCodeRedeemResult;
import com.ipnossoft.api.soundlibrary.SoundLibrary;
import ipnossoft.rma.*;
import ipnossoft.rma.free.R;
import ipnossoft.rma.util.RelaxAnalytics;
import java.net.URLEncoder;
import java.util.Properties;

// Referenced classes of package ipnossoft.rma.preferences:
//            ActionBarPreferenceActivity


//cavaj
public class ActivationPreference extends DialogPreference
{
  private class PromoCodeServiceRedeemer extends AsyncTask
  {

    String appstoreName;
    private Context context;
    android.app.AlertDialog.Builder promotionResultBuilder;

    protected Integer doInBackground(String as[])
    {
      String s;
      try {
        s = URLEncoder.encode(as[0], "UTF-8");
        Properties as_1 = RelaxPropertyHandler.getInstance().getProperties();
        PromoCodeServiceImplementation as_2 = new PromoCodeServiceImplementation(RelaxMelodiesApp.getRelaxServerURL(), as_1.getProperty("RELAX_SERVER_USERNAME"), as_1.getProperty("RELAX_SERVER_API_KEY"), as_1.getProperty("RELAX_APP_CODE"));
        final PromoCodeRedeemResult promocoderedeemresult = as_2.redeem(s);
        String as_3;
        if (promocoderedeemresult.getFeatureId() != null) {
          as_3 = promocoderedeemresult.getFeatureId();
          ((Activity) getContext()).runOnUiThread(new Runnable() {
            public void run() {
              appstoreName = FeatureManager.getInstance().redeemFeature(promocoderedeemresult.getFeatureId());
            }
          });
        } else {
          as_3 = "premium_sounds";
          PersistedDataManager.saveBoolean("is_promotion_premium", true, RelaxMelodiesApp.getInstance().getApplicationContext());
          SoundLibrary.getInstance().notifyLockedSounds();
        }
        promotionResultBuilder.setNeutralButton(R.string.activation_preference_close, new android.content.DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialoginterface, int i) {
            actionBarPreferenceActivity.onCorrectCode();
          }
        });
        RelaxAnalytics.logActivationCodeResult(s, as_3, true);
        return Integer.valueOf(R.string.activation_preference_correct_activation_code);
      } catch (Exception e1){
        try
        {
          //RelaxAnalytics.logActivationCodeResult(s, null, false);
          promotionResultBuilder.setNeutralButton(R.string.activation_preference_close, new android.content.DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialoginterface, int i)
            {
              actionBarPreferenceActivity.onWrongCode();
            }
          });
          return Integer.valueOf(R.string.activation_preference_error_connecting_to_server);
        } catch(Exception e) {
          RelaxAnalytics.logActivationCodeResult(null, null, false);
          Log.e("RMA", e.toString());
          return Integer.valueOf(R.string.activation_preference_wrong_activation_code);
        }
      }

      /*RelaxAnalytics.logActivationCodeResult(s, null, false);
      promotionResultBuilder.setNeutralButton(R.string.activation_preference_close, new android.content.DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialoginterface, int i)
        {
          actionBarPreferenceActivity.onWrongCode();
        }
      });
      i = R.string.activation_preference_wrong_activation_code;
      return Integer.valueOf(i);*/
    }

    protected Object doInBackground(Object aobj[])
    {
      return doInBackground((String[])aobj);
    }

    protected void onPostExecute(Integer integer)
    {
      if(isContextActivityAvailable(context))
      {
        android.app.AlertDialog.Builder builder = promotionResultBuilder;
        String integer_1;
        if(appstoreName == null || appstoreName.isEmpty())
        {
          integer_1 = getContext().getString(integer.intValue());
        } else
        {
          integer_1 = (new StringBuilder()).append(getContext().getString(R.string.activation_preference_correct_activation_code_unlock)).append(appstoreName).toString();
        }
        builder.setMessage(integer_1);
        promotionResultBuilder.show();
      }
    }

    protected void onPostExecute(Object obj)
    {
      onPostExecute((Integer)obj);
    }

    protected void onPreExecute()
    {
      context = getContext();
      promotionResultBuilder = new android.app.AlertDialog.Builder(context);
      promotionResultBuilder.setTitle(R.string.activation_preference_activation_code);
    }

    protected void onProgressUpdate(Object aobj[])
    {
      onProgressUpdate((Void[])aobj);
    }

    protected void onProgressUpdate(Void avoid[])
    {
    }

    private PromoCodeServiceRedeemer()
    {

    }

  }


  private ActionBarPreferenceActivity actionBarPreferenceActivity;
  private EditText activationCodeEditText;

  public ActivationPreference(Context context, AttributeSet attributeset)
  {
    super(context, attributeset);
    setDialogTitle(R.string.activation_preference_enter_activation_code);
    setPositiveButtonText(R.string.activation_preference_activate);
    setNegativeButtonText(R.string.timer_activity_dialog_custom_no);
    setDialogLayoutResource(R.layout.activation_dialog);
    actionBarPreferenceActivity = (ActionBarPreferenceActivity)context;
  }

  private boolean checkIfPromoCodeForceCrashApp(String s)
  {
    return false;
  }

  private boolean checkIfPromoCodeToConsumeInAppPurchases(String s)
  {
    new ipnossoft.rma.ui.dialog.RelaxDialog.Builder(getContext(), ipnossoft.rma.ui.dialog.RelaxDialog.RelaxDialogButtonOrientation.VERTICAL);
    return false;
  }

  private boolean isContextActivityAvailable(Context context)
  {
    boolean flag;
    flag = true;
    if(context == null || !(context instanceof Activity))
    {
      return false;
    }
    Activity context_1 = (Activity)context;
    if(android.os.Build.VERSION.SDK_INT < 17) {
      if(!context_1.isFinishing())
        return flag;
      return false;
    }
    if(context_1.isDestroyed() || context_1.isFinishing())
    {
      flag = false;
    }
    return flag;
  }

  public void onBindDialogView(View view)
  {
    super.onBindDialogView(view);
    activationCodeEditText = (EditText)view.findViewById(R.id.activation_code);
    activationCodeEditText.requestFocus();
    ((InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(2, 1);
  }

  public void onClick(DialogInterface dialoginterface, int i)
  {
    if(i == -1)
    {
      RelaxAnalytics.logActivationCodeDialog();
      PromoCodeServiceRedeemer dialoginterface_1 = new PromoCodeServiceRedeemer();
      String s;
      for(s = activationCodeEditText.getText().toString(); checkIfPromoCodeToConsumeInAppPurchases(s) || checkIfPromoCodeForceCrashApp(s);)
      {
        return;
      }

      dialoginterface_1.execute(new String[] {
              s
      });
    }
    ((InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(activationCodeEditText.getWindowToken(), 0);
  }

  protected void onPrepareDialogBuilder(android.app.AlertDialog.Builder builder)
  {
    super.onPrepareDialogBuilder(builder);
    builder.setCancelable(false);
  }


}
