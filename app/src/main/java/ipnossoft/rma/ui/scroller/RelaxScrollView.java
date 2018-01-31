package ipnossoft.rma.ui.scroller;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;
import com.ipnossoft.api.soundlibrary.Sound;
import com.ipnossoft.api.soundlibrary.SoundLibrary;
import com.ipnossoft.api.soundlibrary.SoundLibraryObserver;
import com.ipnossoft.api.soundlibrary.sounds.GuidedMeditationSound;
import com.ipnossoft.api.soundlibrary.sounds.SoundState;
import ipnossoft.rma.MainActivity;
import ipnossoft.rma.media.SoundManager;
import ipnossoft.rma.media.SoundManagerObserver;
import ipnossoft.rma.ui.button.SoundButton;
import ipnossoft.rma.ui.scroller.column.AmbientSoundColumn;
import ipnossoft.rma.ui.scroller.column.BinauralSoundColumn;
import ipnossoft.rma.ui.scroller.column.IsochronicSoundColumn;
import ipnossoft.rma.util.RelaxAnalytics;
import ipnossoft.rma.util.Utils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.WeakHashMap;

public class RelaxScrollView extends HorizontalScrollView implements SoundManagerObserver, SoundLibraryObserver {
  private static final double MAX_SCROLL_SPEED = 11000.0D;
  private boolean allowedLockedButtonClick = true;
  private AmbientSoundGroup ambientSoundGroup;
  private BinauralSoundGroup binauralSoundGroup;
  private WeakHashMap<String, SoundButton> buttons = new WeakHashMap();
  private RelativeLayout content;
  private boolean contentBuilt = false;
  private List<SoundGroup> groups = new ArrayList();
  private IsochronicSoundGroup isochronicSoundGroup;
  private int lastMeasuredHeight;
  private int lastMeasuredWidth;
  private String lastScrollTrackingEvent = null;
  private int lastSoundScrollPercentage = 0;
  private volatile boolean layoutPerformed = false;
  private boolean measureIsDirty = true;
  private RelaxScrollEventHandler relaxScrollEventHandler;
  private SoundGroup scrollToGroup;
  private boolean splashScreenDismissed = false;

  public RelaxScrollView(Context var1) {
    super(var1);
    this.initGroups();
  }

  public RelaxScrollView(Context var1, AttributeSet var2) {
    super(var1, var2);
    this.initGroups();
  }

  public RelaxScrollView(Context var1, AttributeSet var2, int var3) {
    super(var1, var2, var3);
    this.initGroups();
  }

  private void addSoundGroups() {
    int var1 = 0;

    SoundGroup var3;
    for(Iterator var2 = this.groups.iterator(); var2.hasNext(); var1 = var3.getId()) {
      var3 = (SoundGroup)var2.next();
      var3.addGroupToContainer(this.content, var1);
    }

  }

  private void animateSelectedSoundButtons(boolean var1) {
    Iterator var2 = SoundManager.getInstance().getSelectedSounds().iterator();

    while(var2.hasNext()) {
      Sound var3 = (Sound)var2.next();
      if(!(var3 instanceof GuidedMeditationSound) && SoundManager.getInstance().isPlaying()) {
        SoundButton var4 = this.getButton(var3.getId());
        if(var4 != null) {
          var4.setAnimated(var1);
        }
      }
    }

  }

  private void buildContent() {
    this.hideGroups();
    this.buildSoundGroups();
    this.addSoundGroups();
  }

  private void buildSoundGroups() {
    this.isochronicSoundGroup.addSounds(SoundLibrary.getInstance().getIsochronicSounds(), (MainActivity)this.getContext());
    this.binauralSoundGroup.addSounds(SoundLibrary.getInstance().getBinauralSounds(), (MainActivity)this.getContext());
    this.ambientSoundGroup.addSounds(SoundLibrary.getInstance().getSortedAmbientSounds(), (MainActivity)this.getContext());
  }

