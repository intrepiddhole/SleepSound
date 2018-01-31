//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.ipnossoft.api.dynamiccontent.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ipnossoft.api.httputils.model.RestResource;
import java.util.List;
import java.util.Locale;

@JsonIgnoreProperties(
        ignoreUnknown = true
)
public class InAppPurchase extends RestResource {
  private String audioDuration;
  private String audioPreview;
  private boolean brandNew;
  private Integer contentVersion;
  private String description;
  private String descriptionDe;
  private String descriptionEn;
  private String descriptionEs;
  private String descriptionFr;
  private String descriptionIt;
  private String descriptionJa;
  private String descriptionKo;
  private String descriptionRu;
  private String descriptionZhCn;
  private String descriptionZhTw;
  private boolean downloadable;
  private boolean free;
  private String imageLargePath;
  private String imagePath;
  private boolean isFeatured;
  private String name;
  private String nameDe;
  private String nameEn;
  private String nameEs;
  private String nameFr;
  private String nameIt;
  private String nameJa;
  private String nameKo;
  private String nameRu;
  private String nameZhCn;
  private String nameZhTw;
  private int order;
  private String shortDescriptionDe;
  private String shortDescriptionEn;
  private String shortDescriptionEs;
  private String shortDescriptionFr;
  private String shortDescriptionIt;
  private String shortDescriptionJa;
  private String shortDescriptionKo;
  private String shortDescriptionRu;
  private String shortDescriptionZhCn;
  private String shortDescriptionZhTw;
  private boolean subscription;
  private boolean subscriptionAutoRenewable;
  private Integer subscriptionDuration;
  private int subscriptionDurationUnit = 2;
  private int subscriptionTrialDuration = -1;
  private List<DynamicContentTag> tagObjects;
  private List<String> tags;
  private String type;

  public InAppPurchase() {
    super("inapppurchase");
  }

  public String getAudioDuration() {
    return this.audioDuration;
  }

  public String getAudioPreview() {
    return this.audioPreview;
  }

  public Integer getContentVersion() {
    return this.contentVersion;
  }

  public String getDescription() {
    String var1 = Locale.getDefault().getLanguage().toLowerCase();
    return var1.equals("de") && this.descriptionDe != null && !this.descriptionDe.isEmpty()?this.descriptionDe:(var1.equals("en") && this.descriptionEn != null && !this.descriptionEn.isEmpty()?this.descriptionEn:(var1.equals("es") && this.descriptionEs != null && !this.descriptionEs.isEmpty()?this.descriptionEs:(var1.equals("fr") && this.descriptionFr != null && !this.descriptionFr.isEmpty()?this.descriptionFr:(var1.equals("it") && this.descriptionIt != null && !this.descriptionIt.isEmpty()?this.descriptionIt:(var1.equals("ja") && this.descriptionJa != null && !this.descriptionJa.isEmpty()?this.descriptionJa:(var1.equals("ko") && this.descriptionKo != null && !this.descriptionKo.isEmpty()?this.descriptionKo:(var1.equals("ru") && this.descriptionRu != null && !this.descriptionRu.isEmpty()?this.descriptionRu:(var1.equals("zh_tw") && this.descriptionZhTw != null && !this.descriptionZhTw.isEmpty()?this.descriptionZhTw:(var1.startsWith("zh") && this.descriptionZhCn != null && !this.descriptionZhCn.isEmpty()?this.descriptionZhCn:this.description)))))))));
  }

  public String getImageLargePath() {
    return this.imageLargePath;
  }

  public String getImagePath() {
    return this.imagePath;
  }

  public String getName() {
    String var1 = Locale.getDefault().getLanguage();
    return var1.equals("de") && this.nameDe != null && !this.nameDe.isEmpty()?this.nameDe:(var1.equals("en") && this.nameEn != null && !this.nameEn.isEmpty()?this.nameEn:(var1.equals("es") && this.nameEs != null && !this.nameEs.isEmpty()?this.nameEs:(var1.equals("fr") && this.nameFr != null && !this.nameFr.isEmpty()?this.nameFr:(var1.equals("it") && this.nameIt != null && !this.nameIt.isEmpty()?this.nameIt:(var1.equals("ja") && this.nameJa != null && !this.nameJa.isEmpty()?this.nameJa:(var1.equals("ko") && this.nameKo != null && !this.nameKo.isEmpty()?this.nameKo:(var1.equals("ru") && this.nameRu != null && !this.nameRu.isEmpty()?this.nameRu:(var1.equals("zh_tw") && this.nameZhTw != null && !this.nameZhTw.isEmpty()?this.nameZhTw:(var1.startsWith("zh") && this.nameZhCn != null && !this.nameZhCn.isEmpty()?this.nameZhCn:this.name)))))))));
  }

