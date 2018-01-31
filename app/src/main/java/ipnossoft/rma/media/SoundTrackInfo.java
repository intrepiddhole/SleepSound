package ipnossoft.rma.media;

import android.content.Context;
import com.ipnossoft.api.soundlibrary.Sound;
import com.ipnossoft.api.soundlibrary.SoundLibrary;
import com.ipnossoft.api.soundlibrary.sounds.SoundState;

// Referenced classes of package ipnossoft.rma.media:
//            SoundTrack


//cavaj
public class SoundTrackInfo
{

  private String soundId;
  private SoundState soundState;
  private float volume;

  public SoundTrackInfo()
  {
  }

  public SoundTrackInfo(SoundTrack soundtrack)
  {
    this(soundtrack.getSound().getId(), soundtrack.getVolume());
    soundState = soundtrack.getState();
  }

  public SoundTrackInfo(String s, float f)
  {
    soundId = s;
    volume = f;
    soundState = SoundState.PLAYING;
  }

  public String getSoundId()
  {
    return soundId;
  }

  public SoundState getSoundState()
  {
    return soundState;
  }

  public float getVolume()
  {
    return volume;
  }

  public void setSoundId(String s)
  {
    soundId = s;
  }

  public void setSoundState(SoundState soundstate)
  {
    soundState = soundstate;
  }

  public void setVolume(float f)
  {
    volume = f;
  }

  public SoundTrack toSoundTrack(Context context)
  {
    if(soundId != null){
      Object obj;
      obj = (Sound)SoundLibrary.getInstance().getSound(soundId);
      float f;
      if(volume != 0.0F)
      {
        f = volume;
      }
      else{
        f = 0.5F;
      }
      try
      {
        SoundTrack obj_1 = new SoundTrack(context, ((Sound) (obj)), f);
        if(!soundState.equals(SoundState.PAUSED)){
          return obj_1;
        }
        obj_1.pause();
        return obj_1;
      }
      catch(Exception e)
      {
        return null;
      }
    }
    return null;
  }
}
