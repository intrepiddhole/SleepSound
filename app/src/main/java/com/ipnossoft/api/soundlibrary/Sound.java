package com.ipnossoft.api.soundlibrary;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.ArrayRes;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.ipnossoft.api.dynamiccontent.model.DynamicContentTag;
import com.ipnossoft.api.dynamiccontent.model.InAppPurchase;
import com.ipnossoft.api.featuremanager.FeatureManager;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.onepf.oms.appstore.googleUtils.Purchase;

@JsonIgnoreProperties(
        ignoreUnknown = true
)
@JsonTypeInfo(
        include = As.PROPERTY,
        property = "@class",
        use = Id.CLASS
)
public class Sound implements Parcelable, Cloneable {
  public static final Creator<Sound> CREATOR = new Creator<Sound>(){
    public Sound createFromParcel(Parcel var1) {
      try {
        Sound var3 = new Sound(var1);
        return var3;
      } catch (Exception var2) {
        return null;
      }
    }

    public Sound[] newArray(int var1) {
      return new Sound[var1];
    }
  };
  protected static Context currentContext;
  private static AtomicInteger nextSoundId = new AtomicInteger(0);
  private String audioDuration;
  private boolean builtIn;
  private String description;
  private int favoriteImageResourceId;
  private String filePath;
  @JsonProperty("free")
  private boolean free;
  private String id;
  private String imageLargeUri;
  private int imageResourceId;
  private String imageUri;
  private int infoNormalImageResourceId;
  private int infoSelectedImageResourceId;
  private boolean isFeatured;
  @JsonProperty("new")
  private boolean isNew;
  private int labelResourceId;
  private int mediaResourceId;
  private String name;
  private int normalImageResourceId;
  private int order;
  private String preview;
  private String previewFilePath;
  private String purchaseId;
  private int selectedImageResourceId;
  private int soundId;
  private String tag;
  private String tagColor;
  private String tagId;
  private String tagImageUrl;
  private int tagOrder;

  public Sound() {
    this.soundId = -1;
  }

  public Sound(Parcel var1) throws Exception {
    this(var1.readString(), var1.readString(), var1.readString(), var1.readString(), var1.readString(), var1.readInt() == 1, var1.readString(), var1.readString(), var1.readInt(), var1.readInt(), var1.readInt(), var1.readString(), var1.readString(), var1.readString(), var1.readString(), var1.readString(), var1.readInt(), var1.readInt(), var1.readString(), var1.readInt());
    this.readFromParcelExtra(var1);
  }

  public Sound(String var1, int var2, int var3, int var4, String var5, int var6, boolean var7) {
    this.soundId = -1;
    this.setId(var1);
    this.setSoundId(var2);
    this.setSelectedImageResourceId(var3);
    this.setNormalImageResourceId(var4);
    this.setName(var5);
    this.setMediaResourceId(var6);
    this.setBuiltIn(var7);
    this.setIsFree(var7);
  }

  public Sound(String var1, int var2, int var3, String var4, int var5, boolean var6) {
    this.soundId = -1;
    this.setId(var1);
    this.setSoundId(var2);
    this.setImageResourceId(var3);
    this.setName(var4);
    this.setMediaResourceId(var5);
    this.setBuiltIn(var6);
    this.setIsFree(var6);
  }

  public Sound(String var1, String var2, String var3, String var4, String var5, boolean var6, String var7, String var8, int var9, int var10, int var11) {
    this.soundId = -1;
    this.id = var1;
    this.name = var2;
    this.description = var3;
    this.preview = var4;
    this.imageUri = var5;
    this.builtIn = var6;
    this.filePath = var7;
    this.previewFilePath = var8;
    this.imageResourceId = var11;
    this.soundId = var9;
    this.mediaResourceId = var10;
  }

