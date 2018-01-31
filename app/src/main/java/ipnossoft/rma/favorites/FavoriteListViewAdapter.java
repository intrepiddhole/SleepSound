package ipnossoft.rma.favorites;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.LayoutParams;
import android.support.v7.widget.RecyclerView.OnChildAttachStateChangeListener;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.View.OnLongClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.ipnossoft.api.soundlibrary.Sound;
import com.ipnossoft.api.soundlibrary.SoundLibrary;
import com.ipnossoft.api.soundlibrary.sounds.BinauralSound;
import com.ipnossoft.api.soundlibrary.sounds.GuidedMeditationSound;
import com.ipnossoft.api.soundlibrary.sounds.IsochronicSound;
import ipnossoft.rma.PersistedDataManager;
import ipnossoft.rma.RelaxMelodiesApp;
import ipnossoft.rma.free.R;
import ipnossoft.rma.media.SoundManager;
import ipnossoft.rma.media.SoundTrackInfo;
import ipnossoft.rma.ui.button.FavoriteAnimatedButton;
import ipnossoft.rma.ui.scrollview.StaffPicksHorizontalScrollView;
import java.util.Iterator;
import java.util.List;

public class FavoriteListViewAdapter extends Adapter<ViewHolder> {
  private static final String ASTRAL_SOUND_ID = "ipnossoft.rma.sounds.sound21";
  public static final int EMPTY_FAVORITES_CELL_VIEW_TYPE = 3;
  public static final int FAVORITE_CELL_VIEW_TYPE = 2;
  public static final int HEADER_CELL_VIEW_TYPE = 0;
  public static final String PREF_SELECTED_FAVORITE = "selectedFavorite";
  public static final int STAFF_PICK_CELL_VIEW_TYPE = 1;
  private static final String WHITE_NOISE_SOUND_ID = "ipnossoft.rma.sounds.sound19";
  private Activity activity;
  private OnCreateContextMenuListener contextMenuListener;
  private List<FavoriteSounds> favorites;
  private boolean hasStaffPicksLoadedVisuallyOnce = false;
  private Animation infiniteSwing;
  private int position;
  private RecyclerView recyclerView;
  private int resourceId;
  private FavoriteSounds selectedFavorite;
  private StaffPicksScrollViewAdapter staffPicksAdapter;
  private FavoriteListViewAdapter.ViewHolderOriginalPadding viewHolderOriginalPadding;

  public FavoriteListViewAdapter(Activity var1, int var2, List<FavoriteSounds> var3, OnCreateContextMenuListener var4) {
    this.activity = var1;
    this.resourceId = var2;
    this.favorites = var3;
    this.contextMenuListener = var4;
    this.infiniteSwing = AnimationUtils.loadAnimation(var1.getApplicationContext(), R.anim.rotate_sound_button_2);
    this.infiniteSwing.setRepeatCount(-1);
  }

  private void addTopMarginToHeader(FavoriteListViewAdapter.HeaderViewHolder var1, int var2) {
    LayoutParams var3 = (LayoutParams)var1.titleRelativeLayout.getLayoutParams();
    var3.setMargins(0, var2, 0, 0);
    var1.titleRelativeLayout.setLayoutParams(var3);
  }

  private void animateCell(ViewHolder var1) {
    if(var1.getItemViewType() == 2) {
      FavoriteListViewAdapter.ItemViewHolder var4 = (FavoriteListViewAdapter.ItemViewHolder)var1;
      ((FavoriteAnimatedButton)var4.favoriteListItemSoundStack).setAnimated(true);
      int var2 = (int)this.activity.getResources().getDimension(R.dimen.sound_image_height);
      var4.favoriteListItemSoundStack.setPivotY((float)var2 * 0.17447917F);
      int var3 = var4.favoriteListItemSoundStack.getWidth();
      var2 = var3;
      if(var3 == 0) {
        var2 = (int)this.activity.getResources().getDimension(R.dimen.sound_image_width);
      }

      var4.favoriteListItemSoundStack.setPivotX((float)(var2 / 2));
    }

  }

  private int convertStringToDrawableResourceId(Activity var1, String var2) {
    return var1.getResources().getIdentifier(var2, "drawable", var1.getPackageName());
  }

