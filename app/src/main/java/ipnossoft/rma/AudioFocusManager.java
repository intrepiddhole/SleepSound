package ipnossoft.rma;

import android.content.Context;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.os.Handler;
import ipnossoft.rma.media.SoundManager;

public class AudioFocusManager {
  public static final String AUDIO_FOCUS_PREFERENCE_DEFAULT_VALUE = "1";
  public static final String AUDIO_FOCUS_PREFERENCE_DISABLED_VALUE = "0";
  public static final String AUDIO_FOCUS_PREFERENCE_ENABLED_VALUE = "1";
  public static final String AUDIO_FOCUS_PREFERENCE_KEY = "useAudioFocus";
  private static Context context;
  private static Handler handler = new Handler();
  private static boolean hasAudioFocus;
  private static OnAudioFocusChangeListener onAudioFocusChangeListener = new OnAudioFocusChangeListener(){
    public void onAudioFocusChange(int var1) {
      if(var1 == -2) {
        if(!SoundManager.getInstance().isPaused()) {
          AudioFocusManager.resumeOnAudioFocus = true;
          SoundManager.getInstance().pauseAll(false);
        }
      } else if(var1 == 1) {
        if(AudioFocusManager.resumeOnAudioFocus) {
          AudioFocusManager.resumeOnAudioFocus = false;
          AudioFocusManager.handler.postDelayed(new Runnable(){
            public void run() {
              if(SoundManager.getInstance().isPaused() && SoundManager.getInstance().getNbSelectedSounds() > 0) {
                SoundManager.getInstance().playAll();
              }
            }
          }, 500L);
          return;
        }
      } else if(var1 == -1 && AudioFocusManager.isAudioFocusEnabled()) {
        AudioManager var2 = (AudioManager)AudioFocusManager.context.getSystemService(Context.AUDIO_SERVICE);
        if(var2 != null) {
          var2.abandonAudioFocus(AudioFocusManager.onAudioFocusChangeListener);
        }

        AudioFocusManager.resumeOnAudioFocus = false;
        SoundManager.getInstance().pauseAll(false);
        return;
      }

    }
  };

  private static boolean resumeOnAudioFocus;

  public AudioFocusManager() {
  }

  public static void cancelAudioFocus() {
    if(hasAudioFocus && !resumeOnAudioFocus) {
      ((AudioManager)getContext().getSystemService(Context.AUDIO_SERVICE)).abandonAudioFocus(onAudioFocusChangeListener);
      hasAudioFocus = false;
    }
  }

  public static String getAudioFocusValue() {
    return PersistedDataManager.getString("useAudioFocus", "1", RelaxMelodiesApp.getInstance().getApplicationContext());
  }

  private static Context getContext() {
    if(context == null) {
      context = RelaxMelodiesApp.getInstance().getApplicationContext();
    }

    return context;
  }

  public static boolean hasAudioFocusSetting() {
    return PersistedDataManager.getString("useAudioFocus", (String)null, RelaxMelodiesApp.getInstance().getApplicationContext()) != null;
  }

  public static boolean isAudioFocusEnabled() {
    return PersistedDataManager.getString("useAudioFocus", "1", getContext()).equals("1");
  }

  public static boolean requestAudioFocus() {
    boolean var0 = true;
    if(hasAudioFocus) {
      return true;
    } else {
      if(((AudioManager)getContext().getSystemService(Context.AUDIO_SERVICE)).requestAudioFocus(onAudioFocusChangeListener, 3, 1) != 1) {
        var0 = false;
      }

      hasAudioFocus = var0;
      return hasAudioFocus;
    }
  }

  public static void setAudioFocusEnabled(String var0) {
    PersistedDataManager.saveString("useAudioFocus", var0, RelaxMelodiesApp.getInstance().getApplicationContext());
    if(SoundManager.getInstance().isPlaying()) {
      requestAudioFocus();
    }

  }
}