  private SoundButton getButton(String var1) {
    return (SoundButton)this.buttons.get(var1);
  }

  private void handleSoundStateUpdated(String var1, SoundState var2) {
    SoundButton var3 = this.getButton(var1);
    if(var3 != null) {
      var3.update(var2);
    }

  }

  private void handleSoundUpdated(Sound var1) {
    SoundButton var2 = this.getButton(var1.getId());
    if(var2 != null) {
      var2.update(var1);
    }

  }

  private void hideGroups() {
    Iterator var1 = this.groups.iterator();

    while(var1.hasNext()) {
      ((SoundGroup)var1.next()).setVisibility(View.GONE);
    }

  }

  private void initGroups() {
    IsochronicSoundColumn.isochronicSoundGroupIndex = 0;
    BinauralSoundColumn.binauralSoundGroupIndex = 0;
    AmbientSoundColumn.ambientSoundGroupIndex = 0;
    this.isochronicSoundGroup = new IsochronicSoundGroup(this.getContext(), this);
    this.binauralSoundGroup = new BinauralSoundGroup(this.getContext(), this);
    this.ambientSoundGroup = new AmbientSoundGroup(this.getContext(), this);
    this.groups.add(this.isochronicSoundGroup);
    this.groups.add(this.binauralSoundGroup);
    this.groups.add(this.ambientSoundGroup);
  }

  private void refreshContentLayoutDelayed(int var1) {
    (new Handler()).postDelayed(new Runnable() {
      public void run() {
        RelaxScrollView.this.measureIsDirty = true;
        RelaxScrollView.this.content.requestLayout();
      }
    }, (long)var1);
  }

  private void scrollTo(SoundGroup var1) {
    if(!this.layoutPerformed) {
      this.scrollToGroup = var1;
    } else {
      this.scrollTo(var1.getScrollLeft(), 0);
    }
  }

  private void showGroups() {
    Iterator var1 = this.groups.iterator();

    while(var1.hasNext()) {
      ((SoundGroup)var1.next()).setVisibility(View.VISIBLE);
    }

  }

  private void trackScroll(int var1) {
    int var5 = this.ambientSoundGroup.getScrollLeft();
    int var6 = this.ambientSoundGroup.getParentWidth();
    int var7 = (int)((double)var5 - 1.5D * (double)var6);
    int var8 = (int)((double)var5 - 0.5D * (double)var6);
    Object var3 = null;
    int var4 = 0;
    String var2;
    if(var1 <= var7) {
      var2 = "isochronics";
    } else if(var1 <= var8) {
      var2 = "binaurals";
    } else {
      var2 = (String)var3;
      if(var1 > var5) {
        var1 = (int)(Math.floor((double)((float)(var1 - var5 + var6) / (float)this.ambientSoundGroup.getWidth() * 10.0F)) * 10.0D);
        var2 = (String)var3;
        var4 = var1;
        if(var1 > 0) {
          var2 = "sounds";
          var4 = var1;
        }
      }
    }

    if(var2 != null && var2.equals("sounds")) {
      if(var4 != this.lastSoundScrollPercentage) {
        RelaxAnalytics.flagScrolledIntoSounds(var4);
        this.lastSoundScrollPercentage = var4;
        this.lastScrollTrackingEvent = var2;
      }
    } else if(var2 != null && (this.lastScrollTrackingEvent == null || !var2.equals(this.lastScrollTrackingEvent))) {
      if(var2.equals("isochronics")) {
        RelaxAnalytics.flagScrolledIntoIsochronics();
      } else {
        RelaxAnalytics.flagScrolledIntoBinaurals();
      }

      this.lastScrollTrackingEvent = var2;
      this.lastSoundScrollPercentage = 0;
      return;
    }

  }

