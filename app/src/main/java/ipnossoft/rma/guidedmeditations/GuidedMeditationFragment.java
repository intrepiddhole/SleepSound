package ipnossoft.rma.guidedmeditations;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import com.ipnossoft.api.dynamiccontent.model.InAppPurchase;
import com.ipnossoft.api.featuremanager.FeatureManager;
import com.ipnossoft.api.featuremanager.FeatureManagerObserver;
import com.ipnossoft.api.featuremanager.data.Subscription;
import com.ipnossoft.api.soundlibrary.Sound;
import com.ipnossoft.api.soundlibrary.SoundLibrary;
import com.ipnossoft.api.soundlibrary.SoundLibraryObserver;
import com.ipnossoft.api.soundlibrary.sounds.GuidedMeditationSound;
import com.ipnossoft.api.soundlibrary.sounds.SoundState;
import ipnossoft.rma.MainActivity;
import ipnossoft.rma.NavigationMenuItemFragment;
import ipnossoft.rma.PersistedDataManager;
import ipnossoft.rma.RelaxMelodiesApp;
import ipnossoft.rma.animation.ViewTransitionAnimator;
import ipnossoft.rma.free.R;
import ipnossoft.rma.media.GuidedMeditationVolumeManager;
import ipnossoft.rma.media.SoundManager;
import ipnossoft.rma.media.SoundManagerObserver;
import ipnossoft.rma.media.SoundTrack;
import ipnossoft.rma.ui.button.RoundBorderedButton;
import ipnossoft.rma.ui.dialog.RelaxDialog.Builder;
import ipnossoft.rma.ui.dialog.RelaxDialog.RelaxDialogButtonOrientation;
import ipnossoft.rma.util.RelaxAnalytics;
import ipnossoft.rma.util.Utils;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import org.onepf.oms.appstore.googleUtils.Purchase;

public class GuidedMeditationFragment extends Fragment implements SoundLibraryObserver, FeatureManagerObserver, SoundManagerObserver, OnScrollListener, NavigationMenuItemFragment, OnClickListener {
  private static final String PREF_FIRST_TIME_IN_MEDITATIONS = "FIRST_TIME_IN_MEDITATIONS";
  private boolean canTrackScroll = false;
  private boolean firstResume;
  private GuidedMeditationAdapter guidedMeditationAdapter;
  private TextView guidedMeditationEmpty;
  private ListView guidedMeditationListView;
  private GuidedMeditationPlayingProgressView guidedMeditationPlayingProgressView;
  private List<GuidedMeditationSound> guidedMeditationSounds = new ArrayList();
  private GuidedMeditationVolumeManager guidedMeditationVolumeManager = null;
  private RelativeLayout guidedSubVolumeLayout;
  private int lastTrackedScrollPercentage = 0;
  private LinearLayout meditationPlayingLayout;
  private TextView playingMeditationDurationTextView;
  private TextView playingMeditationTitleTextView;
  private SoundTrack selectedGuidedSoundTrack = null;
  private Handler updateMeditationProgressHandler = new Handler();
  private Runnable updateMeditationProgressRunnable;
  private ViewTransitionAnimator viewTransitionAnimator;

  public GuidedMeditationFragment() {
  }

  private void addClonedFeaturedMeditationToList(List<GuidedMeditationSound> var1, GuidedMeditationSound var2) {
    Object var3 = null;

    try {
      var2 = var2.clone();
    } catch (CloneNotSupportedException var4) {
      var2 = (GuidedMeditationSound)var3;
    }

    if(var2 != null) {
      var2.setTag(this.getString(R.string.featured_category_title));
      var1.add(var2);
    }

  }

