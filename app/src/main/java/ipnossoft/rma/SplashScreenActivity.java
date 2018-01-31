package ipnossoft.rma;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.ipnossoft.api.featuremanager.FeatureManager;
import com.ipnossoft.api.soundlibrary.SoundLibrary;
import ipnossoft.rma.feature.RelaxFeatureManager;
import ipnossoft.rma.feature.RelaxFeatureManagerCallback;
import ipnossoft.rma.free.R;
import ipnossoft.rma.util.RelaxAnalytics;

public abstract class SplashScreenActivity extends Activity {
  public SplashScreenActivity() {
  }

  private void showSounds() {
    NavigationHelper.showSounds(this);
  }

  protected void onCreate(Bundle var1) {
    super.onCreate(var1);
    Intent var2 = this.getIntent();
    if(var2 != null && (var2.getFlags() & 4194304) != 0) {
      this.finish();
    }

    this.setContentView(R.layout.splash);
  }

  boolean processApplicationPreLoading() {
    RelaxMelodiesApp.areServiceInitialized = true;
    Context var1 = this.getApplicationContext();
    RelaxAnalytics.initialize(var1);
    RelaxFeatureManager.configureRelaxFeatureManager(this, new RelaxFeatureManagerCallback());
    SoundLibrary.getInstance().configureSoundLibrary(var1);
    return true;
  }

  void splashScreenLoadingFinished() {
    FeatureManager.getInstance().fetchAvailableFeatures();
    this.showSounds();
    this.finish();
  }
}
