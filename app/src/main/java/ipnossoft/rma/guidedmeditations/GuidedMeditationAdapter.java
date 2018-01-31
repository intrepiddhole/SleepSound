package ipnossoft.rma.guidedmeditations;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AbsListView.LayoutParams;
import com.ipnossoft.api.soundlibrary.Sound;
import com.ipnossoft.api.soundlibrary.sounds.GuidedMeditationSound;
import ipnossoft.rma.RelaxMelodiesApp;
import ipnossoft.rma.free.R;
import ipnossoft.rma.media.SoundManager;
import ipnossoft.rma.util.Utils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GuidedMeditationAdapter extends ArrayAdapter<GuidedMeditationSound> {
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;
    private Map<Integer, String> categoryPositions;
    private List<GuidedMeditationSound> guidedMeditationSounds = null;
    private boolean isVisible = true;
    private int resourceId;

    public GuidedMeditationAdapter(Activity var1, int var2, List<GuidedMeditationSound> var3) {
        super(var1, var2, var3);
        this.guidedMeditationSounds = var3;
        this.resourceId = var2;
        this.categoryPositions = this.loadCategoryPositions();
    }

    private void addHeightAndTopPaddingToHeader(View var1, int var2, float var3) {
        LayoutParams var4 = new LayoutParams(-1, (int)((float)var2 + var3));
        var1.setPadding(var1.getPaddingLeft(), var2, var1.getPaddingRight(), var1.getPaddingBottom());
        var1.setLayoutParams(var4);
    }

    @NonNull
    private GuidedMeditationAdapter.ItemViewHolder findAllViewsInViewHolder(View var1) {
        GuidedMeditationAdapter.ItemViewHolder var2 = new GuidedMeditationAdapter.ItemViewHolder();
        var2.itemView = var1;
        var2.guidedMeditationNameTextView = (TextView)var1.findViewById(R.id.guided_meditation_list_view_name);
        var2.guidedMeditationDescriptionTextView = (TextView)var1.findViewById(R.id.guided_meditation_list_view_description);
        var2.guidedMeditationDurationTextView = (TextView)var1.findViewById(R.id.guided_meditation_list_view_duration);
        var2.bulletPointImageView = (ImageView)var1.findViewById(R.id.guided_meditation_bullet_point_view);
        var2.guidedMeditationProBadge = (TextView)var1.findViewById(R.id.guided_meditation_pro_badge);
        var2.buttonLayout = (RelativeLayout)var1.findViewById(R.id.guided_meditation_button_layout);
        var2.specialBackgroundView = var1.findViewById(R.id.meditation_special_background);
        var2.newGuidedMeditationTextView = (TextView)var1.findViewById(R.id.new_guided_meditation_text_view);
        var2.buttonLayout.setEnabled(false);
        return var2;
    }

    private int getFooterCellCount() {
        return 1;
    }

    private GuidedMeditationAdapter.MeditationState getMeditationState(GuidedMeditationSound var1) {
        return var1.isLockedWithPremiumEnabled(RelaxMelodiesApp.isPremium().booleanValue()) && !GuidedMeditationStatus.getInstance().didSeeFreeMeditation(var1)?GuidedMeditationAdapter.MeditationState.LOCKED:(!var1.isPlayable()?GuidedMeditationAdapter.MeditationState.DOWNLOADABLE:(SoundManager.getInstance().isPlaying(var1.getId())?GuidedMeditationAdapter.MeditationState.PLAYING:(SoundManager.getInstance().isSelected(var1.getId())?GuidedMeditationAdapter.MeditationState.SELECTED:GuidedMeditationAdapter.MeditationState.NORMAL)));
    }

    private boolean isLanguageHeaderViewPresent() {
        return !Utils.getCurrentLanguageLocale(RelaxMelodiesApp.getInstance().getApplicationContext()).equals(Locale.ENGLISH.getLanguage());
    }

    private boolean isPositionOfFooterView(int var1) {
        return var1 == this.getCount() - 1;
    }

    private int listIndexToMeditationIndex(int var1) {
        int var3 = var1;
        Iterator var2 = this.categoryPositions.keySet().iterator();

        while(var2.hasNext()) {
            if(((Integer)var2.next()).intValue() < var1) {
                --var3;
            }
        }

        return var3;
    }

    private Map<Integer, String> loadCategoryPositions() {
        HashMap var3 = new HashMap();
        String var1 = null;

        String var2;
        for(int var5 = 0; var5 < this.guidedMeditationSounds.size(); var1 = var2) {
            GuidedMeditationSound var4 = (GuidedMeditationSound)this.guidedMeditationSounds.get(var5);
            var2 = var1;
            if(var4.getTagId() != null) {
                var2 = var1;
                if(!var4.getTagId().equals(var1)) {
                    var2 = var4.getTagId();
                    var3.put(Integer.valueOf(var3.size() + var5), var2);
                }
            }

            ++var5;
        }

        return var3;
    }

    private void populateFooterView(GuidedMeditationAdapter.ItemViewHolder var1) {
        var1.itemView.setEnabled(false);
        this.setAllInnerHolderViewsVisibility(var1, 8);
        var1.itemView.setPadding(0, 0, 0, (int)this.getContext().getResources().getDimension(R.dimen.fragment_list_view_padding_bottom));
    }

    private void resetViewHolderPadding(GuidedMeditationAdapter.ItemViewHolder var1) {
        var1.itemView.setPadding(0, 0, 0, 0);
    }

    private void setAllInnerHolderViewsVisibility(GuidedMeditationAdapter.ItemViewHolder var1, int var2) {
        var1.guidedMeditationDescriptionTextView.setVisibility(var2);
        var1.guidedMeditationNameTextView.setVisibility(var2);
        var1.guidedMeditationDurationTextView.setVisibility(var2);
        var1.bulletPointImageView.setVisibility(var2);
        var1.guidedMeditationProBadge.setVisibility(var2);
        var1.specialBackgroundView.setVisibility(var2);
        var1.buttonLayout.setVisibility(var2);
    }

    private void setFeaturedGuidedMeditationSpecialLayout(GuidedMeditationSound var1, GuidedMeditationAdapter.ItemViewHolder var2, int var3) {
        if(GuidedMeditationStatus.getInstance().isMeditationFeatured(var1) && var3 < 2) {
            var2.specialBackgroundView.setVisibility(View.VISIBLE);
            var3 = this.getContext().getResources().getDimensionPixelSize(R.dimen.feature_guided_meditation_top_padding);
            var2.itemView.setPadding(0, var3, 0, 0);
        } else {
            var2.itemView.setLayoutParams(new LayoutParams(-1, -1));
            var2.specialBackgroundView.setVisibility(View.GONE);
            var2.newGuidedMeditationTextView.setVisibility(View.GONE);
            if(GuidedMeditationStatus.getInstance().isMeditationNew(var1)) {
                var2.newGuidedMeditationTextView.setVisibility(View.GONE);
            }

            var2.itemView.setPadding(0, 0, 0, 0);
        }
    }

    private void setGuidedMeditationButtonState(GuidedMeditationAdapter.ItemViewHolder var1, GuidedMeditationAdapter.MeditationState var2, GuidedMeditationSound var3, View var4) {
        if(var2.getValue() >= GuidedMeditationAdapter.MeditationState.SELECTED.getValue()) {
            var4.setBackgroundColor(this.getContext().getResources().getColor(R.color.fragment_list_selected_cell_color));
        } else {
            var4.setBackgroundColor(0);
        }

        if(var2 == GuidedMeditationAdapter.MeditationState.DOWNLOADABLE) {
            var1.bulletPointImageView.setImageResource(R.drawable.icon_meditation_download);
        } else {
            var1.bulletPointImageView.setImageResource(R.drawable.icon_meditation_play);
        }

        if(GuidedMeditationStatus.getInstance().didListenToMeditation(var3)) {
            String var5 = this.getContext().getString(R.string.guided_meditation_listened);
            var1.guidedMeditationDurationTextView.setText(var1.guidedMeditationDurationTextView.getText() + " - " + var5);
        }

    }

    private void setGuidedMeditationDescriptionView(GuidedMeditationAdapter.ItemViewHolder var1, GuidedMeditationSound var2) {
        var1.guidedMeditationDescriptionTextView.setText(var2.getShortDescription());
    }

    private void setGuidedMeditationDurationView(GuidedMeditationAdapter.ItemViewHolder var1, GuidedMeditationSound var2) {
        SimpleDateFormat var4 = new SimpleDateFormat("HH:mm:ss");
        Date var3 = new Date();

        Date var6;
        try {
            var6 = var4.parse(var2.getAudioDuration());
        } catch (ParseException var5) {
            var5.printStackTrace();
            var6 = var3;
        }

        SimpleDateFormat var7 = new SimpleDateFormat("m:ss");
        var1.guidedMeditationDurationTextView.setText(var7.format(var6));
    }

    private void setGuidedMeditationNameView(GuidedMeditationAdapter.ItemViewHolder var1, GuidedMeditationSound var2) {
        var1.guidedMeditationNameTextView.setText(var2.getName());
    }

    private void setGuidedMeditationOverlayViews(GuidedMeditationAdapter.ItemViewHolder var1, GuidedMeditationAdapter.MeditationState var2) {
        var1.guidedMeditationProBadge.setVisibility(View.GONE);
        var1.bulletPointImageView.setVisibility(View.VISIBLE);
        if(var2 == GuidedMeditationAdapter.MeditationState.LOCKED) {
            var1.guidedMeditationProBadge.setVisibility(View.VISIBLE);
            var1.bulletPointImageView.setVisibility(View.INVISIBLE);
        }

    }

    private void setupHeaderViewForTag(int var1, Sound var2, GuidedMeditationAdapter.HeaderViewHolder var3) {
        View var4 = var3.cellView;
        if(var1 == 0) {
            this.setupTopHeader(var4);
        } else {
            this.addHeightAndTopPaddingToHeader(var4, 0, this.getContext().getResources().getDimension(R.dimen.fragment_list_view_header_height));
        }

        var3.guidedMeditationCategoryTextView.setText(var2.getTag());
    }

    private void setupTopHeader(View var1) {
        float var4 = this.getContext().getResources().getDimension(R.dimen.guided_meditation_fragment_list_view_divider_height);
        float var3 = this.getContext().getResources().getDimension(R.dimen.fragment_list_view_header_height) + var4;
        float var2 = var3;
        if(this.isLanguageHeaderViewPresent()) {
            var2 = var3 - var4;
        }

        this.addHeightAndTopPaddingToHeader(var1, 0, var2);
    }

    private void setupViewForGuidedMeditation(GuidedMeditationAdapter.ItemViewHolder var1, GuidedMeditationSound var2, View var3, int var4) {
        var1.itemView.setEnabled(true);
        var1.itemView.setVisibility(View.VISIBLE);
        this.resetViewHolderPadding(var1);
        this.setAllInnerHolderViewsVisibility(var1, 0);
        this.setGuidedMeditationNameView(var1, var2);
        this.setGuidedMeditationDescriptionView(var1, var2);
        this.setGuidedMeditationDurationView(var1, var2);
        GuidedMeditationAdapter.MeditationState var5 = this.getMeditationState(var2);
        this.setGuidedMeditationOverlayViews(var1, var5);
        this.setGuidedMeditationButtonState(var1, var5, var2, var3);
        this.setFeaturedGuidedMeditationSpecialLayout(var2, var1, var4);
    }

    public int getCount() {
        return this.guidedMeditationSounds.size() + this.categoryPositions.size() + this.getFooterCellCount();
    }

    public GuidedMeditationSound getItem(int var1) {
        return (GuidedMeditationSound)this.guidedMeditationSounds.get((int)this.getItemId(var1));
    }

    public long getItemId(int var1) {
        return (long)this.listIndexToMeditationIndex(var1);
    }

    public int getItemViewType(int var1) {
        return this.categoryPositions.keySet().contains(Integer.valueOf(var1))?1:0;
    }

    public View getView(int var1, View var2, ViewGroup var3) {
        int var6 = this.getItemViewType(var1);
        GuidedMeditationAdapter.ItemViewHolder var4 = null;
        GuidedMeditationSound var5 = null;
        GuidedMeditationAdapter.HeaderViewHolder var8 = null;
        GuidedMeditationSound var8_1;
        if(var2 == null) {
            LayoutInflater var7 = ((Activity)this.getContext()).getLayoutInflater();
            if(var6 == 1) {
                var2 = var7.inflate(R.layout.guided_meditation_fragment_list_header, (ViewGroup)null);
                var8 = new GuidedMeditationAdapter.HeaderViewHolder();
                var8.cellView = var2;
                var8.guidedMeditationCategoryTextView = (TextView)var2.findViewById(R.id.guided_meditation_header_title);
                var2.setTag(var8);
            } else {
                var2 = var7.inflate(this.resourceId, var3, false);
                var4 = this.findAllViewsInViewHolder(var2);
                var2.setTag(var4);
                var8_1 = var5;
            }
        } else if(var6 == 1) {
            var8 = (GuidedMeditationAdapter.HeaderViewHolder)var2.getTag();
        } else {
            var4 = (GuidedMeditationAdapter.ItemViewHolder)var2.getTag();
            var8_1 = var5;
        }

        if(this.isPositionOfFooterView(var1)) {
            this.populateFooterView(var4);
            return var2;
        } else {
            var5 = this.getItem(var1);
            if(var6 == 1) {
                this.setupHeaderViewForTag(var1, var5, var8);
                return var2;
            } else {
                this.setupViewForGuidedMeditation(var4, var5, var2, var1);
                return var2;
            }
        }
    }

    public int getViewTypeCount() {
        return 2;
    }

    public boolean isEnabled(int var1) {
        boolean var2 = true;
        if(this.isPositionOfFooterView(var1)) {
            return false;
        } else {
            if(this.getItemViewType(var1) == 1) {
                var2 = false;
            }

            return var2;
        }
    }

    public void onPause() {
        if(this.isVisible) {
            this.isVisible = false;
            this.notifyDataSetChanged();
        }

    }

    public void onResume() {
        if(!this.isVisible) {
            this.isVisible = true;
            this.notifyDataSetChanged();
        }

    }

    public void setGuidedMeditationSounds(List<GuidedMeditationSound> var1) {
        this.guidedMeditationSounds = var1;
        this.categoryPositions = this.loadCategoryPositions();
    }

    private static class HeaderViewHolder {
        View cellView;
        TextView guidedMeditationCategoryTextView;

        private HeaderViewHolder() {
        }
    }

    private static class ItemViewHolder {
        ImageView bulletPointImageView;
        RelativeLayout buttonLayout;
        TextView guidedMeditationDescriptionTextView;
        TextView guidedMeditationDurationTextView;
        TextView guidedMeditationNameTextView;
        TextView guidedMeditationProBadge;
        View itemView;
        TextView newGuidedMeditationTextView;
        View specialBackgroundView;

        private ItemViewHolder() {
        }
    }

    private static enum MeditationState {
        DOWNLOADABLE(1),
        LOCKED(0),
        NORMAL(2),
        PLAYING(4),
        SELECTED(3);

        private final int value;

        private MeditationState(int var3) {
            this.value = var3;
        }

        public int getValue() {
            return this.value;
        }
    }
}
