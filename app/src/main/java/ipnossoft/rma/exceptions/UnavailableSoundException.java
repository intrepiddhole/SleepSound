package ipnossoft.rma.exceptions;

import com.ipnossoft.api.soundlibrary.Sound;

public class UnavailableSoundException extends Exception {
  private Sound sound;

  public UnavailableSoundException(String var1, Sound var2) {
    super(var1);
    this.sound = var2;
  }

  public Sound getSound() {
    return this.sound;
  }
}
