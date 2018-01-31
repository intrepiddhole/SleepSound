package ipnossoft.rma.favorites;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import ipnossoft.rma.PersistedDataManager;
import ipnossoft.rma.RelaxMelodiesApp;
import ipnossoft.rma.free.R;
import ipnossoft.rma.media.SoundManager;
import ipnossoft.rma.ui.button.FavoriteAnimatedButton;
import ipnossoft.rma.ui.dialog.RelaxDialog;
import ipnossoft.rma.util.RelaxAnalytics;

public class StaffPicksOnClickListener implements OnClickListener {
  private Activity activity;
  private StaffPicksScrollViewAdapter adapter;
  private StaffFavoriteSounds favoriteSound;
  private RelaxDialog upgradeAlertDialog;

  public StaffPicksOnClickListener(StaffFavoriteSounds var1, StaffPicksScrollViewAdapter var2, Activity var3) {
    this.favoriteSound = var1;
    this.adapter = var2;
    this.activity = var3;
  }

  private void animateStaffFavorite(View var1) {
    FavoriteAnimatedButton var2 = (FavoriteAnimatedButton)var1.findViewById(R.id.staff_picks_all_sounds_layout);
    var2.setAnimated(true);
    var2.setPivotY((float)((int)this.activity.getResources().getDimension(R.dimen.sound_image_height)) * 0.17447917F);
    var2.setPivotX((float)(var2.getWidth() / 2));
  }

  private int convertStringToDrawableResourceId(Activity var1, String var2) {
    return var1.getResources().getIdentifier(var2, "drawable", var1.getPackageName());
  }

  private boolean isStaffPickSelected() {
    String var1 = PersistedDataManager.getString("selectedStaffPick", "", RelaxMelodiesApp.getInstance().getApplicationContext());
    return this.favoriteSound.getIdentifier().equals(var1);
  }

  private void playOrStopStaffFavorite(View var1) {
    if(!this.isStaffPickSelected()) {
      this.playStaffFavorite(var1);
    } else {
      this.stopStaffFavorite(var1);
    }
  }

  private void playStaffFavorite(View var1) {
    RelaxAnalytics.logPlayStaffPick(this.favoriteSound.getIdentifier());
    SoundManager.getInstance().playSoundSelection(this.favoriteSound, this.activity);
    this.showStaffPickAsSelected(var1);
    this.adapter.setSelectedStaffPick(this.favoriteSound);
  }

  private void showStaffPickAsSelected(View var1) {
    ImageView var3 = (ImageView)var1.findViewById(R.id.staff_pick_inner_cell_image_view);
    ImageView var4 = (ImageView)var1.findViewById(R.id.favorite_staff_picks_sound_stack);
    int var2 = this.convertStringToDrawableResourceId(this.activity, this.favoriteSound.getSelectedImageResourceEntryName());
    Glide.with(this.activity).load(Integer.valueOf(var2)).placeholder(var2).dontAnimate().into(var3);
    Glide.with(this.activity).load(Integer.valueOf(R.drawable.favorite_sound_stackselected)).placeholder(R.drawable.favorite_sound_stackselected).dontAnimate().into(var4);
    var1.setSelected(true);
    this.animateStaffFavorite(var1);
  }

  private void showStaffPickAsUnSelected(View var1) {
    ImageView var2 = (ImageView)var1.findViewById(R.id.staff_pick_inner_cell_image_view);
    ImageView var3 = (ImageView)var1.findViewById(R.id.favorite_staff_picks_sound_stack);
    int var4 = this.convertStringToDrawableResourceId(this.activity, this.favoriteSound.getImageResourceEntryName());
    Glide.with(this.activity).load(Integer.valueOf(var4)).placeholder(var4).dontAnimate().into(var2);
    Glide.with(this.activity).load(Integer.valueOf(R.drawable.favorite_sound_stacknormal)).placeholder(R.drawable.favorite_sound_stacknormal).dontAnimate().into(var3);
    var1.setSelected(false);
    this.stopAnimationStaffFavorites(var1);
  }

  private void stopAnimationStaffFavorites(View var1) {
    ((FavoriteAnimatedButton)var1.findViewById(R.id.staff_picks_all_sounds_layout)).setAnimated(false);
  }

  private void stopStaffFavorite(View var1) {
    this.adapter.setSelectedStaffPick((StaffFavoriteSounds)null);
    SoundManager.getInstance().clearAll();
    this.showStaffPickAsUnSelected(var1);
  }

  public void onClick(View var1) {
    if(!FavoriteStatusHandler.favoriteContainsLockedSounds(this.favoriteSound)) {
      if(FavoriteStatusHandler.favoriteContainsUnplayableSounds(this.favoriteSound)) {
        SoundManager.getInstance().downloadSounds(this.activity);
      } else {
        this.playOrStopStaffFavorite(var1);
      }
    } else {
      FavoriteStatusHandler.showUpgradeAlert(this.activity, this.upgradeAlertDialog);
    }
  }
}
