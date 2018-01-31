package ipnossoft.rma;

import com.appsflyer.AppsFlyerLib;
import java.util.Map;

public class RelaxMelodiesAppFree extends RelaxMelodiesApp {
  public RelaxMelodiesAppFree() {
  }

  public void completeTapJoyRewardAction() {
    AppsFlyerLib.getInstance().trackEvent(this.getApplicationContext(), "favorite_created", (Map)null);
  }

  public Class<? extends MainActivity> getMainActivityClass() {
    return MainActivityFree.class;
  }

  public void onCreate() {
    RelaxPropertyHandler.getInstance().configureRelaxPropertyReader(this.getBaseContext(), new RelaxFreePropertyReader(), "http://cdn1.ipnoscloud.com/config/rma/properties/");
    AppsFlyerLib.getInstance().setCollectIMEI(false);
    AppsFlyerLib.getInstance().setCollectAndroidID(false);
    AppsFlyerLib.getInstance().startTracking(this, "7QJd2kxs5D7G9K4nH3FkGM");
    super.onCreate();
  }
}