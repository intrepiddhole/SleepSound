package ipnossoft.rma.guidedmeditations;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import com.ipnossoft.api.featuremanager.FeatureManager;
import com.ipnossoft.api.soundlibrary.sounds.GuidedMeditationSound;
import ipnossoft.rma.NavigationHelper;
import ipnossoft.rma.RelaxMelodiesApp;
import ipnossoft.rma.free.R;
import ipnossoft.rma.media.SoundManager;
import ipnossoft.rma.util.RelaxAnalytics;
import java.util.List;

class GuidedMeditationOnItemClickListener implements OnItemClickListener {
  private static final int TIME_BETWEEN_MEDITATION_SELECTION = 1000;
  private Activity activity;
  private List<GuidedMeditationSound> guidedMeditationSounds;
  private boolean hasLanguageHeaderView;

  public GuidedMeditationOnItemClickListener(List<GuidedMeditationSound> var1, Activity var2, boolean var3) {
    this.guidedMeditationSounds = var1;
    this.activity = var2;
    this.hasLanguageHeaderView = var3;
  }

  private boolean hasNotPassedSpamProtection() {
    return GuidedMeditationStatus.getInstance().getLastGuidedClickedTimeStamp() != 0L && System.currentTimeMillis() <= GuidedMeditationStatus.getInstance().getLastGuidedClickedTimeStamp() + 1000L;
  }

  public void onItemClick(AdapterView<?> var1, View var2, int var3, long var4) {
    if(var2.getId() != R.id.guided_meditation_header_row && !this.hasNotPassedSpamProtection()) {
      GuidedMeditationStatus.getInstance().setLastGuidedClickedTimeStamp(System.currentTimeMillis());
      if(this.hasLanguageHeaderView) {
        --var3;
      }

      if(var3 >= 0) {
        GuidedMeditationSound var7 = (GuidedMeditationSound)this.guidedMeditationSounds.get((int)var4);
        if(!FeatureManager.getInstance().hasActiveSubscription() && var7.isLockedWithPremiumEnabled(RelaxMelodiesApp.isPremium().booleanValue()) && !GuidedMeditationStatus.getInstance().didSeeFreeMeditation(var7)) {
          RelaxAnalytics.logUpgradeFromMeditations();
          NavigationHelper.showSubscription(this.activity);
          return;
        }

        boolean var6 = SoundManager.getInstance().handleSoundPressed(var7, this.activity);
        if(SoundManager.getInstance().isSelected(var7.getId())) {
          var2.setBackgroundColor(this.activity.getResources().getColor(R.color.fragment_list_selected_cell_color));
          return;
        }

        if(var6) {
          var2.setBackgroundColor(0);
          return;
        }
      }
    }

  }
}
