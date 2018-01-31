package ipnossoft.rma.media;

import com.ipnossoft.api.soundlibrary.Sound;
import java.util.List;

public abstract interface SoundManagerObserver
{
  public abstract void onClearSounds(List<Sound> paramList);
  
  public abstract void onPausedAllSounds();
  
  public abstract void onResumedAllSounds();
  
  public abstract void onSelectionChanged(List<Sound> paramList1, List<Sound> paramList2);
  
  public abstract void onSoundManagerException(String paramString, Exception paramException);
  
  public abstract void onSoundPlayed(Sound paramSound);
  
  public abstract void onSoundStopped(Sound paramSound, float paramFloat);
  
  public abstract void onSoundVolumeChange(String paramString, float paramFloat);
}


/* Location:           D:\ProcessingWork\2018.01.15\SleepSound\classes-dex2jar.jar
 * Qualified Name:     ipnossoft.rma.media.SoundManagerObserver
 * JD-Core Version:    0.7.0.1
 */