  private void addHeaderView() {
    TextView var1 = new TextView(this.getActivity());
    var1.setText(R.string.guided_meditation_are_only_in_english);
    var1.setTextColor(this.getResources().getColor(R.color.list_view_item_description_color));
    var1.setGravity(17);
    var1.setMaxLines(2);
    var1.setTextSize(0, this.getResources().getDimension(R.dimen.small_general_font_size));
    int var2 = (int)this.getResources().getDimension(R.dimen.guided_meditation_fragment_only_available_english_label_padding_top);
    int var3 = (int)this.getResources().getDimension(R.dimen.guided_only_in_english_padding_lr);
    var1.setPadding((int)this.getResources().getDimension(R.dimen.guided_only_in_english_padding_lr), var2, var3, 0);
    this.guidedMeditationListView.addHeaderView(var1);
  }

  private void doUpdateUIAndData() {
    this.updateGuidedListView();
    if(this.guidedMeditationSounds != null && !this.guidedMeditationSounds.isEmpty()) {
      this.setVisibleViewForGuidedMeditations();
    } else {
      this.setVisibleViewForEmptyGuidedMeditations();
    }
  }

  private List<GuidedMeditationSound> getFeaturedWithRegularSounds(List<GuidedMeditationSound> var1) {
    return this.insertFeaturedBeforeTheRestOfMeditations(this.loadFeaturedGuidedMeditations(var1), var1);
  }

  private void handleStateChangeForSubVolume(String var1, SoundState var2) {
    this.updateGuidedSubVolumeBar(false);
    if(this.selectedGuidedSoundTrack != null && this.selectedGuidedSoundTrack.getSound().getId().equals(var1) && var2 == SoundState.STOPPED || this.selectedGuidedSoundTrack == null) {
      this.hideGuidedSubVolumeBar();
    }

    this.updatePlayingGuidedMeditationContent();
  }

  private void hideGuidedSubVolumeBar() {
    if(this.guidedMeditationVolumeManager != null) {
      this.guidedMeditationVolumeManager.hideVolumeLayout();
    }

    this.selectedGuidedSoundTrack = null;
  }

  private void hidePlayingGuidedMeditation() {
    this.toggleListViewClickable(true);
    this.stopUpdatingPlayingGuidedMeditationProgress();
    if(this.meditationPlayingLayout.getVisibility() == View.VISIBLE) {
      this.viewTransitionAnimator.transitionBetweenViews(this.meditationPlayingLayout, this.guidedMeditationListView);
    }

    this.resetPlayingPercentageAfterDelay();
  }

  private void hidePlayingGuidedMeditationInstant() {
    this.guidedMeditationListView.setVisibility(View.VISIBLE);
    this.guidedMeditationListView.setAlpha(1.0F);
    this.toggleListViewClickable(true);
    this.meditationPlayingLayout.setVisibility(View.GONE);
  }

  private List<GuidedMeditationSound> insertFeaturedBeforeTheRestOfMeditations(List<GuidedMeditationSound> var1, List<GuidedMeditationSound> var2) {
    ArrayList var3 = new ArrayList();
    var3.addAll(var1);
    if(var2.size() > 1) {
      var3.addAll(var2);
    }

    return var3;
  }

  private boolean isOldUserThatBoughtPremiumInApp() {
    return RelaxMelodiesApp.isFreeVersion() && RelaxMelodiesApp.isPremium().booleanValue();
  }

  private List<GuidedMeditationSound> loadFeaturedGuidedMeditations(List<GuidedMeditationSound> var1) {
    ArrayList var2 = new ArrayList();
    Iterator var4 = var1.iterator();

    while(var4.hasNext()) {
      GuidedMeditationSound var3 = (GuidedMeditationSound)var4.next();
      if(GuidedMeditationStatus.getInstance().isMeditationFeatured(var3)) {
        this.addClonedFeaturedMeditationToList(var2, var3);
      }
    }

    return var2;
  }

