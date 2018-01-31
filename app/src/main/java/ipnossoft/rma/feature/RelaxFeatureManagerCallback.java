package ipnossoft.rma.feature;

import android.app.Activity;
import com.ipnossoft.api.featuremanager.FeatureManagerCallback;

public class RelaxFeatureManagerCallback implements FeatureManagerCallback {
  public RelaxFeatureManagerCallback() {
  }

  public boolean preOpenIABSetup(Activity var1) {
    return true;
  }
}
