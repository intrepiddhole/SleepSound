package com.ipnossoft.api.promocode;

import com.ipnossoft.api.promocode.client.PromoCodeListener;
import com.ipnossoft.api.promocode.exceptions.PromoCodeException;
import com.ipnossoft.api.promocode.model.PromoCodeRedeemResult;

public abstract interface PromoCodeService
{
  public abstract PromoCodeRedeemResult redeem(String paramString)
    throws PromoCodeException;
  
  public abstract void redeem(String paramString, PromoCodeListener paramPromoCodeListener);
}


/* Location:           D:\ProcessingWork\2018.01.15\SleepSound\classes2-dex2jar.jar
 * Qualified Name:     com.ipnossoft.api.promocode.PromoCodeService
 * JD-Core Version:    0.7.0.1
 */