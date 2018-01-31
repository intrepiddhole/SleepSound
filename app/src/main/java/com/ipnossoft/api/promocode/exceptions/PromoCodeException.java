package com.ipnossoft.api.promocode.exceptions;

public class PromoCodeException extends Exception {
  public PromoCodeException(String var1) {
    super(var1);
  }

  PromoCodeException(String var1, Exception var2) {
    super(var1, var2);
  }
}
