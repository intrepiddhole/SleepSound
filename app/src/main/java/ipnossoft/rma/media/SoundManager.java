package ipnossoft.rma.media;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;

import com.ipnossoft.api.dynamiccontent.DownloadStages;
import com.ipnossoft.api.dynamiccontent.InAppPurchaseNotFoundException;
import com.ipnossoft.api.dynamiccontent.model.InAppPurchase;
import com.ipnossoft.api.featuremanager.FeatureManager;
import com.ipnossoft.api.featuremanager.FeatureManagerListenerResult;
import com.ipnossoft.api.featuremanager.exceptions.FeatureManagerException;
import com.ipnossoft.api.soundlibrary.Sound;
import com.ipnossoft.api.soundlibrary.SoundLibrary;
import com.ipnossoft.api.soundlibrary.sounds.*;
import com.nativemediaplayer.IMediaPlayer;
import ipnossoft.rma.PersistedDataManager;
import ipnossoft.rma.RelaxMelodiesApp;
import ipnossoft.rma.exceptions.SoundLimitReachedException;
import ipnossoft.rma.free.R;
import ipnossoft.rma.guidedmeditations.GuidedMeditationStatus;
import ipnossoft.rma.review.ReviewProcess;
import ipnossoft.rma.ui.dialog.RelaxProgressDialog;
import ipnossoft.rma.util.RelaxAnalytics;
import ipnossoft.rma.util.Utils;

import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

//cavaj
public class SoundManager implements com.nativemediaplayer.IMediaPlayer.OnCompletionListener
{

  private static final String PREMIUM_SOUNDS_IN_APP_PURCHASE_IDENTIFIER = "ipnossoft.rma.free.sounds";
  private static final String TAG = "SoundManager";
  private static SoundManager instance;
  private boolean fadeOutInProgress;
  private NativeMediaPlayerMonitor monitor;
  private Set observers;
  private ConcurrentHashMap tracks;

  public SoundManager()
  {
    tracks = new ConcurrentHashMap();
    observers = new HashSet();
    fadeOutInProgress = false;
  }

