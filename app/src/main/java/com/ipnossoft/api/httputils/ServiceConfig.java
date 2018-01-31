package com.ipnossoft.api.httputils;

import com.ipnossoft.api.httputils.model.RestResource;
import java.net.MalformedURLException;

public class ServiceConfig {
  protected String apiKey;
  private String appId;
  private String appVersion;
  protected String serviceUrl;
  protected String username;

  public ServiceConfig(String var1, String var2, String var3, String var4) {
    this.validateApiParameters(var1, var2, var3, var4);
    this.setServiceUrl(var1);
    this.setApiKey(var3);
    this.setUsername(var2);
    this.setAppId(var4);
  }

  private void validateApiParameters(String var1, String var2, String var3, String var4) {
    if(var1 == null || var1.isEmpty() || var2 == null || var2.isEmpty() || var3 == null || var3.isEmpty() || var4 == null || var4.isEmpty()) {
      throw new IllegalArgumentException("All arguments are mandatory.");
    }
  }

  public String getApiBaseUrl() throws MalformedURLException {
    return URLUtils.combine(new String[]{this.serviceUrl, "api", HttpServiceApi.API_VERSION});
  }

  public String getApiKey() {
    return this.apiKey;
  }

  public String getAppBaseUrl() throws MalformedURLException {
    return URLUtils.combine(new String[]{this.serviceUrl, "api", HttpServiceApi.API_VERSION, "app", this.appId});
  }

  public String getAppId() {
    return this.appId;
  }

  public String getAppVersion() {
    return this.appVersion;
  }

  public String getAuthorizationHeaderValue() {
    return "ApiKey " + this.getUsername() + ":" + this.getApiKey();
  }

  public String getDownloadURL(String var1) throws MalformedURLException {
    return URLUtils.combine(new String[]{this.getAppBaseUrl(), "/inapppurchase", var1, "/download", "/"});
  }

  public String getResourceBaseUrl(String var1) throws MalformedURLException {
    return URLUtils.combine(new String[]{this.getAppBaseUrl(), var1});
  }

  public String getResourceUrl(RestResource var1) throws MalformedURLException {
    return URLUtils.combine(new String[]{this.getResourceBaseUrl(var1.getResourceName()), var1.getIdentifier()});
  }

  public String getServiceUrl() {
    return this.serviceUrl;
  }

  public String getUsername() {
    return this.username;
  }

  public void setApiKey(String var1) {
    this.apiKey = var1;
  }

  public void setAppId(String var1) {
    this.appId = var1;
  }

  public void setAppVersion(String var1) {
    this.appVersion = var1;
  }

  public void setServiceUrl(String var1) {
    this.serviceUrl = var1;
  }

  public void setUsername(String var1) {
    this.username = var1;
  }
}