  private ViewHolder createEmptyFavoritesCellViewHolder(ViewGroup var1) {
    return new FavoriteListViewAdapter.NoFavoritesCellViewHolder(LayoutInflater.from(var1.getContext()).inflate(R.layout.favorite_list_view_no_favorites_cell, var1, false));
  }

  private ViewHolder createFavoriteCellViewHolder(ViewGroup var1) {
    View var2 = LayoutInflater.from(var1.getContext()).inflate(this.resourceId, var1, false);
    this.viewHolderOriginalPadding = new FavoriteListViewAdapter.ViewHolderOriginalPadding((RelativeLayout)var2.findViewById(R.id.favorite_list_item_background_overlay));
    return new FavoriteListViewAdapter.ItemViewHolder(var2, this.contextMenuListener);
  }

  private ViewHolder createHeaderViewHolder(ViewGroup var1) {
    return new FavoriteListViewAdapter.HeaderViewHolder(LayoutInflater.from(var1.getContext()).inflate(R.layout.favorite_list_header, var1, false));
  }

  private ViewHolder createStaffPickCellViewHolder(ViewGroup var1) {
    View var3 = LayoutInflater.from(var1.getContext()).inflate(R.layout.favorite_list_staff_pick_cell, var1, false);
    StaffPicksHorizontalScrollView var2 = (StaffPicksHorizontalScrollView)var3.findViewById(R.id.staff_pick_horizontal_scroll_view);
    this.staffPicksAdapter = new StaffPicksScrollViewAdapter(this.activity, var2, R.layout.favorite_list_staff_pick_horizontal_inner_cell);
    var2.configure(var3, this.staffPicksAdapter);
    return new FavoriteListViewAdapter.StaffPickViewHolder(var3, var2);
  }

  @Nullable
  private SoundTrackInfo extractMainFavoriteSound(FavoriteSounds var1) {
    List var2 = var1.getTrackInfos();
    SoundTrackInfo var5 = null;
    Iterator var3 = var2.iterator();

    while(true) {
      Sound var4;
      SoundTrackInfo var6;
      do {
        if(!var3.hasNext()) {
          return var5;
        }

        var6 = (SoundTrackInfo)var3.next();
        var4 = (Sound)SoundLibrary.getInstance().getSound(var6.getSoundId());
      } while(var5 != null && (var6.getVolume() <= var5.getVolume() || var4 instanceof GuidedMeditationSound));

      var5 = var6;
    }
  }

  private int getAdjustedAdapterPosition(int var1) {
    return this.getNumberOfHeaders() + var1 + this.getNumberOfStaffPickCells();
  }

  private int getAdjustedFavoritesPosition(int var1) {
    return var1 - this.getNumberOfHeaders() - this.getNumberOfStaffPickCells();
  }

  private int getImageResourceForBrainwave(boolean var1) {
    Sound var2 = (Sound)SoundLibrary.getInstance().getSound("ipnossoft.rma.sounds.sound19");
    return var1?var2.getSelectedImageResourceId():var2.getNormalImageResourceId();
  }

  private int getImageResourceForMainSound(SoundTrackInfo var1, boolean var2) {
    if(var1 != null) {
      Sound var3 = (Sound)SoundLibrary.getInstance().getSound(var1.getSoundId());
      if(var3 instanceof BinauralSound || var3 instanceof IsochronicSound) {
        return this.getImageResourceForBrainwave(var2);
      }

      if(var3 instanceof GuidedMeditationSound) {
        return this.getImageResourceForMeditation(var2);
      }

      if(var3 != null) {
        if(var2) {
          return var3.getSelectedImageResourceId();
        }

        return var3.getNormalImageResourceId();
      }
    }

    return -1;
  }

  private int getImageResourceForMeditation(boolean var1) {
    Sound var2 = (Sound)SoundLibrary.getInstance().getSound("ipnossoft.rma.sounds.sound21");
    return var1?var2.getSelectedImageResourceId():var2.getNormalImageResourceId();
  }

  private int getNormalImageResource(FavoriteListViewAdapter.ItemViewHolder var1) {
    if(var1.favorite.getImageResourceEntryName() != null) {
      return this.convertStringToDrawableResourceId(this.activity, var1.favorite.getImageResourceEntryName());
    } else {
      if(var1.mainSound == null) {
        var1.mainSound = this.extractMainFavoriteSound(var1.favorite);
      }

      return this.getImageResourceForMainSound(var1.mainSound, false);
    }
  }

