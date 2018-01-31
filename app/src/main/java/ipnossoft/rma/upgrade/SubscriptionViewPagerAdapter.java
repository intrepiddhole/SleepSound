package ipnossoft.rma.upgrade;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.ipnossoft.api.soundlibrary.SoundLibrary;
import ipnossoft.rma.RelaxMelodiesApp;
import ipnossoft.rma.free.R;

public class SubscriptionViewPagerAdapter extends FragmentPagerAdapter {
  private final Activity activity;
  private SubscriptionPageFragment[] fragments;

  public SubscriptionViewPagerAdapter(FragmentManager var1, Activity var2) {
    super(var1);
    this.activity = var2;
    Boolean var4 = RelaxMelodiesApp.isPremium();
    byte var3;
    if(var4.booleanValue()) {
      var3 = 1;
    } else {
      var3 = 4;
    }

    this.fragments = new SubscriptionPageFragment[var3];
    if(var4.booleanValue()) {
      var3 = 0;
    } else {
      var3 = 1;
    }

    this.fragments[var3] = SubscriptionPageFragment.newInstance(this.populateGuidedMeditationCountString(), var2.getString(R.string.subscription_view_pager_meditation_subText1), var2.getString(R.string.subscription_view_pager_meditation_text2));
    if(!var4.booleanValue()) {
      this.fragments[0] = SubscriptionPageFragment.newInstance(this.populateSoundCountStrings(), (String)null, var2.getString(R.string.subscription_view_pager_sounds_text2));
      this.fragments[2] = SubscriptionPageFragment.newInstance(var2.getString(R.string.subscription_view_pager_background_audio_text1), (String)null, var2.getString(R.string.subscription_view_pager_background_audio_text2));
      this.fragments[3] = SubscriptionPageFragment.newInstance(var2.getString(R.string.subscription_view_pager_ad_free_text1), (String)null, var2.getString(R.string.subscription_view_pager_ad_free_text2));
    }

  }

  @NonNull
  private String populateGuidedMeditationCountString() {
    return String.format(this.activity.getString(R.string.subscription_view_pager_meditation_text1), new Object[]{Integer.valueOf(SoundLibrary.getInstance().getGuidedMeditationSounds().size())});
  }

  @NonNull
  private String populateSoundCountStrings() {
    int var1 = SoundLibrary.getInstance().getBinauralSounds().size();
    int var2 = SoundLibrary.getInstance().getIsochronicSounds().size();
    int var3 = SoundLibrary.getInstance().getAmbientSound().size();
    return String.format(this.activity.getString(R.string.subscription_view_pager_sounds_text1), new Object[]{Integer.valueOf(var1 + var2 + var3)});
  }

  public int getCount() {
    return RelaxMelodiesApp.isPremium().booleanValue()?1:4;
  }

  public Fragment getItem(int var1) {
    switch(var1) {
      case 0:
        return this.fragments[0];
      case 1:
        return this.fragments[1];
      case 2:
        return this.fragments[2];
      case 3:
        return this.fragments[3];
      default:
        return null;
    }
  }
}