  private void downloadFeature(String s, final Context context)
  {
    final FeatureManager featuremanager = FeatureManager.getInstance();
    featuremanager.downloadFeature(s, new FeatureManagerListenerResult() {
      DownloadStages lastStage = DownloadStages.NONE;
      ProgressDialog progressDialog = null;
      RelaxProgressDialog relaxProgressDialog;

      private String describeStage(DownloadStages downloadstages, InAppPurchase inapppurchase) {
        switch (downloadstages) {
          default:
            return context.getString(R.string.download_sound_waiting);

          case CONNECTING: // '\001'
            return context.getString(R.string.download_sound_connecting);

          case DOWNLOADING_ZIP: // '\002'
            return getDownloadMessageFromInAppPurchase(inapppurchase);

          case EXTRACTING_ZIP: // '\003'
            return context.getString(R.string.download_sound_extracting);
        }
      }

      ;

      private String getDownloadMessageFromInAppPurchase(InAppPurchase inapppurchase) {
        if (inapppurchase == null || inapppurchase.getIdentifier().equals("ipnossoft.rma.free.sounds")) {
          return context.getString(R.string.download_sound_premium_sound_package_name);
        } else {
          return (new StringBuilder()).append(context.getString(R.string.download_sound_downloading)).append(" \"").append(inapppurchase.getName()).append("\"").toString();
        }
      }

      public void hideProgressDialog() {
        if (progressDialog != null) {
          progressDialog.hide();
          progressDialog.dismiss();
        }
      }

      public void onCancel() {
        hideProgressDialog();
      }

      public void onFailure(FeatureManagerException featuremanagerexception) {
        hideProgressDialog();
        if (featuremanagerexception == null) {
          Utils.alert(context, context.getString(R.string.download_error_title), context.getString(R.string.download_error_message));
          return;
        }
        if ((featuremanagerexception.getCause() instanceof UnknownHostException) || (featuremanagerexception.getCause() instanceof InAppPurchaseNotFoundException)) {
          Utils.alert(context, context.getString(R.string.download_error_title), context.getString(R.string.download_error_message));
          return;
        } else {
          Utils.alert(context, context.getString(R.string.download_error_title), featuremanagerexception.getLocalizedMessage());
          return;
        }
      }

      public void onProgressChange(double d, int i) {
        DownloadStages downloadstages = DownloadStages.values()[i];
        if (progressDialog != null && downloadstages == lastStage) {
          progressDialog.setProgress((int) d);
        } else {
          progressDialog = showProgressDialog(1, describeStage(downloadstages, null), downloadstages);
        }
        lastStage = downloadstages;
      }

      public void onSuccess(Object obj) {
        onSuccess((String[]) obj);
      }

      public void onSuccess(String as[]) {
        hideProgressDialog();
      }

      public RelaxProgressDialog showProgressDialog(int i, String s, DownloadStages downloadstages) {
        if (relaxProgressDialog != null) {
          relaxProgressDialog.changeTitle(s);
          relaxProgressDialog.setProgress(0);
          if (downloadstages == DownloadStages.EXTRACTING_ZIP) {
            relaxProgressDialog.disableNegativeButton();
          }
        } else {
          RelaxProgressDialog.Builder downloadstages_1 = new ipnossoft.rma.ui.dialog.RelaxProgressDialog.Builder(context, ipnossoft.rma.ui.dialog.RelaxProgressDialog.RelaxDialogButtonOrientation.VERTICAL);
          downloadstages_1.setStyle(i);
          downloadstages_1.setTitle(s);
          downloadstages_1.setProgress(0);
          downloadstages_1.setMaxProgress(100);
          downloadstages_1.setCancelable(false);
          downloadstages_1.setProgressNumberFormat("");
          downloadstages_1.setNegativeButton(context.getString(R.string.cancel), new android.view.View.OnClickListener() {

            public void onClick(View view) {
              /*if (inAppPurchase == null || inAppPurchase.getIdentifier().equals("ipnossoft.rma.free.sounds")) {
                RelaxAnalytics.logCancelDownload("premium_sounds");
              } else {
                RelaxAnalytics.logCancelDownload(inAppPurchase.getIdentifier());
              }*/
              featuremanager.cancelDownloadFeature();
            }
          });
          relaxProgressDialog = downloadstages_1.create();
          relaxProgressDialog.show();
          hideProgressDialog();
        }
        return relaxProgressDialog;
      }
    });
  }

  private void downloadPurchase(Sound sound, Context context)
  {
    if(!getSoundPurchaseId(sound).equals("ipnossoft.rma.free.sounds") && sound.isLockedWithPremiumEnabled(RelaxMelodiesApp.isPremium().booleanValue()) && !FeatureManager.getInstance().hasActiveSubscription())
    {
      Utils.alert(context, context.getString(R.string.download_error_title), context.getString(R.string.service_error_message));
      return;
    } else
    {
      downloadFeature(getSoundPurchaseId(sound), context);
      return;
    }
  }

  private void downloadSound(Sound sound, Activity activity)
  {
    downloadPurchase(sound, activity);
    if(sound instanceof GuidedMeditationSound)
    {
      RelaxAnalytics.logDownload(sound.getId());
      return;
    } else
    {
      RelaxAnalytics.logDownload("premium_sounds");
      return;
    }
  }

  public static SoundManager getInstance()
  {
    if(instance == null)
    {
      instance = new SoundManager();
    }
    return instance;
  }

  private String getSoundPurchaseId(Sound sound)
  {
    String s = sound.getId();
    String sound_1 = s;
    if(s.startsWith("ipnossoft.rma.sounds"))
    {
      sound_1 = "ipnossoft.rma.free.sounds";
    }
    return sound_1;
  }

  private SoundTrack getTrack(Activity activity, Sound sound)
  {
    SoundTrack soundtrack1 = (SoundTrack)tracks.get(sound.getId());
    SoundTrack soundtrack = soundtrack1;
    if(soundtrack1 == null)
    {
      soundtrack = new SoundTrack(activity, sound, 0.5F);
      tracks.put(sound.getId(), soundtrack);
    }
    return soundtrack;
  }

