package com.ipnossoft.api.soundlibrary.sounds;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.ArrayRes;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.ipnossoft.api.dynamiccontent.model.InAppPurchase;
import com.ipnossoft.api.soundlibrary.Sound;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(
        ignoreUnknown = true
)
@JsonInclude(Include.NON_DEFAULT)
@JsonTypeInfo(
        include = As.PROPERTY,
        property = "@class",
        use = Id.CLASS
)
public class GuidedMeditationSound extends Sound {
  private String shortDescription;

  public GuidedMeditationSound() {
  }

  public GuidedMeditationSound(String var1, int var2, String var3, String var4, int var5, String var6, String var7, String var8, String var9, int var10) {
    super(var1, -1, -1, var3, var5, true);
    this.setOrder(var10);
    this.setTagId(var6);
    this.setTag(var7);
    this.setTagColor(var8);
    this.setShortDescription(var4);
    this.setAudioDuration(var9);
  }

  public static <T extends Sound> List<T> loadSoundsFromResourceArray(Context var0, @ArrayRes int var1) {
    TypedArray var7 = var0.getResources().obtainTypedArray(var1);
    ArrayList var8 = new ArrayList();
    var1 = 0;

    while(var1 < var7.length()) {
      int var2 = var1 + 1;
      String var9 = var7.getString(var1);
      int var3 = var2 + 1;
      var7.getInt(var2, 0);
      var1 = var3 + 1;
      var2 = var7.getResourceId(var3, 0);
      int var4 = var1 + 1;
      var3 = var7.getResourceId(var1, 0);
      var1 = var4 + 1;
      var4 = var7.getResourceId(var4, 0);
      int var5 = var1 + 1;
      String var10 = var7.getString(var1);
      var1 = var5 + 1;
      String var11 = var0.getText(var7.getResourceId(var5, 0)).toString();
      var5 = var1 + 1;
      String var12 = var7.getString(var1);
      int var6 = var5 + 1;
      String var13 = var7.getString(var5);
      var1 = var6 + 1;
      var5 = var7.getInt(var6, 0);
      GuidedMeditationSound var14 = new GuidedMeditationSound(var9, -1, var0.getResources().getString(var2), var0.getResources().getString(var3), var4, var10, var11, var12, var13, var5);
      var14.setLabelResourceId(var2);
      var14.setOrder(var5);
      var8.add(var14);
    }

    var7.recycle();
    return var8;
  }

  public GuidedMeditationSound clone() throws CloneNotSupportedException {
    return (GuidedMeditationSound)super.clone();
  }

  public boolean doesPremiumUnlockSound() {
    return false;
  }

  @JsonIgnore
  public String getFilePath() {
    if(this.getMediaResourceId() == 0 && this.getId() != null) {
      String[] var1 = this.getId().split("\\.");
      if(var1.length > 0) {
        String var2 = var1[var1.length - 1];
        return currentContext.getFilesDir().getPath() + "/downloads/" + var2 + "/en.lproj/audio.caf";
      }
    }

    return super.getFilePath();
  }

  public String getShortDescription() {
    return this.shortDescription;
  }

  public int getSoundId() {
    return -1;
  }

  public void setShortDescription(String var1) {
    this.shortDescription = var1;
  }

  public void setSoundId(int var1) {
    super.setSoundId(-1);
  }

  public boolean updateByInAppPurchase(InAppPurchase var1) {
    boolean var2 = this.willBeUpdatedByInAppPurchase(var1);
    if(var2) {
      super.updateByInAppPurchase(var1);
      this.shortDescription = var1.getShortDescription();
    }

    return var2;
  }

  public boolean willBeUpdatedByInAppPurchase(InAppPurchase var1) {
    return this.areDifferent(this.shortDescription, var1.getShortDescription()) || super.willBeUpdatedByInAppPurchase(var1);
  }
}