  private int getNumberOfFooters() {
    return 1;
  }

  private int getNumberOfHeaders() {
    return 2;
  }

  private int getNumberOfStaffPickCells() {
    return 1;
  }

  private int getSelectedImageResource(FavoriteListViewAdapter.ItemViewHolder var1) {
    if(var1.favorite.getSelectedImageResourceEntryName() != null) {
      return this.convertStringToDrawableResourceId(this.activity, var1.favorite.getSelectedImageResourceEntryName());
    } else {
      if(var1.mainSound == null) {
        var1.mainSound = this.extractMainFavoriteSound(var1.favorite);
      }

      return this.getImageResourceForMainSound(var1.mainSound, true);
    }
  }

  private boolean isPositionOfFooterView(int var1) {
    return var1 == this.getItemCount() - 1;
  }

  private boolean isSelected(FavoriteSounds var1) {
    long var2 = PersistedDataManager.getLong("selectedFavorite", -1L, RelaxMelodiesApp.getInstance().getApplicationContext());
    return var1.getId() == var2;
  }

  private boolean isValidFavoriteIndex(int var1) {
    return var1 >= 0 && var1 < this.favorites.size();
  }

  private void onBindStaffPickCell(final FavoriteListViewAdapter.StaffPickViewHolder var1) {
    if(!this.hasStaffPicksLoadedVisuallyOnce) {
      var1.itemView.animate().alpha(1.0F).setDuration(500L).setListener(new AnimatorListener() {
        public void onAnimationCancel(Animator var1x) {
        }

        public void onAnimationEnd(Animator var1x) {
          FavoriteListViewAdapter.this.hasStaffPicksLoadedVisuallyOnce = true;
          var1.itemView.setAlpha(1.0F);
        }

        public void onAnimationRepeat(Animator var1x) {
        }

        public void onAnimationStart(Animator var1x) {
        }
      });
    } else {
      this.staffPicksAdapter.notifyDataSetChanged();
    }
  }

  private void populateFavoriteCellViewHolder(FavoriteListViewAdapter.ItemViewHolder var1, int var2) {
    if(!this.isPositionOfFooterView(var2)) {
      var1.itemView.setEnabled(true);
      var1.itemView.setVisibility(View.VISIBLE);
      this.resetViewHolderPadding(var1);
      this.updateViewHolderContents(var1);
      this.setHolderClickListeners(var1);
    } else {
      this.populateFooterView(var1);
    }
  }

  private void populateFooterView(FavoriteListViewAdapter.ItemViewHolder var1) {
    var1.itemView.setEnabled(false);
    this.setAllInnerHolderViewsVisibility(var1, 8);
    var1.backgroundOverlayLayout.setPadding(0, 0, 0, (int)this.activity.getResources().getDimension(R.dimen.favorite_fragment_list_view_padding_bottom));
  }

  private void populateHeaderCellViewHolder(FavoriteListViewAdapter.HeaderViewHolder var1, int var2) {
    if(var2 == 0) {
      var1.titleTextView.setText(this.activity.getResources().getString(R.string.favorites_staff_pick));
      this.addTopMarginToHeader(var1, 0);
    } else if(var2 == 2) {
      var1.titleTextView.setText(this.activity.getResources().getString(R.string.favorites_my_favorites));
      return;
    }

  }

  private void refreshCellSelectionStatus(ViewHolder var1) {
    if(var1 instanceof FavoriteListViewAdapter.ItemViewHolder) {
      int var2 = var1.getAdapterPosition();
      if(!this.isPositionOfFooterView(var2)) {
        this.refreshCellStatus((FavoriteSounds)this.favorites.get(this.getAdjustedFavoritesPosition(var2)), var1);
        this.resetViewHolderPadding((FavoriteListViewAdapter.ItemViewHolder)var1);
      } else if(var1.itemView.isSelected()) {
        this.unSelectCell(var1);
        return;
      }
    }

  }

  private void refreshCellStatus(FavoriteSounds var1, ViewHolder var2) {
    if(this.isSelected(var1)) {
      this.setCellAsSelected(var1, var2);
    } else {
      this.setCellAsNonSelected(var1, var2);
    }
  }

  private void resetViewHolderPadding(FavoriteListViewAdapter.ItemViewHolder var1) {
    var1.backgroundOverlayLayout.setPadding(this.viewHolderOriginalPadding.left, this.viewHolderOriginalPadding.top, this.viewHolderOriginalPadding.right, this.viewHolderOriginalPadding.bottom);
  }