  private void updateAllSelectedSoundsOnMainThread() {
    ArrayList var1 = new ArrayList();
    Iterator var2 = this.buttons.values().iterator();

    while(var2.hasNext()) {
      SoundButton var3 = (SoundButton)var2.next();
      if(var3.isSelected()) {
        var1.add(var3.getSound());
      }
    }

    this.updateSoundsOnMainThread(var1);
  }

  private void updateGroupsSize(int var1, int var2) {
    this.measureIsDirty = true;
    Iterator var3 = this.groups.iterator();

    while(var3.hasNext()) {
      SoundGroup var4 = (SoundGroup)var3.next();
      var4.setViewPortDimensions(var1, var2, (Activity)this.getContext());
      var4.updatePaddingLayout();
      var4.updateColumnLayout();
      var4.requestLayout();
    }

    this.refreshContentLayoutDelayed(10);
  }

  private void updateSounds(List<Sound> var1) {
    this.measureIsDirty = true;
    Iterator var2 = var1.iterator();

    while(var2.hasNext()) {
      this.handleSoundUpdated((Sound)var2.next());
    }

  }

  private void updateSoundsOnMainThread(final List<Sound> var1) {
    this.measureIsDirty = true;
    if(Utils.isMainThread()) {
      this.updateSounds(var1);
    } else {
      ((Activity)this.getContext()).runOnUiThread(new Runnable() {
        public void run() {
          RelaxScrollView.this.updateSounds(var1);
        }
      });
    }
  }

  public void addButton(SoundButton var1) {
    this.buttons.put(var1.getSound().getId(), var1);
  }

  public void disposeButton(String var1) {
    this.buttons.remove(var1);
  }

  public void flashNewGiftSpecialEffectsOnSounds(List<String> var1) {
    Iterator var3 = var1.iterator();

    while(var3.hasNext()) {
      String var2 = (String)var3.next();
      ((SoundButton)this.buttons.get(var2)).flashGiftedSpecialEffect();
    }

  }

  public void fling(int var1) {
    super.fling((int)(Math.min((double)Math.abs(var1), 11000.0D) * (double)Math.signum((float)var1)));
  }

  public void forceOnMesure() {
    this.measureIsDirty = true;
  }

  public AmbientSoundGroup getAmbientSoundGroup() {
    return this.ambientSoundGroup;
  }

  public void initialize(RelativeLayout var1, RelaxScrollEventHandler var2) {
    this.content = var1;
    this.relaxScrollEventHandler = var2;
    SoundManager.getInstance().registerObserver(this);
    SoundLibrary.getInstance().registerObserver(this);
  }

  public boolean isAllowedLockedButtonClick() {
    return this.allowedLockedButtonClick;
  }

  public void onClearSounds(List<Sound> var1) {
    this.updateAllSelectedSoundsOnMainThread();
  }

  protected void onLayout(boolean var1, int var2, int var3, final int var4, final int var5) {
    super.onLayout(var1, var2, var3, var4, var5);
    if(this.contentBuilt && var4 > 0 && var5 > 0) {
      (new Handler()).postDelayed(new Runnable() {
        public void run() {
          RelaxScrollView.this.lastMeasuredHeight = var5;
          RelaxScrollView.this.lastMeasuredWidth = var4;
          RelaxScrollView.this.measureIsDirty = false;
        }
      }, 1000L);
    }

    if(this.scrollToGroup != null && !var1) {
      this.layoutPerformed = true;
      this.scrollTo(this.scrollToGroup);
      this.scrollToGroup = null;
    }

    Iterator var6 = this.groups.iterator();

    while(var6.hasNext()) {
      SoundGroup var7 = (SoundGroup)var6.next();
      if(var7.getScrollOffset() != 0) {
        this.scrollTo(this.getScrollX() + var7.getScrollOffset() + var7.getColumnMargin(), 0);
        var7.setScrollOffset(0);
      }
    }

    if(!this.splashScreenDismissed && this.layoutPerformed) {
      ((MainActivity)this.getContext()).onApplicationReady();
      this.splashScreenDismissed = true;
    }

  }

