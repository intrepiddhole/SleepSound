//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.ipnossoft.api.newsservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;

@JsonIgnoreProperties(
        ignoreUnknown = true
)
public class News {
  @JsonProperty("date")
  private Date date;
  @JsonProperty("id")
  private int id;
  @JsonProperty("retina_image")
  private String image;
  @JsonIgnore
  private String message;
  @JsonIgnore
  private String title;
  @JsonProperty("video")
  private String videoURL;

  public News() {
  }

  public Date getDate() {
    return this.date;
  }

  public int getId() {
    return this.id;
  }

  public String getImage() {
    return this.image;
  }

  public String getMessage() {
    return this.message;
  }

  public String getTitle() {
    return this.title;
  }

  public String getVideoURL() {
    return this.videoURL;
  }

  public void setDate(Date var1) {
    this.date = var1;
  }

  public void setId(int var1) {
    this.id = var1;
  }

  public void setImage(String var1) {
    this.image = var1;
  }

  public void setMessage(String var1) {
    this.message = var1;
  }

  public void setTitle(String var1) {
    this.title = var1;
  }

  public void setVideoURL(String var1) {
    this.videoURL = var1;
  }
}
