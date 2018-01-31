package com.ipnossoft.api.promocode.client;

import com.ipnossoft.api.promocode.model.PromoCodeRedeemResult;

public abstract interface PromoCodeListener
{
  public abstract void onFailure(Exception paramException);
  
  public abstract void onSuccess(PromoCodeRedeemResult paramPromoCodeRedeemResult);
}


/* Location:           D:\ProcessingWork\2018.01.15\SleepSound\classes2-dex2jar.jar
 * Qualified Name:     com.ipnossoft.api.promocode.client.PromoCodeListener
 * JD-Core Version:    0.7.0.1
 */