  private SoundTrack getTrack(Activity activity, String s)
  {
    SoundTrack soundtrack1 = (SoundTrack)tracks.get(s);
    SoundTrack soundtrack = soundtrack1;
    if(soundtrack1 == null)
    {
      soundtrack = getTrack(activity, (Sound)SoundLibrary.getInstance().getSound(s));
    }
    return soundtrack;
  }

  private List getTracksPaused()
  {
    return getTracksWithState(SoundState.PAUSED);
  }

  private List getTracksPlaying()
  {
    return getTracksWithState(SoundState.PLAYING);
  }

  private List getTracksWithState(SoundState soundstate)
  {
    ArrayList arraylist = new ArrayList();
    Iterator iterator = tracks.values().iterator();
    do
    {
      if(!iterator.hasNext())
      {
        break;
      }
      SoundTrack soundtrack = (SoundTrack)iterator.next();
      if(soundtrack.getState().equals(soundstate))
      {
        arraylist.add(soundtrack);
      }
    } while(true);
    return arraylist;
  }

  private boolean isBrainwaveSelected()
  {
    for(Iterator iterator = tracks.values().iterator(); iterator.hasNext();)
    {
      SoundTrack soundtrack = (SoundTrack)iterator.next();
      if((soundtrack.getSound() instanceof IsochronicSound) || (soundtrack.getSound() instanceof BinauralSound))
      {
        return true;
      }
    }

    return false;
  }

  private boolean isGuidedAndShouldNotSetItsVolume(boolean flag, SoundTrack soundtrack)
  {
    return (soundtrack.getSound() instanceof GuidedMeditationSound) && !flag;
  }

  private void logNewSoundPressed(SoundManager soundmanager, Sound sound)
  {
    if(soundmanager.isPlaying(sound.getId()))
    {
      RelaxAnalytics.logSoundPlayed(sound);
      return;
    } else
    {
      RelaxAnalytics.logSoundStopped(sound);
      return;
    }
  }

  private void notifyAllSoundPaused()
  {
    for(Iterator iterator = observers.iterator(); iterator.hasNext(); ((SoundManagerObserver)iterator.next()).onPausedAllSounds()) { }
  }

  private void notifyAllSoundResumed()
  {
    for(Iterator iterator = observers.iterator(); iterator.hasNext(); ((SoundManagerObserver)iterator.next()).onResumedAllSounds()) { }
  }

  private void notifyClearSounds(List list)
  {
    for(Iterator iterator = observers.iterator(); iterator.hasNext(); ((SoundManagerObserver)iterator.next()).onClearSounds(list)) { }
  }

  private void notifySelectionChanged(List list)
  {
    for(Iterator iterator = observers.iterator(); iterator.hasNext(); ((SoundManagerObserver)iterator.next()).onSelectionChanged(list, getSelectedSounds())) { }
  }

  private void notifySoundManagerException(String s, Exception exception)
  {
    for(Iterator iterator = observers.iterator(); iterator.hasNext(); ((SoundManagerObserver)iterator.next()).onSoundManagerException(s, exception)) { }
  }

  private void notifySoundPlayed(Sound sound)
  {
    for(Iterator iterator = observers.iterator(); iterator.hasNext(); ((SoundManagerObserver)iterator.next()).onSoundPlayed(sound)) { }
  }

  private void notifySoundStopped(Sound sound, float f)
  {
    for(Iterator iterator = observers.iterator(); iterator.hasNext(); ((SoundManagerObserver)iterator.next()).onSoundStopped(sound, f)) { }
  }

  private void onBeforePlay(Sound sound, boolean flag)
          throws Exception
  {
    if(sound instanceof GuidedMeditationSound)
    {
      stopAllSoundsOfType(GuidedMeditationSound.class, sound);
    } else
    if(((sound instanceof IsochronicSound) || (sound instanceof BinauralSound)) && isBrainwaveSelected())
    {
      SoundVolumeManager.getInstance().preventSubVolumeDismissal();
      stopAllSoundsOfType(IsochronicSound.class, sound);
      stopAllSoundsOfType(BinauralSound.class, sound);
      SoundVolumeManager.getInstance().enableSubVolumeDismissal();
    }
    if(getSelectedTracks().size() >= 10 && !flag)
    {
      throw new SoundLimitReachedException();
    } else
    {
      return;
    }
  }