  private void selectCell(ViewHolder var1) {
    if(var1.getItemViewType() == 2) {
      FavoriteListViewAdapter.ItemViewHolder var2 = (FavoriteListViewAdapter.ItemViewHolder)var1;
      var2.soundStackImage.setImageResource(R.drawable.favorite_sound_stackselected);
      int var3 = this.getSelectedImageResource(var2);
      if(var3 != -1) {
        var2.mainsSoundImage.setImageResource(var3);
      }
    }

    var1.itemView.setSelected(true);
    if(!SoundManager.getInstance().isPaused()) {
      this.animateCell(var1);
    } else {
      this.stopAnimatingCell(var1);
    }
  }

  private void selectCellAtIndex(int var1) {
    ViewHolder var2 = this.recyclerView.findViewHolderForAdapterPosition(var1);
    if(var2 != null) {
      this.selectCell(var2);
    }
  }

  private void selectSpecificFavoriteAndCell(long var1) {
    if(this.recyclerView != null) {
      if(var1 == -1L) {
        if(this.selectedFavorite == null) {
          return;
        }

        this.setSelectedFavorite((FavoriteSounds)null);
      }

      for(int var3 = 0; var3 < this.favorites.size(); ++var3) {
        if(((FavoriteSounds)this.favorites.get(var3)).getId() == var1) {
          this.selectCellAtIndex(this.getAdjustedAdapterPosition(var3));
          this.setClickedFavoriteIndex(var3);
        } else {
          this.unSelectCellAtIndex(this.getAdjustedAdapterPosition(var3));
        }
      }
    }

  }

  private void setAllInnerHolderViewsVisibility(FavoriteListViewAdapter.ItemViewHolder var1, int var2) {
    var1.favoriteNameTextView.setVisibility(var2);
    var1.optionButton.setVisibility(var2);
    var1.favoritesSounds.setVisibility(var2);
  }

  private void setCellAsNonSelected(FavoriteSounds var1, ViewHolder var2) {
    if(this.selectedFavorite != null && this.selectedFavorite.getId() == var1.getId()) {
      this.setSelectedFavorite((FavoriteSounds)null);
    }

    this.unSelectCell(var2);
  }

  private void setCellAsSelected(FavoriteSounds var1, ViewHolder var2) {
    this.selectCell(var2);
    this.setSelectedFavorite(var1);
  }

  private void setHolderClickListeners(final FavoriteListViewAdapter.ItemViewHolder var1) {
    var1.itemView.setOnClickListener(new FavoriteListViewAdapterOnItemClickListener((FavoriteSounds)this.favorites.get(this.getAdjustedFavoritesPosition(var1.getAdapterPosition())), var1.getAdapterPosition(), this.activity, this));
    OnLongClickListener var2 = new OnLongClickListener() {
      public boolean onLongClick(View var1x) {
        FavoriteListViewAdapter.this.setPosition(var1.getAdapterPosition());
        return false;
      }
    };
    var1.itemView.setOnLongClickListener(var2);
    var1.optionButton.setOnClickListener(new OnClickListener() {
      public void onClick(View var1x) {
        FavoriteListViewAdapter.this.setPosition(var1.getAdapterPosition());
        FavoriteListViewAdapter.this.contextMenuListener.onCreateContextMenu((ContextMenu)null, (View)null, (ContextMenuInfo)null);
      }
    });
  }

  private void setPosition(int var1) {
    this.position = var1;
  }

  private void setSelectedFavorite(FavoriteSounds var1) {
    this.selectedFavorite = var1;
    if(this.staffPicksAdapter != null) {
      this.staffPicksAdapter.clearSelection();
    }

    long var2;
    if(var1 != null) {
      var2 = var1.getId();
    } else {
      var2 = -1L;
    }

    PersistedDataManager.saveLong("selectedFavorite", var2, RelaxMelodiesApp.getInstance().getApplicationContext());
  }

  private void stopAnimatingCell(ViewHolder var1) {
    if(var1.getItemViewType() == 2) {
      ((FavoriteAnimatedButton)((FavoriteListViewAdapter.ItemViewHolder)var1).favoriteListItemSoundStack).setAnimated(false);
    } else if(this.getSelectedStaffPick() != null) {
      this.staffPicksAdapter.stopAnimatingSelectedCell();
      return;
    }

  }

