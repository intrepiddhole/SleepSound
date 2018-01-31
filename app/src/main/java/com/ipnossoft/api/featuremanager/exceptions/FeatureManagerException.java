package com.ipnossoft.api.featuremanager.exceptions;

import android.content.Context;

import ipnossoft.rma.free.R;

public class FeatureManagerException extends Exception {
  public static final int BILLING_RESPONSE_BAD_DEVELOPER_PAYLOAD = 100;

  public FeatureManagerException(String var1) {
    super(var1);
  }

  public FeatureManagerException(String var1, Exception var2) {
    super(var1, var2);
  }

  public static FeatureManagerException fromBillingResponseCode(int var0, Context var1) {
    switch(var0) {
      case 1:
        return new BillingCanceledException(var1.getResources().getString(R.string.billing_response_result_user_canceled));
      case 3:
        return new BillingUnavailableException(var1.getResources().getString(R.string.billing_response_result_billing_unavailable));
      case 4:
        return new BillingItemUnavailableException(var1.getResources().getString(R.string.billing_response_result_item_unavailable));
      case 6:
        return new BillingException(var1.getResources().getString(R.string.billing_response_result_error));
      case 7:
        return new FeatureOperationAlreadyCompleted(var1.getResources().getString(R.string.billing_response_result_item_already_owned));
      case 100:
        return new BillingBadDeveloperPayloadException(var1.getResources().getString(R.string.billing_response_bad_developer_payload));
      default:
        return new BillingException(var1.getResources().getString(R.string.billing_response_result_error));
    }
  }

  public static FeatureManagerException fromException(Exception var0) {
    return var0 == null?new FeatureOperationFailedException("Unknown exception."):new FeatureOperationFailedException(var0.getMessage(), var0);
  }
}