  Sound(String var1, String var2, String var3, String var4, String var5, boolean var6, String var7, String var8, int var9, int var10, int var11, String var12, String var13, String var14, String var15, String var16, int var17, int var18, String var19, int var20) {
    this(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
    this.imageLargeUri = var12;
    this.tagId = var13;
    this.tag = var14;
    this.tagColor = var15;
    this.tagImageUrl = var16;
    this.tagOrder = var17;
    this.order = var18;
    this.purchaseId = var19;
    if(var20 == 1) {
      var6 = true;
    } else {
      var6 = false;
    }

    this.isNew = var6;
  }

  @JsonIgnore
  private boolean fileExists(String var1) {
    return var1 != null && var1.equals("test_file_path")?true:(new File(var1)).exists();
  }

  private String getImageForTag(DynamicContentTag var1) {
    boolean var2 = false;
    if(currentContext != null) {
      if((double)currentContext.getResources().getDisplayMetrics().scaledDensity > 1.5D) {
        var2 = true;
      } else {
        var2 = false;
      }
    }

    return var2 && var1.getImageHD() != null?var1.getImageHD():var1.getImageSD();
  }

  @SuppressLint("ResourceType")
  public static <T extends Sound> List<T> loadSoundsFromResourceArray(Context var0, @ArrayRes int var1) {
    currentContext = var0;
    TypedArray var2 = var0.getResources().obtainTypedArray(var1);
    ArrayList var3 = new ArrayList();
    var1 = 0;

    while(var1 < var2.length()) {
      int var7 = var1 + 1;
      String var4 = var2.getString(var1);
      var1 = var7 + 1;
      var7 = var2.getInt(var7, 0);
      int var9 = var1 + 1;
      int var8 = var2.getResourceId(var1, 0);
      var1 = var9 + 1;
      var9 = var2.getResourceId(var9, 0);
      int var10 = var1 + 1;
      int var11 = var2.getResourceId(var1, 0);
      var1 = var10 + 1;
      var10 = var2.getResourceId(var10, 0);
      String var5 = var0.getResources().getString(var11);
      boolean var6;
      if(var10 != 0) {
        var6 = true;
      } else {
        var6 = false;
      }

      Sound var12 = new Sound(var4, var7, var8, var9, var5, var10, var6);
      var12.setLabelResourceId(var11);
      var3.add(var12);
    }

    var2.recycle();
    return var3;
  }

  protected boolean areDifferent(Object var1, Object var2) {
    return var1 != null && var2 == null || var1 == null && var2 != null || var1 != null && !var1.equals(var2);
  }

  @JsonIgnore
  public int describeContents() {
    return 0;
  }

  public boolean doesPremiumUnlockSound() {
    return true;
  }

  public String getAudioDuration() {
    return this.audioDuration;
  }

  @JsonProperty("description")
  public String getDescription() {
    return this.description;
  }

  @JsonProperty("favoriteImageResourceId")
  public int getFavoriteImageResourceId() {
    return this.favoriteImageResourceId;
  }

  @JsonIgnore
  public String getFilePath() {
    if(this.filePath == null && this.mediaResourceId == 0 && currentContext != null && this.soundId >= 0) {
      this.filePath = currentContext.getFilesDir().getPath() + "/downloads/sound" + this.soundId + ".ogg";
    }

    return this.filePath;
  }

  @JsonProperty("id")
  public String getId() {
    return this.id;
  }

  @JsonProperty("imageLarge")
  public String getImageLargeUri() {
    return this.imageLargeUri;
  }

  @JsonProperty("imageResourceId")
  public int getImageResourceId() {
    return this.imageResourceId;
  }

  @JsonProperty("image")
  public String getImageUri() {
    return this.imageUri;
  }

  @JsonProperty("infoNormalImageResourceId")
  public int getInfoNormalImageResourceId() {
    return this.infoNormalImageResourceId;
  }

  @JsonProperty("infoSelectedImageResourceId")
  public int getInfoSelectedImageResourceId() {
    return this.infoSelectedImageResourceId;
  }

  public int getLabelResourceId() {
    return this.labelResourceId;
  }

  @JsonProperty("mediaResourceId")
  public int getMediaResourceId() {
    return this.mediaResourceId;
  }

  @JsonProperty("name")
  public String getName() {
    return this.name == null?"":this.name;
  }

  @JsonProperty("normalImageResourceId")
  public int getNormalImageResourceId() {
    return this.normalImageResourceId;
  }

  @JsonProperty("order")
  public int getOrder() {
    return this.order;
  }

  @JsonProperty("preview")
  public String getPreview() {
    return this.preview;
  }

  @JsonProperty("previewFilePath")
  public String getPreviewFilePath() {
    return this.previewFilePath;
  }

  @JsonProperty("purchaseId")
  public String getPurchaseId() {
    return this.purchaseId;
  }

  @JsonProperty("selectedImageResourceId")
  public int getSelectedImageResourceId() {
    return this.selectedImageResourceId;
  }

  @JsonProperty("soundId")
  public int getSoundId() {
    return this.soundId;
  }

  @JsonProperty("tag")
  public String getTag() {
    return this.tag;
  }

  @JsonProperty("tagColor")
  public String getTagColor() {
    return this.tagColor;
  }

  @JsonProperty("tagId")
  public String getTagId() {
    return this.tagId;
  }

  @JsonProperty("tagImageUrl")
  public String getTagImageUrl() {
    return this.tagImageUrl;
  }

  @JsonProperty("tagOrder")
  public int getTagOrder() {
    return this.tagOrder;
  }

  @JsonIgnore
  public boolean hasPreview() {
    return this.getPreview() != null && !this.getPreview().isEmpty();
  }

  @JsonProperty("builtIn")
  public boolean isBuiltIn() {
    return this.builtIn;
  }

  @JsonIgnore
  public boolean isDownloaded() {
    return this.getFilePath() != null && !this.getFilePath().isEmpty() && (this.getFilePath().equals("TEST_PATH") || this.fileExists(this.getFilePath()));
  }

  public boolean isFeatured() {
    return this.isFeatured;
  }

  public final boolean isFree() {
    return this.free;
  }

  @JsonIgnore
  public boolean isLocked() {
    return !FeatureManager.getInstance().hasActiveSubscription() && !this.free && !this.isFeatured;
  }

  @JsonIgnore
  public boolean isLockedWithPremiumEnabled(boolean var1) {
    return var1 && this.doesPremiumUnlockSound()?false:this.isLocked();
  }

  public boolean isNew() {
    return this.isNew;
  }

  @JsonIgnore
  public boolean isNotEqual(Sound var1) {
    return !this.getClass().equals(var1.getClass()) || this.areDifferent(this.getId(), var1.getId()) || this.areDifferent(this.getName(), var1.getName()) || this.areDifferent(this.getDescription(), var1.getDescription()) || this.areDifferent(this.getImageUri(), var1.getImageUri()) || this.areDifferent(Integer.valueOf(this.getImageResourceId()), Integer.valueOf(var1.getImageResourceId())) || this.areDifferent(Integer.valueOf(this.getSoundId()), Integer.valueOf(var1.getSoundId())) || this.areDifferent(Integer.valueOf(this.getMediaResourceId()), Integer.valueOf(var1.getMediaResourceId())) || this.areDifferent(this.getImageLargeUri(), var1.getImageLargeUri()) || this.areDifferent(this.getPreview(), var1.getPreview()) || this.areDifferent(this.getPreviewFilePath(), var1.getPreviewFilePath()) || this.areDifferent(this.getTagId(), var1.getTagId()) || this.areDifferent(this.getTag(), var1.getTag()) || this.areDifferent(this.getTagColor(), var1.getTagColor()) || this.areDifferent(this.getTagImageUrl(), var1.getTagImageUrl()) || this.areDifferent(Integer.valueOf(this.getTagOrder()), Integer.valueOf(var1.getTagOrder())) || this.areDifferent(Integer.valueOf(this.getOrder()), Integer.valueOf(var1.getOrder())) || this.areDifferent(this.getPurchaseId(), var1.getPurchaseId()) || this.areDifferent(Boolean.valueOf(this.isFree()), Boolean.valueOf(var1.isFree())) || this.areDifferent(Boolean.valueOf(this.isNew()), Boolean.valueOf(var1.isNew()));
  }

  @JsonIgnore
  public boolean isPlayable() {
    return this.mediaResourceId != 0 || this.getFilePath() != null && !this.getFilePath().isEmpty() && this.fileExists(this.getFilePath());
  }

  @JsonIgnore
  public boolean isPreviewDownloaded() {
    return this.getPreviewFilePath() != null && !this.getPreviewFilePath().isEmpty() && this.fileExists(this.getPreviewFilePath());
  }

  protected void readFromParcelExtra(Parcel var1) {
  }

  public void setAudioDuration(String var1) {
    this.audioDuration = var1;
  }

  @JsonProperty("builtIn")
  public void setBuiltIn(boolean var1) {
    this.builtIn = var1;
  }

  @JsonProperty("description")
  public void setDescription(String var1) {
    this.description = var1;
  }

  @JsonProperty("favoriteImageResourceId")
  public void setFavoriteImageResourceId(int var1) {
    this.favoriteImageResourceId = var1;
  }

  @JsonIgnore
  public void setFilePath(String var1) {
    this.filePath = var1;
  }

  @JsonProperty("id")
  public void setId(String var1) {
    this.id = var1;
  }

  @JsonProperty("imageLarge")
  public void setImageLargeUri(String var1) {
    this.imageLargeUri = var1;
  }

  @JsonProperty("imageResourceId")
  public void setImageResourceId(int var1) {
    this.imageResourceId = var1;
  }

  @JsonProperty("image")
  public void setImageUri(String var1) {
    this.imageUri = var1;
  }

  @JsonProperty("infoNormalImageResourceId")
  public void setInfoNormalImageResourceId(int var1) {
    this.infoNormalImageResourceId = var1;
  }

  @JsonProperty("infoSelectedImageResourceId")
  public void setInfoSelectedImageResourceId(int var1) {
    this.infoSelectedImageResourceId = var1;
  }

  public void setIsFree(boolean var1) {
    this.free = var1;
  }

  public void setIsNew(boolean var1) {
    this.isNew = var1;
  }

  public void setLabelResourceId(int var1) {
    this.labelResourceId = var1;
  }

  @JsonProperty("mediaResourceId")
  public void setMediaResourceId(int var1) {
    this.mediaResourceId = var1;
  }

  @JsonProperty("name")
  public void setName(String var1) {
    String var2 = var1;
    if(var1 == null) {
      var2 = "";
    }

    this.name = var2;
  }

  @JsonProperty("normalImageResourceId")
  public void setNormalImageResourceId(int var1) {
    this.normalImageResourceId = var1;
  }

  @JsonProperty("order")
  public void setOrder(int var1) {
    this.order = var1;
  }

  @JsonProperty("preview")
  public void setPreview(String var1) {
    this.preview = var1;
  }

  @JsonProperty("previewFilePath")
  public void setPreviewFilePath(String var1) {
    this.previewFilePath = var1;
  }

  @JsonProperty("purchaseId")
  public void setPurchaseId(String var1) {
    this.purchaseId = var1;
  }

  @JsonProperty("selectedImageResourceId")
  public void setSelectedImageResourceId(int var1) {
    this.selectedImageResourceId = var1;
  }

  @JsonProperty("soundId")
  public void setSoundId(int var1) {
    this.soundId = var1;
  }

  @JsonProperty("tag")
  public void setTag(String var1) {
    this.tag = var1;
  }

  @JsonProperty("tagColor")
  public void setTagColor(String var1) {
    this.tagColor = var1;
  }

  @JsonProperty("tagId")
  public void setTagId(String var1) {
    this.tagId = var1;
  }

  @JsonProperty("tagImageUrl")
  public void setTagImageUrl(String var1) {
    this.tagImageUrl = var1;
  }

  @JsonProperty("tagOrder")
  public void setTagOrder(int var1) {
    this.tagOrder = var1;
  }

  @JsonIgnore
  public boolean updateByInAppPurchase(InAppPurchase var1) {
    Object var3 = null;
    boolean var4 = this.willBeUpdatedByInAppPurchase(var1);
    if(var4) {
      this.id = var1.getIdentifier();
      this.name = var1.getName();
      this.description = var1.getDescription();
      this.preview = var1.getAudioPreview();
      this.imageUri = var1.getImagePath();
      this.imageLargeUri = var1.getImageLargePath();
      this.builtIn = false;
      this.order = var1.getOrder();
      String var2;
      if(var1.getTagObjects().isEmpty()) {
        var2 = null;
      } else {
        var2 = ((DynamicContentTag)var1.getTagObjects().get(0)).getIdentifier();
      }

      this.tagId = var2;
      if(var1.getTagObjects().isEmpty()) {
        var2 = null;
      } else {
        var2 = ((DynamicContentTag)var1.getTagObjects().get(0)).getName();
      }

      this.tag = var2;
      if(var1.getTagObjects().isEmpty()) {
        var2 = null;
      } else {
        var2 = ((DynamicContentTag)var1.getTagObjects().get(0)).getColor();
      }

      this.tagColor = var2;
      if(var1.getTagObjects().isEmpty()) {
        var2 = (String)var3;
      } else {
        var2 = this.getImageForTag((DynamicContentTag)var1.getTagObjects().get(0));
      }

      this.tagImageUrl = var2;
      int var5;
      if(var1.getTagObjects().isEmpty()) {
        var5 = -1;
      } else {
        var5 = ((DynamicContentTag)var1.getTagObjects().get(0)).getOrder();
      }

      this.tagOrder = var5;
      this.isNew = var1.isBrandNew();
      this.free = var1.isFree();
      this.audioDuration = var1.getAudioDuration();
      this.isFeatured = var1.isFeatured();
    }

    return var4;
  }

  public boolean updateByPurchase(Purchase var1) {
    boolean var2 = this.willBeUpdatedByPurchase(var1);
    if(var2) {
      this.setPurchaseId(var1.getSku());
    }

    return var2;
  }

  public void updateBySound(Sound var1) {
    if(var1.getId().equals(this.getId())) {
      this.setDescription(var1.getDescription());
      this.setName(var1.getName());
      this.setMediaResourceId(var1.getMediaResourceId());
      this.setImageResourceId(var1.getImageResourceId());
      this.setFilePath(var1.getFilePath());
      this.setPreview(var1.getPreview());
      this.setPreviewFilePath(var1.getPreviewFilePath());
      this.setImageLargeUri(var1.getImageLargeUri());
      this.setImageUri(var1.getImageUri());
      this.setSoundId(var1.getSoundId());
      this.setTagId(var1.getTagId());
      this.setTag(var1.getTag());
      this.setTagColor(var1.getTagColor());
      this.setTagImageUrl(var1.getTagImageUrl());
      this.setTagOrder(var1.getTagOrder());
      this.setOrder(var1.getOrder());
      this.setPurchaseId(var1.getPurchaseId());
      this.setIsFree(var1.isFree());
    }

  }

  public boolean willBeUpdatedByInAppPurchase(InAppPurchase var1) {
    Object var5 = null;
    if(this.id == null) {
      return true;
    } else if(this.areDifferent(this.id, var1.getIdentifier())) {
      return false;
    } else if(this.areDifferent(this.name, var1.getName())) {
      return true;
    } else if(this.areDifferent(this.description, var1.getDescription())) {
      return true;
    } else if(this.areDifferent(this.preview, var1.getAudioPreview())) {
      return true;
    } else if(this.areDifferent(this.imageUri, var1.getImagePath())) {
      return true;
    } else if(this.areDifferent(this.imageLargeUri, var1.getImageLargePath())) {
      return true;
    } else {
      String var6 = this.tagId;
      String var4;
      if(var1.getTagObjects().isEmpty()) {
        var4 = null;
      } else {
        var4 = ((DynamicContentTag)var1.getTagObjects().get(0)).getIdentifier();
      }

      if(this.areDifferent(var6, var4)) {
        return true;
      } else {
        var6 = this.tag;
        if(var1.getTagObjects().isEmpty()) {
          var4 = null;
        } else {
          var4 = ((DynamicContentTag)var1.getTagObjects().get(0)).getName();
        }

        if(this.areDifferent(var6, var4)) {
          return true;
        } else {
          var6 = this.tagColor;
          if(var1.getTagObjects().isEmpty()) {
            var4 = null;
          } else {
            var4 = ((DynamicContentTag)var1.getTagObjects().get(0)).getColor();
          }

          if(this.areDifferent(var6, var4)) {
            return true;
          } else {
            var6 = this.tagImageUrl;
            if(var1.getTagObjects().isEmpty()) {
              var4 = (String)var5;
            } else {
              var4 = ((DynamicContentTag)var1.getTagObjects().get(0)).getImageHD();
            }

            if(this.areDifferent(var6, var4)) {
              return true;
            } else {
              int var3 = this.tagOrder;
              int var2;
              if(var1.getTagObjects().isEmpty()) {
                var2 = -1;
              } else {
                var2 = ((DynamicContentTag)var1.getTagObjects().get(0)).getOrder();
              }

              return this.areDifferent(Integer.valueOf(var3), Integer.valueOf(var2))?true:(this.areDifferent(Integer.valueOf(this.order), Integer.valueOf(var1.getOrder()))?true:(this.areDifferent(Boolean.valueOf(this.free), Boolean.valueOf(var1.isFree()))?true:(this.areDifferent(Boolean.valueOf(this.isNew), Boolean.valueOf(var1.isBrandNew()))?true:this.isFeatured != var1.isFeatured())));
            }
          }
        }
      }
    }
  }

  public boolean willBeUpdatedByPurchase(Purchase var1) {
    return this.id.equals(var1.getSku()) && this.areDifferent(this.purchaseId, var1.getSku());
  }

  public void writeToParcel(Parcel var1, int var2) {
    byte var3 = 1;
    var1.writeString(this.id);
    var1.writeString(this.name);
    var1.writeString(this.description);
    var1.writeString(this.preview);
    var1.writeString(this.imageUri);
    byte var4;
    if(this.builtIn) {
      var4 = 1;
    } else {
      var4 = 0;
    }

    var1.writeInt(var4);
    var1.writeString(this.filePath);
    var1.writeString(this.previewFilePath);
    var1.writeInt(this.soundId);
    var1.writeInt(this.mediaResourceId);
    var1.writeInt(this.imageResourceId);
    var1.writeString(this.imageLargeUri);
    var1.writeString(this.tagId);
    var1.writeString(this.tag);
    var1.writeString(this.tagColor);
    var1.writeString(this.tagImageUrl);
    var1.writeInt(this.tagOrder);
    var1.writeInt(this.order);
    var1.writeString(this.purchaseId);
    if(this.isNew) {
      var4 = var3;
    } else {
      var4 = 0;
    }

    var1.writeInt(var4);
    this.writeToParcelExtra(var1);
  }

  protected void writeToParcelExtra(Parcel var1) {
  }
}
