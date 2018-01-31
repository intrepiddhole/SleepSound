//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.ipnossoft.api.soundlibrary.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import java.util.Iterator;

public class PermanentDataStore {
  private Context context;
  private SharedPreferences store;

  public PermanentDataStore() {
  }

  protected void clear() {
    Editor var1 = this.store.edit();
    Iterator var2 = this.store.getAll().keySet().iterator();

    while(var2.hasNext()) {
      var1.remove((String)var2.next());
    }

    var1.commit();
  }

  protected void configureDataStore(Context var1, String var2) {
    this.context = var1;
    this.store = var1.getSharedPreferences(var2, 0);
  }

  protected boolean contains(String var1) {
    return this.store.contains(var1);
  }

  public Boolean getBoolean(String var1, boolean var2) {
    synchronized(this){}

    try {
      var2 = this.store.getBoolean(var1, var2);
    } finally {
      ;
    }

    return Boolean.valueOf(var2);
  }

  public Context getContext() {
    return this.context;
  }

  public int getInteger(String var1, int var2) {
    synchronized(this){}

    try {
      var2 = this.store.getInt(var1, var2);
    } finally {
      ;
    }

    return var2;
  }

  public long getLong(String var1, long var2) {
    return this.store.getLong(var1, var2);
  }

  protected String getString(String var1) {
    return this.store.getString(var1, (String)null);
  }

  public void putBoolean(String var1, boolean var2) {
    Editor var3 = this.store.edit();
    var3.putBoolean(var1, var2);
    var3.apply();
  }

  public void putInteger(String var1, int var2) {
    Editor var3 = this.store.edit();
    var3.putInt(var1, var2);
    var3.apply();
  }

  public void putLong(String var1, long var2) {
    Editor var4 = this.store.edit();
    var4.putLong(var1, var2);
    var4.apply();
  }

  protected void putString(String var1, String var2) {
    Editor var3 = this.store.edit();
    var3.putString(var1, var2);
    var3.apply();
  }

  protected void remove(String var1) {
    Editor var2 = this.store.edit();
    var2.remove(var1);
    var2.apply();
  }

  public void setContext(Context var1) {
    this.context = var1;
  }
}
