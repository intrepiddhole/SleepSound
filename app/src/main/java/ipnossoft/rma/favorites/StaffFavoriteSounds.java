package ipnossoft.rma.favorites;

import ipnossoft.rma.media.SoundSelection;
import ipnossoft.rma.media.SoundTrack;
import java.util.List;

public class StaffFavoriteSounds extends FavoriteSounds {
  private String identifier;

  public StaffFavoriteSounds() {
  }

  public StaffFavoriteSounds(int var1, String var2, List<SoundTrack> var3) {
    super(var1, var2, var3);
  }

  public StaffFavoriteSounds(int var1, String var2, List<SoundTrack> var3, String var4, String var5) {
    super(var1, var2, var3, var4, var5);
  }

  public StaffFavoriteSounds(SoundSelection var1) {
    super(var1);
  }

  public StaffFavoriteSounds(SoundSelection var1, String var2) {
    super(var1, var2);
  }

  public StaffFavoriteSounds(SoundSelection var1, String var2, String var3) {
    super(var1, var2, var3);
  }

  public StaffFavoriteSounds(SoundSelection var1, String var2, String var3, String var4) {
    super(var1, var2, var3, var4);
  }

  public StaffFavoriteSounds(String var1, String var2) {
    super(var1, var2);
  }

  public String getIdentifier() {
    return this.identifier;
  }

  public void setIdentifier(String var1) {
    this.identifier = var1;
  }
}
