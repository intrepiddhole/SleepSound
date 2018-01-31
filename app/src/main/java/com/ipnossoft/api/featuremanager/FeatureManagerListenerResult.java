package com.ipnossoft.api.featuremanager;

import com.ipnossoft.api.featuremanager.exceptions.FeatureManagerException;

public abstract class FeatureManagerListenerResult<T> {
  public FeatureManagerListenerResult() {
  }

  public void onCancel() {
  }

  public void onComplete() {
  }

  public void onFailure(FeatureManagerException var1) {
  }

  public void onProgressChange(double var1, int var3) {
  }

  public void onSuccess(T var1) {
  }
}
