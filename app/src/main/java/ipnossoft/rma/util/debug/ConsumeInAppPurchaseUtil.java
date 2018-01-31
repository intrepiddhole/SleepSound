package ipnossoft.rma.util.debug;

import com.ipnossoft.api.featuremanager.FeatureManager;
import org.onepf.oms.appstore.googleUtils.IabException;

public class ConsumeInAppPurchaseUtil {
  public static final String PROMO_CODE = "consume";

  public ConsumeInAppPurchaseUtil() {
  }

  public static void consumeInAppPurchases() throws IabException {
    FeatureManager.getInstance().consumeInAppPurchases();
  }
}