package com.ipnossoft.api.promocode.client;

import android.os.AsyncTask;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ipnossoft.api.httputils.HttpServiceApi;
import com.ipnossoft.api.httputils.ServiceConfig;
import com.ipnossoft.api.httputils.URLUtils;
import com.ipnossoft.api.promocode.PromoCodeService;
import com.ipnossoft.api.promocode.exceptions.PromoCodeAlreadyUsedException;
import com.ipnossoft.api.promocode.exceptions.PromoCodeException;
import com.ipnossoft.api.promocode.exceptions.PromoCodeExpiredException;
import com.ipnossoft.api.promocode.exceptions.PromoCodeInactiveException;
import com.ipnossoft.api.promocode.exceptions.PromoCodeNotEffectiveException;
import com.ipnossoft.api.promocode.exceptions.PromoCodeNotFoundException;
import com.ipnossoft.api.promocode.exceptions.PromoCodeRedeemExceededException;
import com.ipnossoft.api.promocode.exceptions.PromoCodeUnexpectedException;
import com.ipnossoft.api.promocode.model.PromoCodeRedeemError;
import com.ipnossoft.api.promocode.model.PromoCodeRedeemResult;
import java.io.IOException;
import java.net.MalformedURLException;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

public class PromoCodeServiceImplementation implements PromoCodeService {
  private HttpServiceApi api;
  private ServiceConfig config;
  private ObjectMapper mapper = new ObjectMapper();

  public PromoCodeServiceImplementation(String var1, String var2, String var3, String var4) {
    this.config = new ServiceConfig(var1, var2, var3, var4);
    this.api = new HttpServiceApi(this.config);
  }

  private String buildRedeemUrl(String var1) throws MalformedURLException {
    return URLUtils.combine(new String[]{this.config.getAppBaseUrl(), "promocode", var1, "redeem", "/"});
  }

  private PromoCodeRedeemResult doRedeem(String var1) throws IOException, PromoCodeException {
    return this.parseResponse(this.executeRedeemRequest(this.buildRedeemUrl(var1)));
  }

  private HttpResponse executeRedeemRequest(String var1) throws IOException {
    return this.api.executePostRequest(var1, "application/json", "");
  }

  private void handleError(PromoCodeRedeemError var1) throws PromoCodeInactiveException, PromoCodeNotEffectiveException, PromoCodeExpiredException, PromoCodeRedeemExceededException, PromoCodeAlreadyUsedException, PromoCodeNotFoundException {
    String var2 = var1.getCode();
    String var3 = var1.getMessage();
    if(var2.equals("PROMO_CODE_NOT_ACTIVE")) {
      throw new PromoCodeInactiveException(var3);
    } else if(var2.equals("PROMO_CODE_NOT_FOUND")) {
      throw new PromoCodeNotFoundException(var3);
    } else if(var2.equals("PROMO_CODE_NOT_EFFECTIVE")) {
      throw new PromoCodeNotEffectiveException(var3);
    } else if(var2.equals("PROMO_CODE_EXPIRED")) {
      throw new PromoCodeExpiredException(var3);
    } else if(var2.equals("PROMO_CODE_REDEEM_EXCEEDED")) {
      throw new PromoCodeRedeemExceededException(var3);
    } else if(var2.equals("PROMO_CODE_ALREADY_USED")) {
      throw new PromoCodeAlreadyUsedException(var3);
    }
  }

  private PromoCodeRedeemResult parseResponse(HttpResponse var1) throws PromoCodeException, IOException {
    if(var1 != null) {
      String var3 = EntityUtils.toString(var1.getEntity());
      int var2 = var1.getStatusLine().getStatusCode();
      if(var2 == 200) {
        return (PromoCodeRedeemResult)this.mapper.readValue(var3, PromoCodeRedeemResult.class);
      }

      if(var2 == 404) {
        this.handleError((PromoCodeRedeemError)this.mapper.readValue(var3, PromoCodeRedeemError.class));
      }
    }

    throw new PromoCodeException("An unexpected error occurred while trying to redeem promo code");
  }

  public PromoCodeRedeemResult redeem(String var1) throws PromoCodeException {
    try {
      PromoCodeRedeemResult var2 = this.doRedeem(var1);
      var2.setCode(var1);
      return var2;
    } catch (IOException var3) {
      throw new PromoCodeUnexpectedException("An unexpected error occurred while trying to redeem promo code", var3);
    }
  }

  public void redeem(final String var1, final PromoCodeListener var2) {
    (new AsyncTask<String, String, Object>() {
      protected Object doInBackground(String... var1x) {
        String var3 = var1x[0];

        try {
          PromoCodeRedeemResult var4 = PromoCodeServiceImplementation.this.redeem(var3);
          return var4;
        } catch (Exception var2x) {
          return var2x;
        }
      }

      protected void onPostExecute(Object var1x) {
        if(var1x instanceof Exception) {
          var2.onFailure((Exception)var1x);
        } else {
          PromoCodeRedeemResult var2x = (PromoCodeRedeemResult)var1x;
          var2x.setCode(var1);
          var2.onSuccess(var2x);
        }
      }
    }).execute(new String[]{var1});
  }
}
