package com.ipnossoft.api.featuremanager;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class IABDeveloperPayloadGenerator {
  public IABDeveloperPayloadGenerator() {
  }

  public static String generate(String var0) {
    return hashFeatureId(var0);
  }

  private static String generateSalt(String var0) {
    byte[] var2 = var0.getBytes();

    for(int var1 = 0; var1 < var2.length; ++var1) {
      if(var2[var1] >= 65 && var2[var1] <= 90) {
        var2[var1] = (byte)(var2[var1] - 65 + 97);
      } else if(var2[var1] >= 97 && var2[var1] <= 122) {
        var2[var1] = (byte)(var2[var1] - 97 + 65);
      }
    }

    return new String(var2);
  }

  private static String hashFeatureId(String var0) {
    String var1 = generateSalt("hD8E7gfiEhdDEjOQOch87QBKjWduwh");
    return md5(md5(var0) + md5(var0 + var1) + md5(var1));
  }

  private static String md5(String var0) {
    if(var0 == null) {
      return null;
    } else {
      try {
        MessageDigest var2 = MessageDigest.getInstance("MD5");
        var2.update(var0.getBytes(), 0, var0.length());
        var0 = (new BigInteger(1, var2.digest())).toString(16);
      } catch (NoSuchAlgorithmException var3) {
        var3.printStackTrace();
        var0 = var0;
      }

      return var0;
    }
  }
}
