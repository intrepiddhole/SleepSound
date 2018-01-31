package com.ipnossoft.api.soundlibrary.sounds;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.ArrayRes;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.ipnossoft.api.soundlibrary.Sound;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(
        ignoreUnknown = true
)
@JsonTypeInfo(
        include = As.PROPERTY,
        property = "@class",
        use = Id.CLASS
)
public class BinauralSound extends Sound {
  private String frequency;
  private int frequencyId;

  public BinauralSound() {
  }

  public BinauralSound(String var1, int var2, int var3, int var4, int var5, int var6, int var7, String var8, int var9, boolean var10, String var11, String var12) {
    super(var1, var2, var3, var4, var8, var9, var10);
    this.setDescription(var12);
    this.setFrequency(var11);
    this.setInfoSelectedImageResourceId(var5);
    this.setInfoNormalImageResourceId(var6);
    this.setFavoriteImageResourceId(var7);
  }

  public BinauralSound(String var1, String var2, String var3, String var4, String var5, boolean var6, String var7, String var8, int var9, int var10, int var11, String var12) {
    super(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
    this.frequency = var12;
  }

  public static <T extends Sound> List<T> loadSoundsFromResourceArray(Context var0, @ArrayRes int var1) {
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
      int var11 = var1 + 1;
      int var10 = var2.getResourceId(var1, 0);
      var1 = var11 + 1;
      var11 = var2.getResourceId(var11, 0);
      int var13 = var1 + 1;
      int var12 = var2.getResourceId(var1, 0);
      var1 = var13 + 1;
      var13 = var2.getResourceId(var13, 0);
      int var15 = var1 + 1;
      int var14 = var2.getResourceId(var1, 0);
      var1 = var15 + 1;
      var15 = var2.getResourceId(var15, 0);
      int var16 = var1 + 1;
      int var17 = var2.getResourceId(var1, 0);
      var1 = var16 + 1;
      var16 = var2.getInt(var16, 0);
      String var5 = var0.getResources().getString(var13);
      boolean var6;
      if(var14 != 0) {
        var6 = true;
      } else {
        var6 = false;
      }

      BinauralSound var18 = new BinauralSound(var4, var7, var8, var9, var10, var11, var12, var5, var14, var6, var0.getResources().getString(var15), var0.getResources().getString(var17));
      var18.setLabelResourceId(var13);
      var18.setFrequencyId(var15);
      var18.setOrder(var16);
      var3.add(var18);
    }

    var2.recycle();
    return var3;
  }

  @JsonProperty("frequency")
  public String getFrequency() {
    return this.frequency;
  }

  @JsonProperty("frequencyId")
  public int getFrequencyId() {
    return this.frequencyId;
  }

  @JsonProperty("frequency")
  public void setFrequency(String var1) {
    this.frequency = var1;
  }

  @JsonProperty("frequencyId")
  public void setFrequencyId(int var1) {
    this.frequencyId = var1;
  }
}
