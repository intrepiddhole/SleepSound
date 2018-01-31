package ipnossoft.rma.media;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.ipnossoft.api.soundlibrary.Sound;
import com.ipnossoft.api.soundlibrary.SoundLibrary;
import com.ipnossoft.api.soundlibrary.sounds.GuidedMeditationSound;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@JsonIgnoreProperties(
        ignoreUnknown = true
)
@JsonTypeInfo(
        include = As.PROPERTY,
        property = "@class",
        use = Id.CLASS
)
public class SoundSelection {
  private List<SoundTrackInfo> trackInfos;

  public SoundSelection() {
    this.trackInfos = new ArrayList();
  }

  public SoundSelection(SoundSelection var1) {
    this();
    this.trackInfos = var1.getTrackInfos();
  }

  public SoundSelection(Collection<SoundTrack> var1) {
    this();
    Iterator var2 = var1.iterator();

    while(var2.hasNext()) {
      this.add((SoundTrack)var2.next());
    }

  }

  private boolean areSoundTrackInfoVolumesEquivalent(SoundTrackInfo var1, SoundTrackInfo var2) {
    return Math.round(var1.getVolume() * 100.0F) == Math.round(var2.getVolume() * 100.0F);
  }

  private boolean soundTrackInfosAreEquivalent(SoundTrackInfo var1, SoundTrackInfo var2) {
    return var1.getSoundId().equals(var2.getSoundId()) && this.areSoundTrackInfoVolumesEquivalent(var1, var2);
  }

  public void add(SoundTrack var1) {
    this.add(new SoundTrackInfo(var1));
  }

  public void add(SoundTrackInfo var1) {
    this.trackInfos.add(var1);
  }

  public GuidedMeditationSound getGuidedMeditationInSelection() {
    Iterator var1 = this.trackInfos.iterator();

    Sound var3;
    do {
      if(!var1.hasNext()) {
        return null;
      }

      SoundTrackInfo var2 = (SoundTrackInfo)var1.next();
      var3 = (Sound)SoundLibrary.getInstance().getSound(var2.getSoundId());
    } while(!var3.getClass().equals(GuidedMeditationSound.class));

    return (GuidedMeditationSound)var3;
  }

  @JsonProperty("trackInfos")
  public List<SoundTrackInfo> getTrackInfos() {
    return this.trackInfos;
  }

  @JsonProperty("trackInfos")
  public void setTrackInfos(List<SoundTrackInfo> var1) {
    this.trackInfos = var1;
  }

  public int size() {
    return this.trackInfos != null?this.trackInfos.size():0;
  }
}