  private void notifyAdapterDataSetChanged() {
    if(this.guidedMeditationListView != null) {
      this.guidedMeditationListView.setOnItemClickListener(new GuidedMeditationOnItemClickListener(this.guidedMeditationSounds, this.getActivity(), this.shouldAddLanguageHeaderView()));
      if(this.guidedMeditationAdapter != null) {
        this.guidedMeditationAdapter.notifyDataSetChanged();
      }
    }

  }

  private void prepareFragmentForHidden() {
    if(this.guidedMeditationAdapter != null) {
      this.guidedMeditationAdapter.onPause();
    }

    this.updateMeditationPlayingLayoutVisibilityInstant();
    this.stopUpdatingPlayingGuidedMeditationProgress();
    this.guidedMeditationListView.setVisibility(View.GONE);
    this.meditationPlayingLayout.setVisibility(View.GONE);
  }

  private void prepareFragmentForVisible() {
    if(this.guidedMeditationAdapter != null) {
      this.guidedMeditationAdapter.onResume();
    }

    this.updateMeditationPlayingLayoutVisibilityInstant();
  }

  private void refresh(Sound var1, SoundState var2) {
    if(var1 instanceof GuidedMeditationSound) {
      this.handleStateChangeForSubVolume(var1.getId(), var2);
      this.updateUIAndData();
    }

  }

  private void refreshCurrentGuided() {
    SoundTrack var1 = SoundManager.getInstance().selectedGuidedMeditation();
    if(var1 != null) {
      this.refresh(var1.getSound(), var1.getState());
    } else {
      this.selectedGuidedSoundTrack = null;
      this.handleStateChangeForSubVolume((String)null, SoundState.STOPPED);
      this.updateUIAndData();
    }
  }

  private void resetPlayingPercentageAfterDelay() {
    (new Handler()).postDelayed(new Runnable() {
      public void run() {
        if(GuidedMeditationFragment.this.guidedMeditationPlayingProgressView != null) {
          GuidedMeditationFragment.this.guidedMeditationPlayingProgressView.setPercentageProgress(0.0F);
        }

      }
    }, 250L);
  }

  private void setVisibilityForGuidedMeditationEmpty(boolean var1) {
    if(this.guidedMeditationEmpty != null) {
      TextView var2 = this.guidedMeditationEmpty;
      byte var3;
      if(var1) {
        var3 = 0;
      } else {
        var3 = 8;
      }

      var2.setVisibility(var3);
    }

  }

  private void setVisibilityForGuidedMeditationListView(boolean var1) {
    if(this.guidedMeditationListView != null) {
      ListView var2 = this.guidedMeditationListView;
      byte var3;
      if(var1) {
        var3 = 0;
      } else {
        var3 = 8;
      }

      var2.setVisibility(var3);
    }

  }

  private void setVisibleViewForEmptyGuidedMeditations() {
    this.setVisibilityForGuidedMeditationListView(false);
    this.setVisibilityForGuidedMeditationEmpty(true);
  }

  private void setVisibleViewForGuidedMeditations() {
    this.setVisibilityForGuidedMeditationListView(true);
    this.setVisibilityForGuidedMeditationEmpty(false);
  }

  private boolean shouldAddLanguageHeaderView() {
    return !Utils.getCurrentLanguageLocale(RelaxMelodiesApp.getInstance().getApplicationContext()).equals(Locale.ENGLISH.getLanguage());
  }

  private void showFidelityPopup() {
    Builder var1 = new Builder(this.getActivity(), RelaxDialogButtonOrientation.VERTICAL);
    var1.setMessage(R.string.fidelity_dialog_message).setTitle(R.string.fidelity_dialog_title);
    var1.setPositiveButton(R.string.error_dialog_button_ok, (OnClickListener)null);
    var1.show();
  }

  private void showPlayingGuidedMeditation() {
    this.toggleListViewClickable(false);
    this.updatePlayingGuidedMeditationContent();
    if(this.meditationPlayingLayout.getVisibility() != View.VISIBLE) {
      this.viewTransitionAnimator.transitionBetweenViews(this.guidedMeditationListView, this.meditationPlayingLayout);
    }

    this.showTimerLabel();
  }

