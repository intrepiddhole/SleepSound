package ipnossoft.rma.favorites;

import ipnossoft.rma.media.SoundSelection;

public class StaffFavoriteDTO {
  private String imageResourceEntryName;
  private String labelResourceId;
  private String selectedImageResourceEntryName;
  private SoundSelection soundSelection;

  public StaffFavoriteDTO() {
  }

  public String getImageResourceEntryName() {
    return this.imageResourceEntryName;
  }

  public String getLabelResourceId() {
    return this.labelResourceId;
  }

  public String getSelectedImageResourceEntryName() {
    return this.selectedImageResourceEntryName;
  }

  public SoundSelection getSoundSelection() {
    return this.soundSelection;
  }

  public void setImageResourceEntryName(String var1) {
    this.selectedImageResourceEntryName = var1;
  }

  public void setLabelResourceId(String var1) {
    this.labelResourceId = var1;
  }

  public void setSelectedImageResourceEntryName(String var1) {
    this.selectedImageResourceEntryName = var1;
  }

  public void setSoundSelection(SoundSelection var1) {
    this.soundSelection = var1;
  }
}
