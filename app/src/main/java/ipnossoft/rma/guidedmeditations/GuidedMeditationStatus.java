package ipnossoft.rma.guidedmeditations;

import com.ipnossoft.api.soundlibrary.Sound;
import com.ipnossoft.api.soundlibrary.SoundLibrary;
import com.ipnossoft.api.soundlibrary.SoundLibraryObserver;
import com.ipnossoft.api.soundlibrary.sounds.GuidedMeditationSound;
import ipnossoft.rma.PersistedDataManager;
import ipnossoft.rma.RelaxMelodiesApp;
import ipnossoft.rma.media.SoundManager;
import ipnossoft.rma.media.SoundManagerObserver;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GuidedMeditationStatus implements SoundManagerObserver, SoundLibraryObserver {
  private static final String CLICKED_MEDITATIONS = "clicked.meditations";
  public static final double COMPLETED_AT_PERCENTAGE = 0.85D;
  private static final String DATE_TO_REMOVE_BUILT_IN_MEDITATION_AS_FEATURED = "fate.to.remove.built.in.meditation.as.featured";
  private static final String FREE_SEEN_MEDITATIONS = "free.seen.meditations";
  private static final String LISTENED_TO_MEDITATIONS = "listened.to.meditations";
  private static final String NEWLY_ADDED_MEDITATIONS = "newly.added.meditations";
  private static final String NEW_MEDITATIONS_LIST = "new.meditation.list";
  private static GuidedMeditationStatus instance;
  private boolean alreadyShowcasedMeditationScroll;
  private boolean configured = false;
  private boolean didScrolledInMeditation = false;
  private boolean isBuiltInGuidedMeditationFeatured = false;
  private long lastGuidedClickedTimeStamp = 0L;
  private List<GuidedMeditationStatusListener> observers = new ArrayList();

  public GuidedMeditationStatus() {
  }

  private void addNewGuidedMeditationToNewList(GuidedMeditationSound var1) {
    this.saveToPreferences("new.meditation.list", var1);
    this.addNewlyAddMeditationsData();
    Iterator var2 = this.observers.iterator();

    while(var2.hasNext()) {
      ((GuidedMeditationStatusListener)var2.next()).newlyAddedGuidedMeditation(true);
    }

  }

  private void addNewlyAddMeditationsData() {
    PersistedDataManager.saveInteger("newly.added.meditations", PersistedDataManager.getInteger("newly.added.meditations", 0, RelaxMelodiesApp.getInstance().getApplicationContext()) + 1, RelaxMelodiesApp.getInstance().getApplicationContext());
  }

  private boolean checkForNewGuidedMeditation(Sound var1) {
    this.throwExceptionIfNotConfigured();
    if(var1 instanceof GuidedMeditationSound) {
      GuidedMeditationSound var3 = (GuidedMeditationSound)var1;
      String var2 = PersistedDataManager.getString("new.meditation.list", "", RelaxMelodiesApp.getInstance().getApplicationContext());
      if(var3.isNew() && !var2.contains(var3.getId())) {
        this.addNewGuidedMeditationToNewList(var3);
        return true;
      }
    }

    return false;
  }

  private void configureBuiltInFeaturedGuidedMeditation() {
    long var1 = PersistedDataManager.getLong("fate.to.remove.built.in.meditation.as.featured", 0L, RelaxMelodiesApp.getInstance().getApplicationContext());
    if(var1 == 0L) {
      PersistedDataManager.saveLong("fate.to.remove.built.in.meditation.as.featured", System.currentTimeMillis() + this.oneWeekInMS(), RelaxMelodiesApp.getInstance().getApplicationContext());
      this.isBuiltInGuidedMeditationFeatured = true;
    } else if(var1 >= System.currentTimeMillis()) {
      this.isBuiltInGuidedMeditationFeatured = true;
    } else {
      this.isBuiltInGuidedMeditationFeatured = false;
    }
  }

  private void flagMeditationAsClicked(GuidedMeditationSound var1) {
    this.saveToPreferences("clicked.meditations", var1);
  }

  private void flagMeditationAsListenedTo(GuidedMeditationSound var1) {
    this.saveToPreferences("listened.to.meditations", var1);
  }

  public static GuidedMeditationStatus getInstance() {
    if(instance == null) {
      instance = new GuidedMeditationStatus();
    }

    return instance;
  }

  private long oneWeekInMS() {
    return 604800000L;
  }

  private void saveToPreferences(String var1, GuidedMeditationSound var2) {
    String var3 = PersistedDataManager.getString(var1, "", RelaxMelodiesApp.getInstance().getApplicationContext());
    String var4 = var2.getId();
    if(!var3.contains(var4)) {
      PersistedDataManager.saveString(var1, var3 + "," + var4, RelaxMelodiesApp.getInstance().getApplicationContext());
    }

  }

  private void throwExceptionIfNotConfigured() throws RuntimeException {
    if(!this.configured) {
      throw new RuntimeException("GuidedMeditationStatus must be configured before it is used.");
    }
  }

  public void configure() {
    SoundManager.getInstance().registerObserver(this);
    SoundLibrary.getInstance().registerObserver(this);
    this.configureBuiltInFeaturedGuidedMeditation();
    this.configured = true;
  }

  public boolean didAlreadyShowcasedMeditationScroll() {
    return this.alreadyShowcasedMeditationScroll;
  }

  public boolean didClickMeditation(GuidedMeditationSound var1) {
    this.throwExceptionIfNotConfigured();
    return PersistedDataManager.getString("clicked.meditations", "", RelaxMelodiesApp.getInstance().getApplicationContext()).contains(var1.getId());
  }

  public boolean didListenToMeditation(GuidedMeditationSound var1) {
    this.throwExceptionIfNotConfigured();
    return PersistedDataManager.getString("listened.to.meditations", "", RelaxMelodiesApp.getInstance().getApplicationContext()).contains(var1.getId());
  }

  public boolean didScrolled() {
    this.didScrolledInMeditation = PersistedDataManager.getBoolean("didScrolledInMeditation", false, RelaxMelodiesApp.getInstance().getApplicationContext()).booleanValue();
    return this.didScrolledInMeditation;
  }

  public boolean didSeeFreeMeditation(GuidedMeditationSound var1) {
    this.throwExceptionIfNotConfigured();
    return PersistedDataManager.getString("free.seen.meditations", "", RelaxMelodiesApp.getInstance().getApplicationContext()).contains(var1.getId());
  }

  public void flagAlreadyShowcasedMeditationScroll() {
    this.alreadyShowcasedMeditationScroll = true;
  }

  public void flagDidScrolled() {
    if(!this.didScrolledInMeditation) {
      PersistedDataManager.saveBoolean("didScrolledInMeditation", true, RelaxMelodiesApp.getInstance().getApplicationContext());
      this.didScrolledInMeditation = true;
    }

  }

  public void flagMeditationAsSeen(GuidedMeditationSound var1) {
    this.throwExceptionIfNotConfigured();
    this.saveToPreferences("free.seen.meditations", var1);
  }

  public long getLastGuidedClickedTimeStamp() {
    return this.lastGuidedClickedTimeStamp;
  }

  public int getNumberNewlyAddedMeditations() {
    this.throwExceptionIfNotConfigured();
    return PersistedDataManager.getInteger("newly.added.meditations", 0, RelaxMelodiesApp.getInstance().getApplicationContext());
  }

  public boolean isMeditationFeatured(GuidedMeditationSound var1) {
    this.throwExceptionIfNotConfigured();
    return var1.isFeatured() && !this.isBuiltInGuidedMeditationFeatured || var1.isBuiltIn() && this.isBuiltInGuidedMeditationFeatured;
  }

  public boolean isMeditationNew(GuidedMeditationSound var1) {
    this.throwExceptionIfNotConfigured();
    return var1.isNew() && !this.didClickMeditation(var1);
  }

  public void onClearSounds(List<Sound> var1) {
  }

  public void onNewSound(Sound var1) {
    this.checkForNewGuidedMeditation(var1);
  }

  public void onNewSounds(List<Sound> var1) {
    Iterator var2 = var1.iterator();

    while(var2.hasNext()) {
      this.checkForNewGuidedMeditation((Sound)var2.next());
    }

  }

  public void onPausedAllSounds() {
  }

  public void onResumedAllSounds() {
  }

  public void onSelectionChanged(List<Sound> var1, List<Sound> var2) {
  }

  public void onSoundManagerException(String var1, Exception var2) {
  }

  public void onSoundPlayed(Sound var1) {
  }

  public void onSoundStopped(Sound var1, float var2) {
    if(var1 instanceof GuidedMeditationSound) {
      this.flagMeditationAsClicked((GuidedMeditationSound)var1);
      if((double)var2 >= 0.85D) {
        this.flagMeditationAsListenedTo((GuidedMeditationSound)var1);
      }
    }

  }

  public void onSoundUpdated(Sound var1) {
    this.checkForNewGuidedMeditation(var1);
  }

  public void onSoundVolumeChange(String var1, float var2) {
  }

  public void onSoundsUpdated(List<Sound> var1) {
    Iterator var2 = var1.iterator();

    while(var2.hasNext()) {
      this.checkForNewGuidedMeditation((Sound)var2.next());
    }

  }

  public void registerObserver(GuidedMeditationStatusListener var1) {
    this.observers.add(var1);
  }

  public void resetShowcasedMeditationScroll() {
    this.alreadyShowcasedMeditationScroll = false;
  }

  public void setLastGuidedClickedTimeStamp(long var1) {
    this.lastGuidedClickedTimeStamp = var1;
  }

  public void unregisterObserver(GuidedMeditationStatusListener var1) {
    this.observers.remove(var1);
  }

  public void userNavigatedToMeditationTab() {
    this.throwExceptionIfNotConfigured();
    PersistedDataManager.saveInteger("newly.added.meditations", 0, RelaxMelodiesApp.getInstance().getApplicationContext());
    Iterator var1 = this.observers.iterator();

    while(var1.hasNext()) {
      ((GuidedMeditationStatusListener)var1.next()).newlyAddedGuidedMeditation(false);
    }

  }
}