  public int getOrder() {
    return this.order;
  }

  public String getShortDescription() {
    String var1 = Locale.getDefault().getLanguage();
    return var1.equals("de") && this.shortDescriptionDe != null && !this.shortDescriptionDe.isEmpty()?this.shortDescriptionDe:(var1.equals("en") && this.shortDescriptionEn != null && !this.shortDescriptionEn.isEmpty()?this.shortDescriptionEn:(var1.equals("es") && this.shortDescriptionEs != null && !this.shortDescriptionEs.isEmpty()?this.shortDescriptionEs:(var1.equals("fr") && this.shortDescriptionFr != null && !this.shortDescriptionFr.isEmpty()?this.shortDescriptionFr:(var1.equals("it") && this.shortDescriptionIt != null && !this.shortDescriptionIt.isEmpty()?this.shortDescriptionIt:(var1.equals("ja") && this.shortDescriptionJa != null && !this.shortDescriptionJa.isEmpty()?this.shortDescriptionJa:(var1.equals("ko") && this.shortDescriptionKo != null && !this.shortDescriptionKo.isEmpty()?this.shortDescriptionKo:(var1.equals("ru") && this.shortDescriptionRu != null && !this.shortDescriptionRu.isEmpty()?this.shortDescriptionRu:(var1.equals("zh_tw") && this.shortDescriptionZhTw != null && !this.shortDescriptionZhTw.isEmpty()?this.shortDescriptionZhTw:(var1.startsWith("zh") && this.shortDescriptionZhCn != null && !this.shortDescriptionZhCn.isEmpty()?this.shortDescriptionZhCn:this.shortDescriptionEn)))))))));
  }

  public Integer getSubscriptionDuration() {
    return this.subscriptionDuration;
  }

  public int getSubscriptionDurationUnit() {
    return this.subscriptionDurationUnit;
  }

  public int getSubscriptionTrialDuration() {
    return this.subscriptionTrialDuration;
  }

  public List<DynamicContentTag> getTagObjects() {
    return this.tagObjects;
  }

  public List<String> getTags() {
    return this.tags;
  }

  public String getType() {
    return this.type;
  }

  public boolean isBrandNew() {
    return this.brandNew;
  }

  public boolean isDownloadable() {
    return this.downloadable;
  }

  public boolean isFeatured() {
    return this.isFeatured;
  }

  public boolean isFree() {
    return this.free;
  }

  public boolean isSubscription() {
    return this.subscription;
  }

  public boolean isSubscriptionAutoRenewable() {
    return this.subscriptionAutoRenewable;
  }

  @JsonProperty("duration")
  public void setAudioDuration(String var1) {
    this.audioDuration = var1;
  }

  @JsonProperty("audio_preview")
  public void setAudioPreview(String var1) {
    this.audioPreview = var1;
  }

  @JsonProperty("new")
  public void setBrandNew(boolean var1) {
    this.brandNew = var1;
  }

  @JsonProperty("content_version")
  public void setContentVersion(Integer var1) {
    this.contentVersion = var1;
  }

  @JsonProperty("description")
  public void setDescription(String var1) {
    this.description = var1;
  }

  @JsonProperty("description_de")
  public void setDescriptionDe(String var1) {
    this.descriptionDe = var1;
  }

  @JsonProperty("description_en")
  public void setDescriptionEn(String var1) {
    this.descriptionEn = var1;
  }

  @JsonProperty("description_es")
  public void setDescriptionEs(String var1) {
    this.descriptionEs = var1;
  }

  @JsonProperty("description_fr")
  public void setDescriptionFr(String var1) {
    this.descriptionFr = var1;
  }

  @JsonProperty("description_it")
  public void setDescriptionIt(String var1) {
    this.descriptionIt = var1;
  }

  @JsonProperty("description_ja")
  public void setDescriptionJa(String var1) {
    this.descriptionJa = var1;
  }

  @JsonProperty("description_ko")
  public void setDescriptionKo(String var1) {
    this.descriptionKo = var1;
  }

  @JsonProperty("description_ru")
  public void setDescriptionRu(String var1) {
    this.descriptionRu = var1;
  }

  @JsonProperty("description_zh_cn")
  public void setDescriptionZhCn(String var1) {
    this.descriptionZhCn = var1;
  }

  @JsonProperty("description_zh_tw")
  public void setDescriptionZhTw(String var1) {
    this.descriptionZhTw = var1;
  }

  @JsonProperty("downloadable")
  public void setDownloadable(boolean var1) {
    this.downloadable = var1;
  }

  @JsonProperty("free")
  public void setFree(boolean var1) {
    this.free = var1;
  }

