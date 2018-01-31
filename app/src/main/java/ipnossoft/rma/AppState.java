package ipnossoft.rma;

import com.ipnossoft.api.soundlibrary.Sound;
import com.ipnossoft.api.soundlibrary.sounds.BinauralSound;
import com.ipnossoft.api.soundlibrary.sounds.GuidedMeditationSound;
import com.ipnossoft.api.soundlibrary.sounds.IsochronicSound;
import java.util.Iterator;
import java.util.List;

public class AppState {
  private static String lastScreen = "";
  private static int numberOfBrainwavesSelected = 0;
  private static int numberOfMeditationsSelected = 0;
  private static int numberOfSoundsSelected = 0;
  private static boolean playing = false;
  private static boolean timerRunning = false;

  public AppState() {
  }

  public static String getLastScreen() {
    return lastScreen;
  }

  public static int getNumberOfBrainwavesSelected() {
    return numberOfBrainwavesSelected;
  }

  public static int getNumberOfMeditationsSelected() {
    return numberOfMeditationsSelected;
  }

  public static int getNumberOfSoundsSelected() {
    return numberOfSoundsSelected;
  }

  public static boolean isPlaying() {
    return playing;
  }

  public static boolean isTimerRunning() {
    return timerRunning;
  }

  public static void setLastScreen(String var0) {
    lastScreen = var0;
  }

  public static void setNumberOfBrainwavesSelected(int var0) {
    numberOfBrainwavesSelected = var0;
  }

  public static void setNumberOfMeditationsSelected(int var0) {
    numberOfMeditationsSelected = var0;
  }

  public static void setNumberOfSoundsSelected(int var0) {
    numberOfSoundsSelected = var0;
  }

  public static void setPlaying(boolean var0) {
    playing = var0;
  }

  public static void setSoundSelection(List<Sound> var0) {
    int var2 = 0;
    int var4 = 0;
    int var3 = 0;
    Iterator var5 = var0.iterator();

    while(true) {
      while(var5.hasNext()) {
        Sound var1 = (Sound)var5.next();
        if(var1 instanceof GuidedMeditationSound) {
          ++var3;
        } else if(!(var1 instanceof IsochronicSound) && !(var1 instanceof BinauralSound)) {
          ++var2;
        } else {
          ++var4;
        }
      }

      setNumberOfBrainwavesSelected(var4);
      setNumberOfSoundsSelected(var2);
      setNumberOfMeditationsSelected(var3);
      return;
    }
  }

  public static void setTimerRunning(boolean var0) {
    timerRunning = var0;
  }
}
