package com.ipnossoft.api.httputils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import java.util.concurrent.atomic.AtomicInteger;

public class VolleyClient {
  private static AtomicInteger clientTag = new AtomicInteger(1);
  private static RequestQueue requestQueue;
  private String apiKey;
  private Context context;
  private int tag;
  private String username;

  public VolleyClient(Context var1) {
    this.context = var1;
    this.tag = clientTag.getAndIncrement();
    initVolley(var1);
  }

  public VolleyClient(Context var1, String var2, String var3) {
    this(var1);
    this.username = var2;
    this.apiKey = var3;
  }

  private static void initVolley(Context var0) {
    synchronized(VolleyClient.class){}

    try {
      if(requestQueue == null) {
        requestQueue = Volley.newRequestQueue(var0.getApplicationContext());
      }
    } finally {
      ;
    }

  }

  public <T> void authorizeRequest(Request<T> var1, String var2, String var3) {
    if(!TextUtils.isEmpty(var2) && !TextUtils.isEmpty(var3)) {
      //var1.getExtraHeaders().put("Authorization", String.format("ApiKey %s:%s", new Object[]{var2, var3}));
    }

    //var1.setUrl(URLUtils.combineParams(var1.getUrl(), new String[]{"app_version=6.0"}));
    Log.d("wrong", "volley library");
  }

  public void cancelPendingRequests(Object var1) {
    if(requestQueue != null) {
      requestQueue.cancelAll(var1);
    }

  }

  public <T> void executeRequest(Request<T> var1) {
    this.executeRequest(var1, Integer.valueOf(this.getTag()));
  }

  public <T> void executeRequest(Request<T> var1, Object var2) {
    Object var3 = var2;
    if(var2 == null) {
      var3 = Integer.valueOf(this.getTag());
    }

    var1.setTag(var3);
    this.authorizeRequest(var1, this.username, this.apiKey);
    this.getRequestQueue().add(var1);
  }

  public Context getContext() {
    return this.context;
  }

  public RequestQueue getRequestQueue() {
    return requestQueue;
  }

  public int getTag() {
    return this.tag;
  }
}