  private void showPlayingGuidedMeditationInstant() {
    this.meditationPlayingLayout.setVisibility(View.VISIBLE);
    this.meditationPlayingLayout.setAlpha(1.0F);
    this.guidedMeditationListView.setVisibility(View.GONE);
    this.toggleListViewClickable(false);
    this.updatePlayingGuidedMeditationContent();
    this.showTimerLabel();
  }

  private void showTimerLabel() {
    MainActivity var1 = (MainActivity)this.getActivity();
    if(var1 != null) {
      var1.showTimerLabel();
    }

  }

  private void showcaseScrollingFeatureIfNecessary() {
    final Handler var1 = new Handler();
    var1.post(new Runnable() {
      public void run() {
        GuidedMeditationFragment.this.canTrackScroll = true;
        if(GuidedMeditationFragment.this.isVisible() && GuidedMeditationFragment.this.guidedMeditationListView.getVisibility() == 0 && !GuidedMeditationStatus.getInstance().didAlreadyShowcasedMeditationScroll()) {
          GuidedMeditationStatus.getInstance().flagAlreadyShowcasedMeditationScroll();
          if(!GuidedMeditationStatus.getInstance().didScrolled()) {
            GuidedMeditationFragment.this.canTrackScroll = false;
            final int var1x = (int)GuidedMeditationFragment.this.getResources().getDimension(R.feature_guided_meditation_cell_height) * 5;
            GuidedMeditationFragment.this.guidedMeditationListView.smoothScrollBy(var1x, 0);
            var1.postDelayed(new Runnable() {
              public void run() {
                GuidedMeditationFragment.this.guidedMeditationListView.smoothScrollBy(-var1x, 3500);
                var1.postDelayed(new Runnable() {
                  public void run() {
                    GuidedMeditationFragment.this.canTrackScroll = true;
                  }
                }, 3500L);
              }
            }, 100L);
          }
        }

      }
    });
  }

  private void stopUpdatingPlayingGuidedMeditationProgress() {
    if(this.updateMeditationProgressRunnable != null) {
      this.updateMeditationProgressHandler.removeCallbacks(this.updateMeditationProgressRunnable);
    }

  }

  private void toggleListViewClickable(boolean var1) {
    this.guidedMeditationListView.setEnabled(var1);
    this.guidedMeditationListView.setClickable(var1);
  }

  private void trackScroll(int var1, int var2, int var3) {
    if(this.canTrackScroll && var1 >= var2 / 2 - 1) {
      GuidedMeditationStatus.getInstance().flagDidScrolled();
      var1 = (int)(Math.floor((double)((float)(var1 + var2) / (float)var3 * 10.0F)) * 10.0D);
      if(var1 > 0 && var1 != this.lastTrackedScrollPercentage) {
        RelaxAnalytics.logScrolledIntoMeditations(var1);
        this.lastTrackedScrollPercentage = var1;
      }
    }

  }

  private void updateGuidedListView() {
    this.guidedMeditationSounds.clear();
    this.guidedMeditationSounds.addAll(this.getFeaturedWithRegularSounds(SoundLibrary.getInstance().getGuidedMeditationSounds()));
    if(this.guidedMeditationAdapter != null) {
      this.guidedMeditationAdapter.setGuidedMeditationSounds(this.guidedMeditationSounds);
    }

    this.notifyAdapterDataSetChanged();
  }

