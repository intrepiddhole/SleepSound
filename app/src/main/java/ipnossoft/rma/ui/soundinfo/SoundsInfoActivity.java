package ipnossoft.rma.ui.soundinfo;

import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.ViewConfiguration;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.ipnossoft.api.dynamiccontent.model.InAppPurchase;
import com.ipnossoft.api.featuremanager.FeatureManager;
import com.ipnossoft.api.featuremanager.FeatureManagerObserver;
import com.ipnossoft.api.featuremanager.data.Subscription;
import com.ipnossoft.api.soundlibrary.Sound;
import ipnossoft.rma.ActionBarListActivity;
import ipnossoft.rma.DefaultServiceConnection;
import ipnossoft.rma.exceptions.SoundLimitReachedException;
import ipnossoft.rma.free.R;
import ipnossoft.rma.media.SoundManager;
import ipnossoft.rma.media.SoundManagerObserver;
import ipnossoft.rma.ui.soundinfo.adapter.SoundInfoAdapter;
import ipnossoft.rma.util.Utils;
import java.util.Date;
import java.util.List;
import org.onepf.oms.appstore.googleUtils.Purchase;

public abstract class SoundsInfoActivity<T extends Sound> extends ActionBarListActivity implements SoundManagerObserver, FeatureManagerObserver {
  private static final float FRICTION_SCALE_FACTOR = 2.5F;
  public static final String INTENT_EXTRA_SOUNDS = "sounds";
  private DefaultServiceConnection serviceConnection;
  private List<T> sounds;
  private int title;

  private SoundsInfoActivity(@StringRes int var1) {
    this.serviceConnection = new DefaultServiceConnection();
    this.title = var1;
  }

  SoundsInfoActivity(@StringRes int var1, List<T> var2) {
    this(var1);
    this.sounds = var2;
  }

  private void updateSoundInfoPopup() {
    if(this.getListView() != null && this.getListAdapter() != null) {
      ((ArrayAdapter)this.getListAdapter()).notifyDataSetChanged();
    }

  }

  protected abstract void addHeaderViews();

  public void onClearSounds(List<Sound> var1) {
    this.updateSoundInfoPopup();
  }

  protected void onCreate(Bundle var1) {
    super.onCreate(var1);
    this.setContentView(R.layout.sound_info_list);
    this.setTitle(this.title);
    ActionBar var2 = this.getSupportActionBar();
    if(var2 != null) {
      var2.setHomeButtonEnabled(true);
      var2.setDisplayHomeAsUpEnabled(true);
    }

    this.addHeaderViews();
    this.getListView().setFriction(ViewConfiguration.getScrollFriction() * 2.5F);
    this.setListAdapter(new SoundInfoAdapter(this, this.sounds));
    ImageView var3 = (ImageView)this.findViewById(R.id.background_overlay);
    Glide.with(this).load(Integer.valueOf(R.drawable.bg_main)).centerCrop().into(var3);
  }

  public void onFeatureDownloaded(InAppPurchase var1, String[] var2) {
    this.updateSoundInfoPopup();
  }

  public void onFeatureManagerSetupFinished() {
  }

  public void onFeatureUnlocked(InAppPurchase var1, String var2) {
    this.updateSoundInfoPopup();
  }

  public boolean onOptionsItemSelected(MenuItem var1) {
    if(var1.getItemId() == 16908332) {
      this.finish();
      return true;
    } else {
      return super.onOptionsItemSelected(var1);
    }
  }

  public void onPausedAllSounds() {
    this.updateSoundInfoPopup();
  }

  public void onPurchaseCompleted(InAppPurchase var1, Purchase var2, Date var3) {
    this.updateSoundInfoPopup();
  }

  public void onPurchasesAvailable(List<InAppPurchase> var1) {
  }

  public void onResumedAllSounds() {
    this.updateSoundInfoPopup();
  }

  public void onSelectionChanged(List<Sound> var1, List<Sound> var2) {
    this.updateSoundInfoPopup();
  }

  public void onSoundManagerException(String var1, Exception var2) {
    if(var2 instanceof SoundLimitReachedException) {
      Utils.uniqueAlert(this, this.getString(R.string.main_activity_too_many_sounds_title), this.getString(R.string.main_activity_too_many_sounds));
    } else {
      Utils.uniqueAlert(this, "Relax Melodies", var2.getMessage());
    }
  }

  public void onSoundPlayed(Sound var1) {
    this.updateSoundInfoPopup();
  }

  public void onSoundStopped(Sound var1, float var2) {
    this.updateSoundInfoPopup();
  }

  public void onSoundVolumeChange(String var1, float var2) {
    this.updateSoundInfoPopup();
  }

  protected void onStart() {
    super.onStart();
    this.serviceConnection.connect(this);
    SoundManager.getInstance().registerObserver(this);
    FeatureManager.getInstance().registerObserver(this);
  }

  protected void onStop() {
    this.serviceConnection.disconnect(this);
    SoundManager.getInstance().unregisterObserver(this);
    FeatureManager.getInstance().unregisterObserver(this);
    super.onStop();
  }

  public void onSubscriptionChanged(Subscription var1, boolean var2) {
    this.updateSoundInfoPopup();
  }

  public void onUnresolvedPurchases(List<String> var1) {
  }
}