  @JsonProperty("image_retina")
  public void setImageLargePath(String var1) {
    this.imageLargePath = var1;
  }

  @JsonProperty("image")
  public void setImagePath(String var1) {
    this.imagePath = var1;
  }

  @JsonProperty("featured")
  public void setIsFeatured(boolean var1) {
    this.isFeatured = var1;
  }

  @JsonProperty("name")
  public void setName(String var1) {
    this.name = var1;
  }

  @JsonProperty("name_de")
  public void setNameDe(String var1) {
    this.nameDe = var1;
  }

  @JsonProperty("name_en")
  public void setNameEn(String var1) {
    this.nameEn = var1;
  }

  @JsonProperty("name_es")
  public void setNameEs(String var1) {
    this.nameEs = var1;
  }

  @JsonProperty("name_fr")
  public void setNameFr(String var1) {
    this.nameFr = var1;
  }

  @JsonProperty("name_it")
  public void setNameIt(String var1) {
    this.nameIt = var1;
  }

  @JsonProperty("name_ja")
  public void setNameJa(String var1) {
    this.nameJa = var1;
  }

  @JsonProperty("name_ko")
  public void setNameKo(String var1) {
    this.nameKo = var1;
  }

  @JsonProperty("name_ru")
  public void setNameRu(String var1) {
    this.nameRu = var1;
  }

  @JsonProperty("name_zh_cn")
  public void setNameZhCn(String var1) {
    this.nameZhCn = var1;
  }

  @JsonProperty("name_zh_tw")
  public void setNameZhTw(String var1) {
    this.nameZhTw = var1;
  }

  @JsonProperty("order")
  public void setOrder(int var1) {
    this.order = var1;
  }

  @JsonProperty("short_description_de")
  public void setShortDescriptionDe(String var1) {
    this.shortDescriptionDe = var1;
  }

  @JsonProperty("short_description_en")
  public void setShortDescriptionEn(String var1) {
    this.shortDescriptionEn = var1;
  }

  @JsonProperty("short_description_es")
  public void setShortDescriptionEs(String var1) {
    this.shortDescriptionEs = var1;
  }

  @JsonProperty("short_description_fr")
  public void setShortDescriptionFr(String var1) {
    this.shortDescriptionFr = var1;
  }

  @JsonProperty("short_description_it")
  public void setShortDescriptionIt(String var1) {
    this.shortDescriptionIt = var1;
  }

  @JsonProperty("short_description_ja")
  public void setShortDescriptionJa(String var1) {
    this.shortDescriptionJa = var1;
  }

  @JsonProperty("short_description_ko")
  public void setShortDescriptionKo(String var1) {
    this.shortDescriptionKo = var1;
  }

  @JsonProperty("short_description_ru")
  public void setShortDescriptionRu(String var1) {
    this.shortDescriptionRu = var1;
  }

  @JsonProperty("short_description_zh_cn")
  public void setShortDescriptionZhCn(String var1) {
    this.shortDescriptionZhCn = var1;
  }

  @JsonProperty("short_description_zh_tw")
  public void setShortDescriptionZhTw(String var1) {
    this.shortDescriptionZhTw = var1;
  }

  @JsonProperty("subscription")
  public void setSubscription(boolean var1) {
    this.subscription = var1;
  }

  @JsonProperty("auto_renewable_subscription")
  public void setSubscriptionAutoRenewable(boolean var1) {
    this.subscriptionAutoRenewable = var1;
  }

  @JsonProperty("months_duration")
  public void setSubscriptionDuration(Integer var1) {
    this.subscriptionDuration = var1;
  }

  public void setSubscriptionDurationUnit(int var1) {
    this.subscriptionDurationUnit = var1;
  }

  @JsonProperty("subscription_duration_unit")
  public void setSubscriptionDurationUnit(String var1) {
    if(var1.equals("WEEK")) {
      this.subscriptionDurationUnit = 3;
    } else if(var1.equals("MONTH")) {
      this.subscriptionDurationUnit = 2;
    } else if(var1.equals("YEAR")) {
      this.subscriptionDurationUnit = 1;
    } else {
      this.subscriptionDurationUnit = -1;
    }
  }

  @JsonProperty("subscription_trial_duration")
  public void setSubscriptionTrialDuration(int var1) {
    this.subscriptionTrialDuration = var1;
  }

  public void setTagObjects(List<DynamicContentTag> var1) {
    this.tagObjects = var1;
  }

  @JsonProperty("tags")
  public void setTags(List<String> var1) {
    this.tags = var1;
  }

  @JsonProperty("type")
  public void setType(String var1) {
    this.type = var1;
  }
}