  private void unSelectCell(final ViewHolder var1) {
    this.activity.runOnUiThread(new Runnable() {
      public void run() {
        if(var1.getItemViewType() == 2) {
          FavoriteListViewAdapter.ItemViewHolder var2 = (FavoriteListViewAdapter.ItemViewHolder)var1;
          var2.soundStackImage.setImageResource(R.drawable.favorite_sound_stacknormal);
          int var1x = FavoriteListViewAdapter.this.getNormalImageResource(var2);
          if(var1x != -1) {
            var2.mainsSoundImage.setImageResource(var1x);
          }
        }

        var1.itemView.setSelected(false);
        FavoriteListViewAdapter.this.stopAnimatingCell(var1);
      }
    });
  }

  private void unSelectCellAtIndex(int var1) {
    ViewHolder var2 = this.recyclerView.findViewHolderForAdapterPosition(var1);
    if(var2 != null) {
      this.unSelectCell(var2);
    }
  }

  private void updateViewHolderContents(FavoriteListViewAdapter.ItemViewHolder var1) {
    FavoriteSounds var2 = (FavoriteSounds)this.favorites.get(this.getAdjustedFavoritesPosition(var1.getAdapterPosition()));
    var1.favoriteNameTextView.setText(var2.getLabel());
    this.setAllInnerHolderViewsVisibility(var1, 0);
    var1.favorite = var2;
    var1.mainSound = null;
    int var3 = this.getNormalImageResource(var1);
    if(var3 != -1) {
      var1.mainsSoundImage.setImageResource(var3);
    }

  }

  public void clearSelection() {
    this.selectSpecificFavoriteAndCell(-1L);
    if(this.staffPicksAdapter != null) {
      this.staffPicksAdapter.clearSelection();
    }

  }

  public void deSelectFirstCell() {
    this.unSelectCellAtIndex(0);
  }

  public FavoriteSounds getFavorite(int var1) {
    return (FavoriteSounds)this.favorites.get(this.getAdjustedFavoritesPosition(var1));
  }

  public int getItemCount() {
    int var2 = this.getNumberOfFooters();
    int var3 = this.getNumberOfHeaders();
    int var4 = this.getNumberOfStaffPickCells();
    int var1;
    if(this.favorites.size() == 0) {
      var1 = 1;
    } else {
      var1 = this.favorites.size();
    }

    return var2 + var3 + var4 + var1;
  }

  public int getItemViewType(int var1) {
    byte var2 = 1;
    if(var1 != 0 && var1 != 2) {
      if(var1 != 1) {
        if(var1 == 3 && this.favorites.size() == 0) {
          return 3;
        }

        return 2;
      }
    } else {
      var2 = 0;
    }

    return var2;
  }

  public int getPosition() {
    return this.position;
  }

  public FavoriteSounds getSelectedFavorite() {
    return this.selectedFavorite;
  }

  public StaffFavoriteSounds getSelectedStaffPick() {
    return this.staffPicksAdapter != null?this.staffPicksAdapter.getSelectedStaffPick():null;
  }

  public boolean isCellSelectedAtIndex(int var1) {
    View var2 = this.recyclerView.getLayoutManager().findViewByPosition(var1);
    return var2 != null && var2.isSelected();
  }

  public void onAttachedToRecyclerView(final RecyclerView var1) {
    super.onAttachedToRecyclerView(var1);
    this.recyclerView = var1;
    RecyclerView var1_1 = this.recyclerView;
    this.recyclerView.addOnChildAttachStateChangeListener(new OnChildAttachStateChangeListener() {
      public void onChildViewAttachedToWindow(View var1x) {
        FavoriteListViewAdapter.this.refreshCellSelectionStatus(var1.getChildViewHolder(var1x));
      }

      public void onChildViewDetachedFromWindow(View var1x) {
      }
    });
  }

  public void onBindViewHolder(ViewHolder var1, int var2) {
    switch(this.getItemViewType(var2)) {
      case 0:
        this.populateHeaderCellViewHolder((FavoriteListViewAdapter.HeaderViewHolder)var1, var2);
        return;
      case 1:
        this.onBindStaffPickCell((FavoriteListViewAdapter.StaffPickViewHolder)var1);
        return;
      case 2:
        this.populateFavoriteCellViewHolder((FavoriteListViewAdapter.ItemViewHolder)var1, var2);
        return;
      default:
    }
  }

