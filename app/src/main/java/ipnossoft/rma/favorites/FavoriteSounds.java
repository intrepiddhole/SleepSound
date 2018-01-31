package ipnossoft.rma.favorites;

import android.support.annotation.NonNull;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import ipnossoft.rma.media.SoundSelection;
import ipnossoft.rma.media.SoundTrack;
import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(
        ignoreUnknown = true
)
@JsonTypeInfo(
        include = As.PROPERTY,
        property = "@class",
        use = Id.CLASS
)
public class FavoriteSounds extends SoundSelection implements Comparable<FavoriteSounds> {
  @Deprecated
  public static final String FIELD_JSON_LABEL = "label";
  @Deprecated
  public static final String FIELD_JSON_SOUNDS = "sounds";
  private long id;
  private String imageResourceEntryName;
  private String label;
  private String selectedImageResourceEntryName;

  public FavoriteSounds() {
  }

  public FavoriteSounds(int var1, String var2, List<SoundTrack> var3) {
    super(var3);
    this.id = (long)var1;
    this.label = var2;
  }

  public FavoriteSounds(int var1, String var2, List<SoundTrack> var3, String var4, String var5) {
    this(var1, var2, var3);
    this.imageResourceEntryName = var4;
    this.selectedImageResourceEntryName = var5;
  }

  public FavoriteSounds(SoundSelection var1) {
    super(var1);
    this.id = (new Date()).getTime();
  }

  public FavoriteSounds(SoundSelection var1, String var2) {
    this(var1);
    this.label = var2;
  }

  public FavoriteSounds(SoundSelection var1, String var2, String var3) {
    super(var1);
    this.imageResourceEntryName = var2;
    this.selectedImageResourceEntryName = var3;
  }

  public FavoriteSounds(SoundSelection var1, String var2, String var3, String var4) {
    this(var1, var2);
    this.imageResourceEntryName = var3;
    this.selectedImageResourceEntryName = var4;
  }

  public FavoriteSounds(String var1, String var2) {
    this.imageResourceEntryName = var1;
    this.selectedImageResourceEntryName = var2;
  }

  public int compareTo(@NonNull FavoriteSounds var1) {
    return Long.valueOf(this.id).compareTo(Long.valueOf(var1.id));
  }

  @JsonProperty("id")
  public long getId() {
    return this.id;
  }

  @JsonProperty("imageResourceEntryName")
  public String getImageResourceEntryName() {
    return this.imageResourceEntryName;
  }

  @JsonProperty("label")
  public String getLabel() {
    return this.label;
  }

  @JsonProperty("selectedImageResourceEntryName")
  public String getSelectedImageResourceEntryName() {
    return this.selectedImageResourceEntryName;
  }

  @JsonProperty("id")
  public void setId(long var1) {
    this.id = var1;
  }

  @JsonProperty("imageResourceEntryName")
  public void setImageResourceEntryName(String var1) {
    this.imageResourceEntryName = var1;
  }

  @JsonProperty("label")
  public void setLabel(String var1) {
    this.label = var1;
  }

  @JsonProperty("selectedImageResourceEntryName")
  public void setSelectedImageResourceEntryName(String var1) {
    this.selectedImageResourceEntryName = var1;
  }
}
