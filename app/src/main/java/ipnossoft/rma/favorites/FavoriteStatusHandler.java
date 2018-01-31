package ipnossoft.rma.favorites;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import com.ipnossoft.api.soundlibrary.Sound;
import com.ipnossoft.api.soundlibrary.SoundLibrary;
import com.ipnossoft.api.soundlibrary.sounds.GuidedMeditationSound;
import ipnossoft.rma.NavigationHelper;
import ipnossoft.rma.RelaxMelodiesApp;
import ipnossoft.rma.free.R;
import ipnossoft.rma.guidedmeditations.GuidedMeditationStatus;
import ipnossoft.rma.media.SoundTrackInfo;
import ipnossoft.rma.ui.dialog.RelaxDialog;
import ipnossoft.rma.ui.dialog.RelaxDialog.Builder;
import ipnossoft.rma.ui.dialog.RelaxDialog.RelaxDialogButtonOrientation;
import ipnossoft.rma.util.RelaxAnalytics;
import java.util.List;

public class FavoriteStatusHandler {
  public FavoriteStatusHandler() {
  }

  public static boolean favoriteContainsLockedSounds(FavoriteSounds var0) {
    SoundLibrary var1 = SoundLibrary.getInstance();
    List var4 = var0.getTrackInfos();

    for(int var3 = 0; var3 < var4.size(); ++var3) {
      Sound var2 = (Sound)var1.getSound(((SoundTrackInfo)var4.get(var3)).getSoundId());
      if(var2.isLockedWithPremiumEnabled(RelaxMelodiesApp.isPremium().booleanValue()) && !soundIsSeenMeditation(var2)) {
        return true;
      }
    }

    return false;
  }

  public static boolean favoriteContainsUnplayableSounds(FavoriteSounds var0) {
    SoundLibrary var1 = SoundLibrary.getInstance();
    List var3 = var0.getTrackInfos();

    for(int var2 = 0; var2 < var3.size(); ++var2) {
      if(!((Sound)var1.getSound(((SoundTrackInfo)var3.get(var2)).getSoundId())).isPlayable()) {
        return true;
      }
    }

    return false;
  }

  private static void openUpgradeScreen(Activity var0) {
    RelaxAnalytics.logUpgradeFromFavorites();
    NavigationHelper.showSubscription(var0);
  }

  public static void showUpgradeAlert(final Activity var0, RelaxDialog var1) {
    if(var1 != null) {
      var1.hide();
    }

    Builder var2 = new Builder(var0, RelaxDialogButtonOrientation.VERTICAL);
    var2.setTitle(var0.getString(R.string.mix_contains_locked_sounds_title));
    var2.setMessage(var0.getString(R.string.mix_contains_locked_sounds_message)).setCancelable(false).setPositiveButton(var0.getString(R.string.activity_title_upgrade), new OnClickListener() {
      public void onClick(View var1) {
        FavoriteStatusHandler.openUpgradeScreen(var0);
      }
    }).setNegativeButton(var0.getString(R.string.cancel), (OnClickListener)null);
    var2.create().show();
  }

  private static boolean soundIsSeenMeditation(Sound var0) {
    return var0 instanceof GuidedMeditationSound?GuidedMeditationStatus.getInstance().didSeeFreeMeditation((GuidedMeditationSound)var0):false;
  }
}
