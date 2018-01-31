package com.ipnossoft.api.httputils;

import android.util.Log;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ipnossoft.api.httputils.exceptions.ServiceError;
import com.ipnossoft.api.httputils.model.ResourcesResponse;
import com.ipnossoft.api.httputils.model.RestResource;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.net.ssl.HttpsURLConnection;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.util.EntityUtils;

public class HttpServiceApi {
  public static String API_URL = "http://relaxdev-ipnos.rhcloud.com";
  public static String API_VERSION = "v1";
  private static DefaultHttpClient httpClient;
  private ServiceConfig configuration;
  private ObjectMapper mapper;

  public HttpServiceApi(ServiceConfig var1) {
    this.setConfiguration(var1);
    if(var1.getServiceUrl().startsWith("https")) {
      X509HostnameVerifier var5 = SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
      DefaultHttpClient var2 = new DefaultHttpClient();
      SchemeRegistry var3 = new SchemeRegistry();
      SSLSocketFactory var4 = SSLSocketFactory.getSocketFactory();
      var4.setHostnameVerifier((X509HostnameVerifier)var5);
      var3.register(new Scheme("https", var4, 443));
      httpClient = new DefaultHttpClient(new SingleClientConnManager(var2.getParams(), var3), var2.getParams());
      HttpsURLConnection.setDefaultHostnameVerifier(var5);
    } else {
      httpClient = new DefaultHttpClient();
    }

    this.mapper = new ObjectMapper();
  }

  public HttpServiceApi(String var1, String var2, String var3, String var4) {
    this(new ServiceConfig(var1, var2, var3, var4));
  }

  private HttpRequestBase authorizeRequest(HttpRequestBase var1, String var2, String var3) {
    var1.setHeader("Authorization", String.format("ApiKey %s:%s", new Object[]{var2, var3}));
    return var1;
  }

  public static HttpURLConnection buildUrlConnection(String var0, String var1, String var2, String var3) throws Exception {
    try {
      HttpURLConnection var5 = (HttpURLConnection)(new URL(URLUtils.combineParams(var1, new String[]{"app_version=" + var3}))).openConnection();
      var5.setRequestMethod(var0.toUpperCase());
      var5.setUseCaches(false);
      var5.setRequestProperty("Authorization", var2);
      var5.setRequestProperty("Accept", "text/plain , application/json");
      var5.setRequestProperty("Accept-Encoding", "identity");
      var5.setRequestProperty("Connection", "close");
      var5.setReadTimeout(10000);
      var5.setConnectTimeout(10000);
      var5.setDoInput(true);
      return var5;
    } catch (Exception var4) {
      Log.e(HttpServiceApi.class.getSimpleName(), "", var4);
      throw var4;
    }
  }

  private <T extends RestResource> T parseResource(HttpResponse var1, Class<T> var2) throws ServiceError, IOException {
    String var3 = EntityUtils.toString(var1.getEntity());
    if(var1.getStatusLine().getStatusCode() == 200) {
      return (T)this.mapper.readValue(var3, var2);
    } else {
      throw (ServiceError)this.mapper.readValue(var3, ServiceError.class);
    }
  }

  private <T extends RestResource> ResourcesResponse<T> parseResources(HttpResponse var1, Class<T> var2) throws ServiceError, IOException {
    String var3 = EntityUtils.toString(var1.getEntity(), "UTF-8");
    if(var1.getStatusLine().getStatusCode() == 200) {
      return (ResourcesResponse)this.mapper.readValue(var3, this.mapper.getTypeFactory().constructParametricType(ResourcesResponse.class, new Class[]{var2}));
    } else {
      throw (ServiceError)this.mapper.readValue(var3, ServiceError.class);
    }
  }

  public HttpGet buildGetRequest(String var1) throws IOException {
    HttpGet var2 = new HttpGet(var1);
    var2.setHeader("Accept", "application/json; charset=utf-8");
    var2.setHeader("Accept-Encoding", "identity");
    var2.setHeader("Cache-Control", "no-cache");
    this.authorizeRequest(var2, this.configuration.getUsername(), this.configuration.getApiKey());
    return var2;
  }

  public HttpPost buildPostRequest(String var1, String var2) throws IOException {
    HttpPost var3 = new HttpPost(var1);
    var3.setHeader("Content-Type", var2);
    var3.setHeader("Accept", "application/json , text/plain");
    var3.setHeader("Accept-Encoding", "identity");
    this.authorizeRequest(var3, this.configuration.getUsername(), this.configuration.getApiKey());
    return var3;
  }

  public HttpResponse executeGetRequest(String var1) throws IOException {
    return this.executeRequest(this.buildGetRequest(var1));
  }

  public HttpResponse executePostRequest(String var1, String var2, String var3) throws IOException {
    HttpPost var4 = this.buildPostRequest(var1, var2);
    var4.setEntity(new StringEntity(var3, "UTF-8"));
    return this.executeRequest(var4);
  }

  public HttpResponse executeRequest(HttpRequestBase var1) throws IOException {
    return httpClient.execute(var1);
  }

  public ServiceConfig getConfiguration() {
    return this.configuration;
  }

  public <T extends RestResource> T getResource(String var1, Class<T> var2) throws ServiceError, IOException {
    return this.parseResource(this.executeGetRequest(var1), var2);
  }

  public <T extends RestResource> List<T> getResourceList(String var1, Class<T> var2) throws ServiceError, IOException {
    ArrayList var3 = new ArrayList();
    var3.addAll(this.getResourcesResponse(URLUtils.combineParams(var1, new String[]{"limit=1000", "app_version=" + this.configuration.getAppVersion(), String.valueOf((new Date()).getTime())}), var2).getResources());
    return var3;
  }

  public <T extends RestResource> ResourcesResponse<T> getResourcesResponse(String var1, Class<T> var2) throws ServiceError, IOException {
    return this.parseResources(this.executeRequest(this.buildGetRequest(var1)), var2);
  }

  public void setConfiguration(ServiceConfig var1) {
    this.configuration = var1;
  }
}
