
package com.ipnossoft.api.featuremanager.iab;

import java.util.HashMap;
import java.util.Map;

public final class IABConfig {
  public static final String GOOGLE_PLAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvfNpyKSb2ic5TRhKK6RBciTV+GmmFBCfLvzXy4i3ySX24F5/xDxj9eLgdYT6tM9rCRpgRMVSFr6PS9g4FK8WYOxzhr7hzXuiCk63DSwJFk29Uzrjk9CnkOGjJoIO+BBuaMbVVW5hMhwDh6YqmJwpOU67MNRlr1Wq+CLl6e6zpxNuiO+qWx/JSZqVOvwfM2TCaDGnFg+e5yB0Xhls1Bc26Vs/c/R3f8WmEx4NwHZv5gZybnc3x6O1Qqp+FvkXs5yv+kz3du6YdsUNfNOT6vecTYDjm7Siv4SKEPrvV/MLLpyFrpLMZqY5E8BzX19rBzGGifXucEHjHz5t1lG5KB3BGQIDAQAB";
  private static final Map<String, String> storeKeys = new HashMap();

  static {
    storeKeys.put("com.google.play", "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvfNpyKSb2ic5TRhKK6RBciTV+GmmFBCfLvzXy4i3ySX24F5/xDxj9eLgdYT6tM9rCRpgRMVSFr6PS9g4FK8WYOxzhr7hzXuiCk63DSwJFk29Uzrjk9CnkOGjJoIO+BBuaMbVVW5hMhwDh6YqmJwpOU67MNRlr1Wq+CLl6e6zpxNuiO+qWx/JSZqVOvwfM2TCaDGnFg+e5yB0Xhls1Bc26Vs/c/R3f8WmEx4NwHZv5gZybnc3x6O1Qqp+FvkXs5yv+kz3du6YdsUNfNOT6vecTYDjm7Siv4SKEPrvV/MLLpyFrpLMZqY5E8BzX19rBzGGifXucEHjHz5t1lG5KB3BGQIDAQAB");
  }

  private IABConfig() {
  }

  public static Map<String, String> getStoreKeys() {
    return storeKeys;
  }
}