  private void updateGuidedSubVolumeBar(boolean var1) {
    if(this.guidedSubVolumeLayout != null) {
      if(this.guidedMeditationVolumeManager == null) {
        this.guidedMeditationVolumeManager = new GuidedMeditationVolumeManager(this.getActivity(), this.guidedSubVolumeLayout);
      }

      boolean var3 = false;
      Iterator var4 = SoundManager.getInstance().getSelectedTracks().iterator();

      boolean var2;
      while(true) {
        var2 = var3;
        if(!var4.hasNext()) {
          break;
        }

        SoundTrack var5 = (SoundTrack)var4.next();
        if(var5.getSound() instanceof GuidedMeditationSound) {
          this.selectedGuidedSoundTrack = var5;
          var2 = true;
          break;
        }
      }

      if(var2) {
        if(var1) {
          this.guidedMeditationVolumeManager.showVolumeLayoutInstant(this.selectedGuidedSoundTrack);
          return;
        }

        this.guidedMeditationVolumeManager.showVolumeLayout(this.selectedGuidedSoundTrack);
        return;
      }
    }

  }

  private void updateMeditationPlayingLayoutVisibility() {
    boolean var1 = SoundManager.getInstance().hasGuidedMeditationSelected();
    if(this.meditationPlayingLayout != null && this.guidedMeditationListView != null) {
      if(!var1) {
        this.hidePlayingGuidedMeditation();
        return;
      }

      this.showPlayingGuidedMeditation();
    }

  }

  private void updateMeditationPlayingLayoutVisibilityInstant() {
    boolean var1 = SoundManager.getInstance().hasGuidedMeditationSelected();
    if(this.meditationPlayingLayout != null && this.guidedMeditationListView != null) {
      if(!var1) {
        this.hidePlayingGuidedMeditationInstant();
        return;
      }

      this.showPlayingGuidedMeditationInstant();
    }

  }

  private void updatePlayingGuidedMeditationContent() {
    final SoundTrack var1 = SoundManager.getInstance().selectedGuidedMeditation();
    if(var1 != null) {
      if(this.playingMeditationTitleTextView != null) {
        this.playingMeditationTitleTextView.setText(var1.getSound().getName());
      }

      if(this.guidedMeditationPlayingProgressView != null) {
        this.updateMeditationProgressRunnable = new Runnable() {
          public void run() {
            float var1x = (float)SoundManager.getInstance().getProgressOfSound(var1.getSound());
            if(var1.getMediaPlayer() == null) {
              try {
                var1.initMediaPlayer();
              } catch (Exception var6) {
                var6.printStackTrace();
              }
            }

            if(var1.getMediaPlayer() != null) {
              int var3 = var1.getMediaPlayer().getDuration();
              long var4 = (long)((float)var3 - var1x);
              if(var1x > 0.0F) {
                GuidedMeditationFragment.this.guidedMeditationPlayingProgressView.setPercentageProgress(var1x / (float)var3);
              } else {
                GuidedMeditationFragment.this.guidedMeditationPlayingProgressView.setPercentageProgress(0.0F);
              }

              GuidedMeditationFragment.this.updatePlayingGuidedMeditationDurationText(var4);
              GuidedMeditationFragment.this.updatePlayingGuidedMeditationProgress();
              if(var4 == 0L) {
                GuidedMeditationFragment.this.stopUpdatingPlayingGuidedMeditationProgress();
                SoundManager.getInstance().stopSound(GuidedMeditationFragment.this.selectedGuidedSoundTrack.getSound());
              }
            }

          }
        };
        this.updateMeditationProgressRunnable.run();
        this.updatePlayingGuidedMeditationProgress();
      }
    }

  }

