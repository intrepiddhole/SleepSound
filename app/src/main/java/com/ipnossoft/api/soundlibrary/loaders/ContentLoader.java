package com.ipnossoft.api.soundlibrary.loaders;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.support.annotation.Nullable;
import com.ipnossoft.api.soundlibrary.SoundLibrary;

public abstract class ContentLoader {
  SoundLibrary soundLibrary;

  ContentLoader(SoundLibrary var1) {
    this.soundLibrary = var1;
  }

  @TargetApi(11)
  private void startTask(AsyncTask var1) {
    if(VERSION.SDK_INT >= 11) {
      var1.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Object[0]);
    } else {
      var1.execute(new Object[0]);
    }
  }

  @Nullable
  protected abstract void doLoad(LoaderCallback var1);

  @Nullable
  public void loadContentWithCallback(final LoaderCallback var1) {
    this.startTask(new AsyncTask<Object, Object, Object>() {
      protected Object doInBackground(Object... var1x) {
        ContentLoader.this.doLoad(var1);
        return null;
      }
    });
  }
}
