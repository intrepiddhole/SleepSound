package com.ipnossoft.api.dynamiccontent.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ipnossoft.api.httputils.model.RestResource;
import java.util.HashMap;
import java.util.Locale;

@JsonIgnoreProperties(
        ignoreUnknown = true
)
public class DynamicContentTag extends RestResource {
  @JsonProperty
  private String color;
  @JsonProperty
  private int id;
  @JsonProperty
  private String identifier;
  @JsonProperty("image_retina")
  private String imageHD;
  @JsonProperty("image")
  private String imageSD;
  @JsonProperty("long_description")
  private HashMap<String, String> localizedLongDescriptions;
  @JsonProperty("name")
  private HashMap<String, String> localizedNames;
  @JsonProperty("short_description")
  private HashMap<String, String> localizedShortDescriptions;
  private String longDescription;
  private String name;
  @JsonProperty
  private int order;
  private String shortDescription;

  public DynamicContentTag() {
    super("inapppurchasetag");
  }

  private String localizeField(String var1, HashMap<String, String> var2) {
    if(var1 != null) {
      return var1;
    } else {
      String var5 = Locale.getDefault().getLanguage().toLowerCase();
      String var4 = (String)var2.get(var5);
      String var3 = var4;
      if(var4 == null) {
        var3 = var4;
        if(var5.startsWith("zh")) {
          var3 = (String)var2.get("zh_cn");
        }
      }

      if(var3 != null) {
        var1 = var3;
      } else if(var2.containsKey("en")) {
        var1 = (String)var2.get("en");
      }

      return var1;
    }
  }

  public String getColor() {
    return this.color;
  }

  public int getId() {
    return this.id;
  }

  public String getIdentifier() {
    return this.identifier;
  }

  public String getImageHD() {
    return this.imageHD;
  }

  public String getImageSD() {
    return this.imageSD;
  }

  public HashMap<String, String> getLocalizedLongDescriptions() {
    return this.localizedLongDescriptions;
  }

  public HashMap<String, String> getLocalizedNames() {
    return this.localizedNames;
  }

  public HashMap<String, String> getLocalizedShortDescriptions() {
    return this.localizedShortDescriptions;
  }

  public String getLongDescription() {
    this.longDescription = this.localizeField(this.longDescription, this.localizedLongDescriptions);
    return this.longDescription;
  }

  public String getName() {
    this.name = this.localizeField(this.name, this.localizedNames);
    return this.name;
  }

  public int getOrder() {
    return this.order;
  }

  public String getShortDescription() {
    this.shortDescription = this.localizeField(this.shortDescription, this.localizedShortDescriptions);
    return this.shortDescription;
  }

  public void setColor(String var1) {
    this.color = var1;
  }

  public void setId(int var1) {
    this.id = var1;
  }

  public void setIdentifier(String var1) {
    this.identifier = var1;
  }

  public void setImageHD(String var1) {
    this.imageHD = var1;
  }

  public void setImageSD(String var1) {
    this.imageSD = var1;
  }

  public void setLocalizedLongDescriptions(HashMap<String, String> var1) {
    this.localizedLongDescriptions = var1;
  }

  public void setLocalizedNames(HashMap<String, String> var1) {
    this.localizedNames = var1;
  }

  public void setLocalizedShortDescriptions(HashMap<String, String> var1) {
    this.localizedShortDescriptions = var1;
  }

  public void setOrder(int var1) {
    this.order = var1;
  }
}