  private void pause(String s)
  {
    SoundTrack soundtrack;
    try
    {
      soundtrack = (SoundTrack)tracks.get(s);
    }
    catch(Exception exception)
    {
      Log.d("SoundManager", (new StringBuilder()).append("Error while pausing sound ").append(s).toString(), exception);
      return;
    }
    if(soundtrack == null)
    {
      try {
        pauseTrack(soundtrack);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  private void pauseTrack(SoundTrack soundtrack)
          throws Exception
  {
    if(soundtrack.getState() == SoundState.PLAYING)
    {
      SoundService.getInstance().pause(soundtrack);
    }
  }

  private void play(Sound sound, float f, Activity activity, boolean flag, boolean flag1)
          throws Exception
  {
    SoundTrack soundtrack = getTrack(activity, sound);
    if(!isGuidedAndShouldNotSetItsVolume(flag1, soundtrack))
    {
      soundtrack.setVolume(f);
    }
    playTrack(soundtrack, false);
    if(flag)
    {
      showVolumeLayout(activity, sound.getId());
    }
  }

  private void playAllPaused()
  {
    if(isPaused() && tracks.size() > 0)
    {
      for(Iterator iterator = getTracksPaused().iterator(); iterator.hasNext();)
      {
        SoundTrack soundtrack = (SoundTrack)iterator.next();
        try
        {
          playTrack(soundtrack, true);
        }
        catch(Exception exception)
        {
          notifySoundManagerException(soundtrack.getSound().getId(), exception);
        }
      }

    }
  }

  private void playAllPausedWith(String s, Activity activity)
  {
    try
    {
      onBeforePlay((Sound)SoundLibrary.getInstance().getSound(s), false);
      selectSound(s, getVolumeForSoundId(s), activity);
      playAllPaused();
      showVolumeLayout(activity, s);
      notifyAllSoundResumed();
      return;
    }
    // Misplaced declaration of an exception variable
    catch(Exception e)
    {
      notifySoundManagerException(s, e);
    }
  }

  private void playAllPausedWithout(String s)
  {
    stopSound((Sound)SoundLibrary.getInstance().getSound(s));
    playAllPaused();
    notifyAllSoundResumed();
  }

  private void playTrack(SoundTrack soundtrack, boolean flag)
          throws Exception
  {
    onBeforePlay(soundtrack.getSound(), flag);
    SoundService.getInstance().play(soundtrack);
  }

  private void selectSound(String s, float f, Activity activity)
          throws Exception
  {
    Sound sound = (Sound)SoundLibrary.getInstance().getSound(s);
    SoundTrack soundtrack = (SoundTrack)tracks.get(sound.getId());
    SoundTrack s_1 = soundtrack;
    if(soundtrack == null)
    {
      s_1 = new SoundTrack(activity, sound, f);
      tracks.put(sound.getId(), s_1);
    }
    SoundService.getInstance().pause(s_1);
  }

  private float stop(String s)
  {
    float f = -1F;
    SoundTrack s_1 = (SoundTrack)tracks.get(s);
    if(s_1 != null)
    {
      f = stopTrack(s_1);
      SoundVolumeManager.getInstance().hideVolumeLayoutInternal(s_1);
    }
    return f;
  }

  private void stopAll()
  {
    for(Iterator iterator = tracks.keySet().iterator(); iterator.hasNext(); stop((String)iterator.next())) { }
  }

  private void stopAllSoundsOfType(Class class1, Sound sound)
  {
    Iterator iterator = tracks.values().iterator();
    do
    {
      if(!iterator.hasNext())
      {
        break;
      }
      SoundTrack soundtrack = (SoundTrack)iterator.next();
      if(soundtrack.getSound().getClass().equals(class1) && (sound == null || !sound.getId().equals(soundtrack.getSound().getId())) && soundtrack.getState() != SoundState.STOPPED)
      {
        stopSound(soundtrack.getSound());
      }
    } while(true);
  }

  private float stopTrack(SoundTrack soundtrack)
  {
    float f = -1F;
    if(trackHasProgress(soundtrack))
    {
      f = (float)soundtrack.getMediaPlayer().getCurrentPosition() / (float)soundtrack.getMediaPlayer().getDuration();
    }
    SoundService.getInstance().stop(soundtrack);
    return f;
  }

  private boolean togglePlay(Sound sound, Activity activity)
  {
    return togglePlay(sound.getId(), activity);
  }

  private boolean togglePlay(String s, Activity activity)
  {
    SoundTrack soundtrack = (SoundTrack)tracks.get(s);
    if(soundtrack == null || !soundtrack.isPlaying())
    {
      play(s, getVolumeForSoundId(s), activity, true);
      return true;
    }
    if(soundtrack.getSound() instanceof GuidedMeditationSound)
    {
      pause(s);
    } else
    {
      stopSound(soundtrack.getSound());
    }
    return false;
  }

  private boolean trackHasProgress(SoundTrack soundtrack)
  {
    return soundtrack != null && soundtrack.getState() == SoundState.PLAYING && soundtrack.getMediaPlayer() != null && !soundtrack.getMediaPlayer().isLooping();
  }

  public void clearAll()
  {
    List list = getSelectedSounds();
    stopAll();
    notifyClearSounds(list);
  }

  public void configureSoundManager(Context context)
  {
    monitor = new NativeMediaPlayerMonitor(context);
    monitor.start();
  }

  public void downloadSounds(Context context)
  {
    downloadFeature("ipnossoft.rma.free.sounds", context);
  }

  public void flushAllPlayers()
  {
    for(Iterator iterator = tracks.values().iterator(); iterator.hasNext(); ((SoundTrack)iterator.next()).disposePlayer()) { }
  }

  public void forcePauseAll()
  {
    if(tracks != null && tracks.size() > 0)
    {
      for(Iterator iterator = tracks.keySet().iterator(); iterator.hasNext(); pause((String)iterator.next())) { }
      notifyAllSoundPaused();
    }
  }

  public int getNbSelectedSounds()
  {
    return getTracksPlaying().size() + getTracksPaused().size();
  }

  public int getProgressOfSound(Sound sound)
  {
    if(sound != null)
    {
      SoundTrack sound_1 = (SoundTrack)tracks.get(sound.getId());
      if(sound_1 != null && sound_1.getMediaPlayer() != null && (sound_1.isPlaying() || sound_1.isPaused()))
      {
        return sound_1.getMediaPlayer().getCurrentPosition();
      }
    }
    return 0;
  }

  public List getSelectedSounds()
  {
    ArrayList arraylist = new ArrayList();
    for(Iterator iterator = getSelectedTracks().iterator(); iterator.hasNext(); arraylist.add(((SoundTrack)iterator.next()).getSound())) { }
    return arraylist;
  }

  public List getSelectedTracks()
  {
    ArrayList arraylist = new ArrayList();
    arraylist.addAll(getTracksPlaying());
    arraylist.addAll(getTracksPaused());
    return arraylist;
  }

  public SoundSelection getSoundSelection()
  {
    List list = getTracksPlaying();
    List list1 = getTracksPaused();
    ArrayList arraylist = new ArrayList(list1.size() + list.size());
    arraylist.addAll(list1);
    arraylist.addAll(list);
    return new SoundSelection(arraylist);
  }

  public SoundState getSoundState(String s)
  {
    SoundTrack s_1 = (SoundTrack)tracks.get(s);
    if(s_1 != null)
    {
      return s_1.getState();
    } else
    {
      return SoundState.STOPPED;
    }
  }

  public float getVolumeForSoundId(String s)
  {
    float f = 0.5F;
    SoundTrack soundtrack = (SoundTrack)tracks.get(s);
    if((Sound)SoundLibrary.getInstance().getSound(s) instanceof GuidedMeditationSound)
    {
      f = PersistedDataManager.getFloat("preference_key_current_guided_meditation_volume", 0.5F, RelaxMelodiesApp.getInstance().getApplicationContext());
    } else
    if(soundtrack != null)
    {
      return soundtrack.getVolume();
    }
    return f;
  }

  public boolean handleSoundPressed(Sound sound, Activity activity)
  {
    boolean flag = false;
    if(isSoundReadyToBeDownloaded(sound))
    {
      downloadSound(sound, activity);
      return false;
    }
    if(!isLocked(sound))
    {
      SoundManager soundmanager = getInstance();
      if(soundmanager.isPaused() && soundmanager.isSelected(sound.getId()) && !(sound instanceof GuidedMeditationSound))
      {
        soundmanager.playAllPausedWithout(sound.getId());
      } else
      if(soundmanager.isPaused() && (!soundmanager.isSelected(sound.getId()) || (sound instanceof GuidedMeditationSound)))
      {
        soundmanager.playAllPausedWith(sound.getId(), activity);
      } else
      {
        soundmanager.togglePlay(sound, activity);
        flag = true;
      }
      ReviewProcess.getInstance().listenedToSound(sound);
      logNewSoundPressed(soundmanager, sound);
      return flag;
    } else
    {
      Log.e(getClass().getSimpleName(), (new StringBuilder()).append("Unable to play sound ").append(sound.getId()).append(" sound file is missing or sound is expired.").toString());
      return false;
    }
  }

  public boolean hasGuidedMeditationSelected()
  {
    return selectedGuidedMeditation() != null;
  }

  public boolean isLocked(Sound sound)
  {
    boolean flag;
    if(sound == null || sound.isLockedWithPremiumEnabled(RelaxMelodiesApp.isPremium().booleanValue()))
    {
      flag = true;
    } else
    {
      flag = false;
    }
    if(sound instanceof GuidedMeditationSound)
    {
      return flag && !GuidedMeditationStatus.getInstance().didSeeFreeMeditation((GuidedMeditationSound)sound);
    } else
    {
      return flag;
    }
  }

  public boolean isPaused()
  {
    return !getTracksPaused().isEmpty();
  }

  public boolean isPlaying()
  {
    return !getTracksPlaying().isEmpty();
  }

  public boolean isPlaying(String s)
  {
    SoundTrack s_1 = (SoundTrack)tracks.get(s);
    return s_1 != null && s_1.isPlaying();
  }

  public boolean isSelected(String s)
  {
    SoundTrack s_1 = (SoundTrack)tracks.get(s);
    return s_1 != null && s_1.getState() != SoundState.STOPPED;
  }

  public boolean isSoundAGuidedMeditation(String s)
  {
    return ((SoundTrack)tracks.get(s)).getSound() instanceof GuidedMeditationSound;
  }

  public boolean isSoundReadyToBeDownloaded(Sound sound)
  {
    return !isLocked(sound) && sound.getMediaResourceId() == 0 && !sound.isDownloaded();
  }

  void notifySoundVolumeChange(String s, float f)
  {
    if(!fadeOutInProgress)
    {
      for(Iterator iterator = observers.iterator(); iterator.hasNext(); ((SoundManagerObserver)iterator.next()).onSoundVolumeChange(s, f)) { }
    }
  }

  public void onCompletion(IMediaPlayer imediaplayer)
  {
    Iterator iterator = tracks.values().iterator();
    do
    {
      if(!iterator.hasNext())
      {
        break;
      }
      SoundTrack soundtrack = (SoundTrack)iterator.next();
      if(imediaplayer.equals(soundtrack.getMediaPlayer()))
      {
        stopSound(soundtrack.getSound());
      }
    } while(true);
  }

  public void pauseAll(boolean flag)
  {
    if(isPlaying() && tracks.size() > 0)
    {
      for(Iterator iterator = tracks.keySet().iterator(); iterator.hasNext(); pause((String)iterator.next())) { }
      notifyAllSoundPaused();
    }
  }

  public void play(String s, float f, Activity activity, boolean flag)
  {
    if(s == null)
      return;
    Sound s_1 = (Sound) SoundLibrary.getInstance().getSound(s);
    try {
      play(s_1, f, activity, flag, true);
      notifySoundPlayed(s_1);
    }catch (Exception e) {
      String s_2;
      if(s_1 != null)
        s_2 = s_1.getId();
      else
        s_2 = null;
      notifySoundManagerException(String.valueOf(s_2), e);
    }
  }

  public void playAll()
  {
    playAllPaused();
    notifyAllSoundResumed();
  }

  public void playSoundSelection(SoundSelection soundselection, Activity activity)
  {
    List list = getSelectedSounds();
    stopAll();
    for(Iterator soundselection_1 = soundselection.getTrackInfos().iterator(); soundselection_1.hasNext();)
    {
      SoundTrackInfo soundtrackinfo = (SoundTrackInfo)soundselection_1.next();
      Sound sound = (Sound)SoundLibrary.getInstance().getSound(soundtrackinfo.getSoundId());
      try
      {
        play(sound, soundtrackinfo.getVolume(), activity, false, false);
      }
      catch(Exception exception)
      {
        notifySoundManagerException(sound.getId(), exception);
      }
    }

    notifySelectionChanged(list);
  }

  public void registerObserver(SoundManagerObserver soundmanagerobserver)
  {
    observers.add(soundmanagerobserver);
  }

  public void resetVolumes()
  {
    PersistedDataManager.saveFloat("preference_key_current_guided_meditation_volume", 0.5F, RelaxMelodiesApp.getInstance().getApplicationContext());
    for(Iterator iterator = tracks.values().iterator(); iterator.hasNext(); ((SoundTrack)iterator.next()).resetVolume()) { }
  }

  public void restoreSoundSelection(SoundSelection soundselection, Activity activity)
  {
    for(Iterator soundselection_1 = soundselection.getTrackInfos().iterator(); soundselection_1.hasNext();)
    {
      SoundTrackInfo soundtrackinfo = (SoundTrackInfo)soundselection_1.next();
      Log.d("SoundManager", (new StringBuilder()).append("Restoring sound selection sound: ").append(soundtrackinfo.getSoundId()).append(" saved state: ").append(soundtrackinfo.getSoundState()).toString());
      try
      {
        selectSound(soundtrackinfo.getSoundId(), soundtrackinfo.getVolume(), activity);
      }
      catch(Exception exception)
      {
        Log.e("SoundManager", (new StringBuilder()).append("Failed to select sound with id ").append(soundtrackinfo.getSoundId()).toString(), exception);
      }
    }

    notifySelectionChanged(new ArrayList());
  }

  public SoundTrack selectedGuidedMeditation()
  {
    for(Iterator iterator = getSelectedTracks().iterator(); iterator.hasNext();)
    {
      SoundTrack soundtrack = (SoundTrack)iterator.next();
      if(soundtrack.getSound() instanceof GuidedMeditationSound)
      {
        return soundtrack;
      }
    }

    return null;
  }

  public void setFadedVolume(float f)
  {
    Iterator iterator = tracks.values().iterator();
    do
    {
      if(!iterator.hasNext())
      {
        break;
      }
      SoundTrack soundtrack = (SoundTrack)iterator.next();
      if(soundtrack.isPlaying())
      {
        soundtrack.setPercentageMarkedVolume(f);
      }
    } while(true);
  }

  public void showVolumeLayout(Activity activity, String s)
  {
    SoundTrack activity_1 = getTrack(activity, s);
    if(!(activity_1.getSound() instanceof GuidedMeditationSound))
    {
      SoundVolumeManager.getInstance().showVolumeLayout(activity_1);
    }
  }

  public void startFadeout()
  {
    fadeOutInProgress = true;
    Iterator iterator = tracks.values().iterator();
    do
    {
      if(!iterator.hasNext())
      {
        break;
      }
      SoundTrack soundtrack = (SoundTrack)iterator.next();
      if(soundtrack.isPlaying())
      {
        soundtrack.markCurrentVolume();
      }
    } while(true);
  }

  public void stopFadeout()
  {
    if(fadeOutInProgress)
    {
      fadeOutInProgress = false;
      for(Iterator iterator = tracks.values().iterator(); iterator.hasNext(); ((SoundTrack)iterator.next()).restoreMarkedVolume()) { }
    }
  }

  public void stopSound(Sound sound)
  {
    notifySoundStopped(sound, stop(sound.getId()));
  }

  public boolean togglePlayPauseAll(boolean flag)
  {
    if(flag)
    {
      playAll();
      return true;
    } else
    {
      pauseAll(true);
      return false;
    }
  }

  public void unregisterObserver(SoundManagerObserver soundmanagerobserver)
  {
    observers.remove(soundmanagerobserver);
  }



}