  protected void onMeasure(int var1, int var2) {
    if(this.measureIsDirty) {
      super.onMeasure(var1, var2);
      this.setMeasuredDimension(this.getMeasuredWidth(), this.getMeasuredHeight());
    } else {
      this.setMeasuredDimension(this.lastMeasuredWidth, this.lastMeasuredHeight);
    }
  }

  public void onNewSound(Sound var1) {
  }

  public void onNewSounds(List<Sound> var1) {
    if(this.contentBuilt) {
      ArrayList var2 = new ArrayList();
      Iterator var4 = var1.iterator();

      while(var4.hasNext()) {
        Sound var3 = (Sound)var4.next();
        if(!(var3 instanceof GuidedMeditationSound)) {
          var2.add(var3);
        }
      }

      this.ambientSoundGroup.addSounds(var2, (MainActivity)this.getContext());
      ((Activity)this.getContext()).runOnUiThread(new Runnable() {
        public void run() {
          RelaxScrollView.this.forceOnMesure();
          RelaxScrollView.this.requestLayout();
        }
      });
    }
  }

  public void onPausedAllSounds() {
    this.updateSoundsOnMainThread(SoundManager.getInstance().getSelectedSounds());
  }

  public void onResumedAllSounds() {
    this.updateSoundsOnMainThread(SoundManager.getInstance().getSelectedSounds());
  }

  protected void onScrollChanged(int var1, int var2, int var3, int var4) {
    super.onScrollChanged(var1, var2, var3, var4);
    this.trackScroll(var1);
    this.relaxScrollEventHandler.onScrollChanged(var1, var2, var3, var4);
  }

  public void onSelectionChanged(List<Sound> var1, List<Sound> var2) {
    this.updateSoundsOnMainThread(var1);
    this.updateSoundsOnMainThread(var2);
  }

  protected void onSizeChanged(int var1, int var2, int var3, int var4) {
    super.onSizeChanged(var1, var2, var3, var4);
    this.updateGroupsSize(var1, var2);
    if(!this.contentBuilt) {
      this.buildContent();
      this.showGroups();
      this.updateGroupsSize(var1, var2);
      this.scrollTo(this.ambientSoundGroup);
      this.contentBuilt = true;
    }

  }

  public void onSoundManagerException(String var1, Exception var2) {
  }

  public void onSoundPlayed(Sound var1) {
    this.updateSound(var1.getId(), SoundState.PLAYING);
  }

  public void onSoundStopped(Sound var1, float var2) {
    this.updateSound(var1.getId(), SoundState.STOPPED);
  }

  public void onSoundUpdated(final Sound var1) {
    this.measureIsDirty = true;
    if(Utils.isMainThread()) {
      this.handleSoundUpdated(var1);
    } else {
      ((Activity)this.getContext()).runOnUiThread(new Runnable() {
        public void run() {
          RelaxScrollView.this.handleSoundUpdated(var1);
        }
      });
    }
  }

  public void onSoundVolumeChange(String var1, float var2) {
  }

  public void onSoundsUpdated(List<Sound> var1) {
    this.updateSoundsOnMainThread(var1);
  }

  public void pauseAnimations() {
    this.animateSelectedSoundButtons(false);
  }

  public void resumeAnimations() {
    this.animateSelectedSoundButtons(true);
  }

  public void setAllowedLockedButtonClick(boolean var1) {
    this.allowedLockedButtonClick = var1;
  }

  public void smoothScrollToEnd() {
    this.post(new Runnable() {
      public void run() {
        RelaxScrollView.this.fullScroll(66);
      }
    });
  }

  public void updateSound(final String var1, final SoundState var2) {
    if(Utils.isMainThread()) {
      this.handleSoundStateUpdated(var1, var2);
    } else {
      ((Activity)this.getContext()).runOnUiThread(new Runnable() {
        public void run() {
          RelaxScrollView.this.handleSoundStateUpdated(var1, var2);
        }
      });
    }
  }
}