  private void updatePlayingGuidedMeditationDurationText(long var1) {
    String var3 = String.format("%d:%02d", new Object[]{Long.valueOf(TimeUnit.MILLISECONDS.toMinutes(var1)), Long.valueOf(TimeUnit.MILLISECONDS.toSeconds(var1) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(var1)))});
    this.playingMeditationDurationTextView.setText(var3);
  }

  private void updatePlayingGuidedMeditationProgress() {
    this.updateMeditationProgressHandler.postDelayed(this.updateMeditationProgressRunnable, 250L);
  }

  private void updateSubVolumeTextAfterFirstLoad(final View var1) {
    var1.addOnLayoutChangeListener(new OnLayoutChangeListener() {
      public void onLayoutChange(View var1x, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9) {
        if(GuidedMeditationFragment.this.guidedMeditationVolumeManager != null) {
          GuidedMeditationFragment.this.guidedMeditationVolumeManager.setSubvolumeText();
        }

        GuidedMeditationFragment.this.refreshSubVolumeHint();
        var1.removeOnLayoutChangeListener(this);
      }
    });
  }

  private void updateUIAndData() {
    if(this.getActivity() != null) {
      this.getActivity().runOnUiThread(new Runnable() {
        public void run() {
          GuidedMeditationFragment.this.doUpdateUIAndData();
          GuidedMeditationFragment.this.updateMeditationPlayingLayoutVisibility();
        }
      });
    }

  }

  public boolean isFirstTimeInMeditations() {
    return PersistedDataManager.getBoolean("FIRST_TIME_IN_MEDITATIONS", true, RelaxMelodiesApp.getInstance().getApplicationContext()).booleanValue();
  }

  public void onClearSounds(List<Sound> var1) {
    if(this.viewTransitionAnimator.areViewsTransitioning()) {
      this.viewTransitionAnimator.cancelAnimations();
      this.hidePlayingGuidedMeditationInstant();
      this.guidedMeditationVolumeManager.hideVolumeLayoutInstant();
    } else {
      this.refreshCurrentGuided();
    }

    this.showTimerLabel();
  }

  public void onClick(View var1) {
    if(var1.getId() == R.id.guided_meditation_playing_stop_button) {
      if(this.selectedGuidedSoundTrack != null && this.meditationPlayingLayout.getAlpha() == 1.0F) {
        SoundManager.getInstance().stopSound(this.selectedGuidedSoundTrack.getSound());
      }

      this.refreshCurrentGuided();
      this.showTimerLabel();
    }

  }

  public void onCreate(@Nullable Bundle var1) {
    super.onCreate(var1);
    SoundLibrary.getInstance().registerObserver(this);
    SoundManager.getInstance().registerObserver(this);
    FeatureManager.getInstance().registerObserver(this);
  }

  public View onCreateView(LayoutInflater var1, ViewGroup var2, Bundle var3) {
    View var4 = var1.inflate(R.layout.guided_meditation_fragment, var2, false);
    this.viewTransitionAnimator = new ViewTransitionAnimator();
    this.guidedMeditationListView = (ListView)var4.findViewById(R.id.guided_meditation_list_layout);
    this.guidedMeditationEmpty = (TextView)var4.findViewById(R.id.guided_meditation_empty_list_label);
    this.guidedSubVolumeLayout = (RelativeLayout)var4.findViewById(R.id.layout_guided_subvolume);
    this.meditationPlayingLayout = (LinearLayout)var4.findViewById(R.id.layout_guided_meditation_playing);
    this.playingMeditationTitleTextView = (TextView)this.meditationPlayingLayout.findViewById(R.id.guided_meditation_playing_title);
    this.guidedMeditationPlayingProgressView = (GuidedMeditationPlayingProgressView)this.meditationPlayingLayout.findViewById(R.id.guided_meditation_playing_progress_view);
    this.playingMeditationDurationTextView = (TextView)this.meditationPlayingLayout.findViewById(R.id.guided_meditation_playing_duration);
    ((RelativeLayout)this.meditationPlayingLayout.findViewById(R.id.guided_meditation_playing_play_pause_layout)).setOnClickListener(this);
    ((RoundBorderedButton)this.meditationPlayingLayout.findViewById(R.id.guided_meditation_playing_stop_button)).setOnClickListener(this);
    this.updateUIAndData();
    if(this.shouldAddLanguageHeaderView()) {
      this.addHeaderView();
    }

    this.guidedMeditationAdapter = new GuidedMeditationAdapter(this.getActivity(), R.layout.guided_meditation_fragment_list_item, this.guidedMeditationSounds);
    this.guidedMeditationListView.setAdapter(this.guidedMeditationAdapter);
    this.guidedMeditationListView.setOnScrollListener(this);
    this.updateGuidedSubVolumeBar(true);
    this.updateSubVolumeTextAfterFirstLoad(var4);
    this.updateMeditationPlayingLayoutVisibilityInstant();
    return var4;
  }

  public void onDestroy() {
    SoundLibrary.getInstance().unregisterObserver(this);
    SoundManager.getInstance().unregisterObserver(this);
    FeatureManager.getInstance().unregisterObserver(this);
    super.onDestroy();
  }

  public void onFeatureDownloaded(InAppPurchase var1, String[] var2) {
    this.updateUIAndData();
  }

  public void onFeatureManagerSetupFinished() {
  }

  public void onFeatureUnlocked(InAppPurchase var1, String var2) {
    this.updateUIAndData();
  }

  public void onHiddenChanged(boolean var1) {
    if(!var1) {
      this.showcaseScrollingFeatureIfNecessary();
    }

  }

  public void onNewSound(Sound var1) {
    this.updateUIAndData();
  }

  public void onNewSounds(List<Sound> var1) {
    this.updateUIAndData();
  }

  public void onPause() {
    super.onPause();
    this.prepareFragmentForHidden();
  }

  public void onPausedAllSounds() {
    this.refreshCurrentGuided();
  }

  public void onPurchaseCompleted(InAppPurchase var1, Purchase var2, Date var3) {
    this.updateUIAndData();
  }

  public void onPurchasesAvailable(List<InAppPurchase> var1) {
    this.updateUIAndData();
  }

  public void onResume() {
    super.onResume();
    if(this.isFirstTimeInMeditations() && this.isOldUserThatBoughtPremiumInApp()) {
      this.showFidelityPopup();
      this.setFirstTimeInMeditations(false);
    }

    this.prepareFragmentForVisible();
    this.showTimerLabel();
    if(!this.firstResume) {
      this.firstResume = true;
      this.onHiddenChanged(false);
    }

  }

  public void onResumedAllSounds() {
    this.refreshCurrentGuided();
  }

  public void onScroll(AbsListView var1, int var2, int var3, int var4) {
    this.trackScroll(var2, var3, var4);
  }

  public void onScrollStateChanged(AbsListView var1, int var2) {
  }

  public void onSelectionChanged(List<Sound> var1, List<Sound> var2) {
    this.refreshCurrentGuided();
  }

  public void onSoundManagerException(String var1, Exception var2) {
  }

  public void onSoundPlayed(Sound var1) {
    this.refresh(var1, SoundState.PLAYING);
  }

  public void onSoundStopped(Sound var1, float var2) {
    this.refresh(var1, SoundState.STOPPED);
  }

  public void onSoundUpdated(Sound var1) {
    this.updateUIAndData();
  }

  public void onSoundVolumeChange(String var1, float var2) {
    if(this.guidedMeditationVolumeManager != null) {
      this.guidedMeditationVolumeManager.updateProgress();
    }

  }

  public void onSoundsUpdated(List<Sound> var1) {
    this.updateUIAndData();
  }

  public void onSubscriptionChanged(Subscription var1, boolean var2) {
    this.updateUIAndData();
  }

  public void onUnresolvedPurchases(List<String> var1) {
  }

  public void refreshSubVolumeHint() {
    if(this.guidedMeditationVolumeManager != null && this.guidedMeditationVolumeManager.isVolumeBarVisible()) {
      this.guidedMeditationVolumeManager.refreshHintStatus();
    }

  }

  public void setFirstTimeInMeditations(boolean var1) {
    PersistedDataManager.saveBoolean("FIRST_TIME_IN_MEDITATIONS", var1, RelaxMelodiesApp.getInstance().getApplicationContext());
  }
}
