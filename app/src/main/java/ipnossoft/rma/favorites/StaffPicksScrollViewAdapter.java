package ipnossoft.rma.favorites;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ipnossoft.rma.PersistedDataManager;
import ipnossoft.rma.RelaxMelodiesApp;
import ipnossoft.rma.free.R;
import ipnossoft.rma.media.SoundManager;
import ipnossoft.rma.ui.button.FavoriteAnimatedButton;
import ipnossoft.rma.util.Utils;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class StaffPicksScrollViewAdapter {
  public static final float PARTIAL_VISIBLE_RIGHT_CELL_RATIO = 0.8F;
  public static final String PREF_SELECTED_STAFF_PICK = "selectedStaffPick";
  private Activity activity;
  private int cellResourceId;
  private Map<Integer, ViewGroup> cells = new HashMap();
  private LinearLayout parent;
  private StaffFavoriteSounds selectedStaffPick;
  private List<StaffFavoriteSounds> staffFavorites = new ArrayList();
  private HorizontalScrollView staffPicksScrollView;

  public StaffPicksScrollViewAdapter(Activity var1, HorizontalScrollView var2, int var3) {
    this.activity = var1;
    this.staffPicksScrollView = var2;
    this.cellResourceId = var3;
    this.parent = (LinearLayout)this.staffPicksScrollView.findViewById(R.id.staff_pick_horizontal_scroll_view_content);
    Utils.executeTask(new StaffPicksScrollViewAdapter.LoadStaffFavoritesTask(), new Activity[]{var1});
  }

  private LayoutParams adjustCellHorizontalSize(int var1) {
    LayoutParams var2 = new LayoutParams(this.calculateCellWidth(), -2);
    int var3 = this.activity.getResources().getDimensionPixelSize(R.dimen.favorite_staff_picks_first_last_margin_lr);
    if(var1 == 0) {
      var2.setMargins(var3, 0, 0, 0);
      return var2;
    } else if(var1 == this.getCount() - 1) {
      var2.setMargins(0, 0, var3, 0);
      return var2;
    } else {
      var2.setMargins(0, 0, 0, 0);
      return var2;
    }
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

  private boolean isStaffPickSelected(StaffFavoriteSounds var1) {
    String var2 = PersistedDataManager.getString("selectedStaffPick", "", RelaxMelodiesApp.getInstance().getApplicationContext());
    return var1.getIdentifier().equals(var2);
  }

  private void prepareCellView(int var1, ViewGroup var2) {
    TextView var8 = (TextView)var2.findViewById(R.id.staff_pick_inner_cell_text_view);
    ImageView var4 = (ImageView)var2.findViewById(R.id.staff_pick_inner_cell_image_view);
    ImageView var5 = (ImageView)var2.findViewById(R.id.favorite_staff_picks_sound_stack);
    TextView var9 = (TextView)var2.findViewById(R.id.staff_pick_inner_cell_pro_badge);
    ImageView var10 = (ImageView)var2.findViewById(R.id.staff_pick_inner_cell_download_badge);
    ImageView var6 = (ImageView)var2.findViewById(R.id.favorite_staff_picks_sound_rope);
    var2.setOnClickListener(new StaffPicksOnClickListener((StaffFavoriteSounds)this.staffFavorites.get(var1), this, this.activity));
    StaffFavoriteSounds var7 = (StaffFavoriteSounds)this.staffFavorites.get(var1);
    var8.setText(var7.getLabel());
    if(FavoriteStatusHandler.favoriteContainsLockedSounds(var7)) {
      var9.setVisibility(View.VISIBLE);
      var10.setVisibility(View.GONE);
    } else {
      var9.setVisibility(View.GONE);
      byte var3;
      if(FavoriteStatusHandler.favoriteContainsUnplayableSounds(var7)) {
        var3 = 0;
      } else {
        var3 = 8;
      }

      var10.setVisibility(var3);
    }

    Context var12 = RelaxMelodiesApp.getInstance().getApplicationContext();
    Glide.with(var12).load(Integer.valueOf(R.drawable.favorite_sound_stack_rope)).placeholder(R.drawable.favorite_sound_stack_rope).dontAnimate().into(var6);
    int var11;
    if(this.isStaffPickSelected(var7)) {
      var11 = this.convertStringToDrawableResourceId(this.activity, var7.getSelectedImageResourceEntryName());
      Glide.with(var12).load(Integer.valueOf(var11)).placeholder(var11).dontAnimate().into(var4);
      Glide.with(var12).load(Integer.valueOf(R.drawable.favorite_sound_stackselected)).placeholder(R.drawable.favorite_sound_stackselected).dontAnimate().into(var5);
      if(SoundManager.getInstance().isPaused()) {
        this.stopAnimationStaffFavorites(var2);
      } else {
        this.animateStaffFavorite(var2);
      }
    } else {
      var11 = this.convertStringToDrawableResourceId(this.activity, var7.getImageResourceEntryName());
      Glide.with(var12).load(Integer.valueOf(var11)).placeholder(var11).dontAnimate().into(var4);
      Glide.with(var12).load(Integer.valueOf(R.drawable.favorite_sound_stacknormal)).placeholder(R.drawable.favorite_sound_stacknormal).dontAnimate().into(var5);
      this.stopAnimationStaffFavorites(var2);
    }

    var2.setLayoutParams(this.adjustCellHorizontalSize(var1));
  }

  private void refreshCells() {
    for(int var2 = 0; var2 < this.getCount(); ++var2) {
      if(!this.cells.containsKey(Integer.valueOf(var2))) {
        ViewGroup var1 = (ViewGroup)((LayoutInflater)this.parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(this.cellResourceId, (ViewGroup)null, false);
        this.cells.put(Integer.valueOf(var2), var1);
        this.parent.addView(var1);
      }

      this.prepareCellView(var2, (ViewGroup)this.cells.get(Integer.valueOf(var2)));
    }

  }

  private void stopAnimationStaffFavorites(View var1) {
    ((FavoriteAnimatedButton)var1.findViewById(R.id.staff_picks_all_sounds_layout)).setAnimated(false);
  }

  public int calculateCellWidth() {
    float var1 = (float)this.activity.getResources().getInteger(R.integer.favorite_staff_pics_full_on_screen_cells);
    return (int)((float)this.activity.getResources().getDimensionPixelSize(R.dimen.favorite_staff_picks_width_with_rounded_corners) / (var1 + 0.8F));
  }

  public void clearSelection() {
    this.setSelectedStaffPick((StaffFavoriteSounds)null);
    this.notifyDataSetChanged();
  }

  public int getCount() {
    return this.staffFavorites.size();
  }

  public StaffFavoriteSounds getSelectedStaffPick() {
    return this.selectedStaffPick;
  }

  public void notifyDataSetChanged() {
    this.activity.runOnUiThread(new Runnable() {
      public void run() {
        StaffPicksScrollViewAdapter.this.refreshCells();
      }
    });
  }

  public void setSelectedStaffPick(StaffFavoriteSounds var1) {
    this.selectedStaffPick = var1;
    String var2;
    if(var1 != null) {
      var2 = var1.getIdentifier();
    } else {
      var2 = "";
    }

    PersistedDataManager.saveString("selectedStaffPick", var2, RelaxMelodiesApp.getInstance().getApplicationContext());
  }

  public void stopAnimatingSelectedCell() {
    if(this.selectedStaffPick != null && this.staffFavorites != null) {
      int var2 = this.staffFavorites.indexOf(this.selectedStaffPick);
      ViewGroup var1 = (ViewGroup)this.cells.get(Integer.valueOf(var2));
      if(var1 != null) {
        this.stopAnimationStaffFavorites(var1);
      }
    }

  }

  private class LoadStaffFavoritesTask extends AsyncTask<Activity, Void, Void> {
    private LoadStaffFavoritesTask() {
    }

    private StaffFavoriteSounds convertDTOToStaffFavorite(Activity var1, StaffFavoriteDTO var2) {
      StaffFavoriteSounds var3 = new StaffFavoriteSounds(var2.getSoundSelection(), this.convertStringResourceToString(var1, var2.getLabelResourceId()), var2.getImageResourceEntryName(), var2.getSelectedImageResourceEntryName());
      var3.setIdentifier(var2.getLabelResourceId());
      return var3;
    }

    private String convertStringResourceToString(Activity var1, String var2) {
      int var3 = var1.getResources().getIdentifier(var2, "string", var1.getPackageName());
      return var1.getResources().getString(var3);
    }

    private void loadStaffFavorites(Activity var1) {
      BufferedReader var2 = new BufferedReader(new InputStreamReader(var1.getResources().openRawResource(R.raw.staff_favorites)));
      Iterator var4 = ((List)(new Gson()).fromJson(var2, (new TypeToken<List<StaffFavoriteDTO>>() {
      }).getType())).iterator();

      while(var4.hasNext()) {
        StaffFavoriteDTO var3 = (StaffFavoriteDTO)var4.next();
        StaffPicksScrollViewAdapter.this.staffFavorites.add(this.convertDTOToStaffFavorite(var1, var3));
      }

    }

    protected Void doInBackground(Activity... var1) {
      if(var1.length > 0) {
        Activity var2 = var1[0];
        StaffPicksScrollViewAdapter.this.staffFavorites.clear();
        this.loadStaffFavorites(var2);
      }

      return null;
    }

    protected void onPostExecute(Void var1) {
      StaffPicksScrollViewAdapter.this.notifyDataSetChanged();
    }
  }
}
