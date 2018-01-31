package ipnossoft.rma.favorites;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import ipnossoft.rma.media.SoundManager;
import ipnossoft.rma.ui.dialog.RelaxDialog;
import ipnossoft.rma.util.RelaxAnalytics;

public class FavoriteListViewAdapterOnItemClickListener implements OnClickListener {
  Activity activity;
  FavoriteListViewAdapter adapter;
  FavoriteSounds favoriteSound;
  int position;
  RelaxDialog upgradeAlertDialog;

  public FavoriteListViewAdapterOnItemClickListener(FavoriteSounds var1, int var2, Activity var3, FavoriteListViewAdapter var4) {
    this.favoriteSound = var1;
    this.position = var2;
    this.activity = var3;
    this.adapter = var4;
  }

  private void playFavorite() {
    SoundManager.getInstance().playSoundSelection(this.favoriteSound, this.activity);
    this.adapter.onFavoriteCellClicked(this.favoriteSound.getId());
  }

  private void stopFavorite() {
    SoundManager.getInstance().clearAll();
  }

  public void onClick(View var1) {
    if(!FavoriteStatusHandler.favoriteContainsLockedSounds(this.favoriteSound)) {
      if(this.adapter.isCellSelectedAtIndex(this.position)) {
        RelaxAnalytics.logStopFavorite();
        this.stopFavorite();
        this.adapter.removePlayingFavorite();
      } else {
        RelaxAnalytics.logPlayFavorite();
        this.playFavorite();
      }
    } else {
      FavoriteStatusHandler.showUpgradeAlert(this.activity, this.upgradeAlertDialog);
    }
  }
}
