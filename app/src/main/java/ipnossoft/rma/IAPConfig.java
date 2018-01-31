package ipnossoft.rma;

import java.util.HashMap;
import java.util.Map;
import org.onepf.oms.SkuManager;

final class IAPConfig {
  private static final String GOOGLE_PLAY_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvfNpyKSb2ic5TRhKK6RBciTV+GmmFBCfLvzXy4i3ySX24F5/xDxj9eLgdYT6tM9rCRpgRMVSFr6PS9g4FK8WYOxzhr7hzXuiCk63DSwJFk29Uzrjk9CnkOGjJoIO+BBuaMbVVW5hMhwDh6YqmJwpOU67MNRlr1Wq+CLl6e6zpxNuiO+qWx/JSZqVOvwfM2TCaDGnFg+e5yB0Xhls1Bc26Vs/c/R3f8WmEx4NwHZv5gZybnc3x6O1Qqp+FvkXs5yv+kz3du6YdsUNfNOT6vecTYDjm7Siv4SKEPrvV/MLLpyFrpLMZqY5E8BzX19rBzGGifXucEHjHz5t1lG5KB3BGQIDAQAB";
  private static final String SKU_PREMIUM = "sku_premium";
  private static final String SKU_PREMIUM_AMAZON = "ipnossoft.rma.free.premiumfeatures";
  private static final String SKU_PREMIUM_GOOGLE = "ipnossoft.rma.free.premiumfeatures";
  private static final String SKU_PREMIUM_SAMSUNG = "100000104531/ipnossoft.rma.free.premiumfeatures";
  private static final Map<String, String> STORE_KEYS_MAP = new HashMap();

  static {
    STORE_KEYS_MAP.put("com.google.play", "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvfNpyKSb2ic5TRhKK6RBciTV+GmmFBCfLvzXy4i3ySX24F5/xDxj9eLgdYT6tM9rCRpgRMVSFr6PS9g4FK8WYOxzhr7hzXuiCk63DSwJFk29Uzrjk9CnkOGjJoIO+BBuaMbVVW5hMhwDh6YqmJwpOU67MNRlr1Wq+CLl6e6zpxNuiO+qWx/JSZqVOvwfM2TCaDGnFg+e5yB0Xhls1Bc26Vs/c/R3f8WmEx4NwHZv5gZybnc3x6O1Qqp+FvkXs5yv+kz3du6YdsUNfNOT6vecTYDjm7Siv4SKEPrvV/MLLpyFrpLMZqY5E8BzX19rBzGGifXucEHjHz5t1lG5KB3BGQIDAQAB");
    SkuManager.getInstance().mapSku("sku_premium", "com.google.play", "ipnossoft.rma.free.premiumfeatures").mapSku("sku_premium", "com.amazon.apps", "ipnossoft.rma.free.premiumfeatures").mapSku("sku_premium", "com.samsung.apps", "100000104531/ipnossoft.rma.free.premiumfeatures");
  }

  private IAPConfig() {
  }
}