  public ViewHolder onCreateViewHolder(ViewGroup var1, int var2) {
    switch(var2) {
      case 0:
        return this.createHeaderViewHolder(var1);
      case 1:
        return this.createStaffPickCellViewHolder(var1);
      case 2:
        return this.createFavoriteCellViewHolder(var1);
      case 3:
        return this.createEmptyFavoritesCellViewHolder(var1);
      default:
        throw new RuntimeException("Attempting to create non-existing view holder type in favorite list.");
    }
  }

  public void onFavoriteCellClicked(long var1) {
    this.selectSpecificFavoriteAndCell(var1);
  }

  public void removePlayingFavorite() {
    this.setSelectedFavorite((FavoriteSounds)null);
  }

  public void setClickedFavoriteIndex(int var1) {
    if(this.isValidFavoriteIndex(var1)) {
      this.setSelectedFavorite((FavoriteSounds)this.favorites.get(var1));
    }

  }

  public void stopAnimatingSelectedCell() {
    int var1 = this.getAdjustedAdapterPosition(this.favorites.indexOf(this.selectedFavorite));
    ViewHolder var2 = this.recyclerView.findViewHolderForAdapterPosition(var1);
    if(var2 != null) {
      this.stopAnimatingCell(var2);
    }
  }

  private static class HeaderViewHolder extends ViewHolder {
    RelativeLayout titleRelativeLayout;
    TextView titleTextView;

    public HeaderViewHolder(View var1) {
      super(var1);
      this.titleRelativeLayout = (RelativeLayout)var1.findViewById(R.id.favorite_list_header_row);
      this.titleTextView = (TextView)var1.findViewById(R.id.favorite_list_header_title);
    }
  }

  public class ItemViewHolder extends ViewHolder {
    RelativeLayout backgroundOverlayLayout;
    FavoriteSounds favorite;
    RelativeLayout favoriteListItemSoundStack;
    TextView favoriteNameTextView;
    RelativeLayout favoritesSounds;
    SoundTrackInfo mainSound;
    ImageView mainsSoundImage;
    RelativeLayout optionButton;
    ImageView soundStackImage;

    public ItemViewHolder(View var2, OnCreateContextMenuListener var3) {
      super(var2);
      this.favoriteNameTextView = (TextView)var2.findViewById(R.id.favorite_list_item_name);
      this.mainsSoundImage = (ImageView)var2.findViewById(R.id.favorite_list_item_main_sound);
      this.favoriteListItemSoundStack = (RelativeLayout)var2.findViewById(R.id.favorite_list_item_sound_stack);
      this.soundStackImage = (ImageView)var2.findViewById(R.id.favorite_list_item_sound_stack_underlay);
      this.backgroundOverlayLayout = (RelativeLayout)var2.findViewById(R.id.favorite_list_item_background_overlay);
      this.favoritesSounds = (RelativeLayout)var2.findViewById(R.id.favorite_list_item_sounds);
      this.optionButton = (RelativeLayout)var2.findViewById(R.id.favorite_liste_item_option_button);
      this.mainSound = null;
      var2.setOnCreateContextMenuListener(var3);
    }
  }

  public class NoFavoritesCellViewHolder extends ViewHolder {
    TextView subTitle;
    TextView title;

    public NoFavoritesCellViewHolder(View var2) {
      super(var2);
      this.title = (TextView)var2.findViewById(R.id.favorite_list_no_favorites_cell_title);
      this.subTitle = (TextView)var2.findViewById(R.id.favorite_list_no_favorites_cell_sub_title);
    }
  }

  private static class StaffPickViewHolder extends ViewHolder {
    HorizontalScrollView staffPickRecyclerView;

    public StaffPickViewHolder(View var1, HorizontalScrollView var2) {
      super(var1);
      this.staffPickRecyclerView = var2;
    }
  }

  private class ViewHolderOriginalPadding {
    int bottom;
    int left;
    int right;
    int top;

    public ViewHolderOriginalPadding(RelativeLayout var2) {
      this.left = var2.getPaddingLeft();
      this.right = var2.getPaddingRight();
      this.top = var2.getPaddingTop();
      this.bottom = var2.getPaddingBottom();
    }
  }
}
